package com.hy.gdlibrary.track;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnCustomAttributeListener;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.query.model.AddTerminalRequest;
import com.amap.api.track.query.model.AddTerminalResponse;
import com.amap.api.track.query.model.AddTrackRequest;
import com.amap.api.track.query.model.AddTrackResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;

import com.hy.gdlibrary.GDHelper;
import com.hy.gdlibrary.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MtHappy
 * Date: 2020/7/23 15:20
 * Description: 后台实时上传轨迹
 */
public class MapTrackService extends Service implements OnCustomAttributeListener {
    public static String controlTypeTag = "controlType";  //0:开启上传   1:关闭上传
    public static String terminalNameTag = "terminalName";  //0:开启上传   1:关闭上传
    private AMapTrackClient aMapTrackClient;
    private GDHelper mGdHelper;
    private String mTerminalName;
    private long mTerminalId;//通过终端名称获取的终端ID
    private long mTrackId;//轨迹ID

    @Override
    public void onCreate() {
        super.onCreate();
        mGdHelper = GDHelper.getInstance();
        aMapTrackClient = mGdHelper.getMapTrackManger().getAMapTrackClient();
        aMapTrackClient.setInterval(5, 30);
        aMapTrackClient.setOnCustomAttributeListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int control = intent.getIntExtra(controlTypeTag, 0);
        mTerminalName = intent.getStringExtra(terminalNameTag);
        switch (control) {
            case 0://开启上传
                startTrack();
                break;
            case 1://关闭上传
                aMapTrackClient.stopTrack(new TrackParam(mGdHelper.getServiceId(), mTerminalId), onTrackListener);
                aMapTrackClient.stopGather(onTrackListener);
                break;
        }
        return START_STICKY_COMPATIBILITY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void startTrack() {
        // 先根据Terminal名称查询Terminal ID，如果Terminal还不存在，就尝试创建，拿到Terminal ID后，
        // 用Terminal ID开启轨迹服务
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(mGdHelper.getServiceId(), mTerminalName), new SimpleOnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()) {
                    if (queryTerminalResponse.isTerminalExist()) {
                        // 当前终端已经创建过，直接使用查询到的terminal id
                        mTerminalId = queryTerminalResponse.getTid();
                        aMapTrackClient.addTrack(new AddTrackRequest(mGdHelper.getServiceId(), mTerminalId), new SimpleOnTrackListener() {
                            @Override
                            public void onAddTrackCallback(AddTrackResponse addTrackResponse) {
                                if (addTrackResponse.isSuccess()) {
                                    // trackId需要在启动服务后设置才能生效，因此这里不设置，而是在startGather之前设置了track id
                                    mTrackId = addTrackResponse.getTrid();
                                    Log.i("MapTrackService---->", "创建新轨迹成功:" + mTrackId);
                                    TrackParam trackParam = new TrackParam(mGdHelper.getServiceId(), mTerminalId);
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        trackParam.setNotification(createNotification());
                                    }
                                    aMapTrackClient.startTrack(trackParam, onTrackListener);
                                } else {
                                    Log.i("MapTrackService---->", "网络请求失败，" + addTrackResponse.getErrorMsg());
                                }
                            }
                        });
                    } else {
                        // 当前终端是新终端，还未创建过，创建该终端并使用新生成的terminal id
                        aMapTrackClient.addTerminal(new AddTerminalRequest(mTerminalName, mGdHelper.getServiceId()), new SimpleOnTrackListener() {
                            @Override
                            public void onCreateTerminalCallback(AddTerminalResponse addTerminalResponse) {
                                if (addTerminalResponse.isSuccess()) {
                                    mTerminalId = addTerminalResponse.getTid();
                                    TrackParam trackParam = new TrackParam(mGdHelper.getServiceId(), mTerminalId);
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        trackParam.setNotification(createNotification());
                                    }
                                    aMapTrackClient.startTrack(trackParam, onTrackListener);
                                } else {
                                    Log.i("MapTrackService---->", "网络请求失败，" + addTerminalResponse.getErrorMsg());
                                }
                            }
                        });
                    }
                } else {
                    Log.i("MapTrackService---->", "网络请求失败，" + queryTerminalResponse.getErrorMsg());
                }
            }
        });
    }

    /**
     * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
     * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(getPackageName(), "app service", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
            builder = new Notification.Builder(getApplicationContext(), getPackageName());
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        Intent nfIntent = new Intent(this, MapTrackService.class);
        nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setSmallIcon(R.mipmap.icon_location)
                .setContentTitle("猎鹰sdk运行中")
                .setContentText("猎鹰sdk运行中");
        Notification notification = builder.build();
        return notification;
    }

    private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {
        @Override
        public void onBindServiceCallback(int status, String msg) {
            Log.i("MapTrackService---->", "onBindServiceCallback, status: " + status + ", msg: " + msg);
        }

        @Override
        public void onStartTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
                // 成功启动
                Log.i("MapTrackService---->", "启动服务成功->开始上传轨迹" + mTrackId);
                aMapTrackClient.setTrackId(mTrackId);
                aMapTrackClient.startGather(onTrackListener);
            } else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                // 已经启动
                Log.i("MapTrackService---->", "服务已经启动");
                aMapTrackClient.setTrackId(mTrackId);
                aMapTrackClient.startGather(onTrackListener);
            } else {
                Log.i("MapTrackService---->", "error onStartTrackCallback, status: " + status + ", msg: " + msg);
            }
        }

        @Override
        public void onStopTrackCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
                Log.i("MapTrackService---->", "停止服务成功");
            } else {
                Log.i("MapTrackService---->", "error onStopTrackCallback, status: " + status + ", msg: " + msg);
            }
        }

        @Override
        public void onStartGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) {
                Log.i("MapTrackService---->", "定位采集开启成功");
            } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
                Log.i("MapTrackService---->", "定位采集已经开启");
            } else {
                Log.i("MapTrackService---->", "error onStartGatherCallback, status: " + status + ", msg: " + msg);
            }
        }

        @Override
        public void onStopGatherCallback(int status, String msg) {
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
                Log.i("MapTrackService---->", "定位采集停止成功");
            } else {
                Log.i("MapTrackService---->", "error onStopGatherCallback, status: " + status + ", msg: " + msg);
            }
        }
    };

    @Override
    public Map<String, String> onTrackAttributeCallback() {
        Map<String,String> params = new HashMap<>();
        params.put("TerminalName",mTerminalName);
        return null;
    }
}
