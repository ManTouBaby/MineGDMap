package com.hrw.gdlibrary.navi;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.hrw.gdlibrary.utils.AnimationUtil;
import com.hrw.gdlibrary.GDHelper;
import com.hrw.gdlibrary.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * @author:MtBaby
 * @date:2020/03/05 13:01
 * @desc:
 */
public class DefaultMapActivity extends BaseMapActivity implements AMapNaviListener, InitListener {
    private LinearLayout mLToolbar;
    private ImageView mIvCar;
    private ImageView mIvBike;
    private ImageView mIvWalk;

    private LinearLayout mLlNavigationSchemeContainer;
    private LinearLayout mLinearLayout1;
    private LinearLayout mLinearLayout2;
    private LinearLayout mLinearLayout3;
    private TextView mTVRedGreedCount;
    private TextView mTVStartNavigation;
    private TextView tvBack;


    private List<LinearLayout> vNavigationScheme = new ArrayList<>();

    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<>();//保存当前算好的路线
    private SparseArray<AMapNaviPath> mapNaviPathSparseArray = new SparseArray<>();//保存当前算好的路线

    private int strategy;
    private GDHelper.Builder mBuilder;
    private NaviLatLng stLocation;
    private NaviLatLng endLocation;
    private SpeechSynthesizer mTts;
    private LinkedList<String> wordList = new LinkedList<>();
    private final int TTS_PLAY = 1;
    private final int CHECK_TTS_PLAY = 2;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TTS_PLAY:
                    if (mTts != null && wordList.size() > 0) {
                        mTts.startSpeaking(wordList.removeFirst(), mTtsListener);
                    }
                    break;
                case CHECK_TTS_PLAY:
                    if (!mTts.isSpeaking()) {
                        handler.obtainMessage(1).sendToTarget();
                    }
                    break;
                default:
            }

        }
    };

    @Override
    protected int createLayout() {
        return R.layout.mine_map_base_activity;
    }

    @Override
    protected void init() {
        mBuilder = (GDHelper.Builder) getIntent().getSerializableExtra("Builder");
        stLocation = getIntent().getParcelableExtra("stLocation");
        endLocation = getIntent().getParcelableExtra("endLocation");

        mIvCar = findView(R.id.mine_tv_car);
        mIvBike = findView(R.id.mine_tv_bike);
        mIvWalk = findView(R.id.mine_tv_step);

        mLinearLayout1 = findViewById(R.id.mine_ll_navigation_scheme1);
        mLinearLayout2 = findViewById(R.id.mine_ll_navigation_scheme2);
        mLinearLayout3 = findViewById(R.id.mine_ll_navigation_scheme3);
        mLToolbar = findViewById(R.id.mine_map_title);
        mLlNavigationSchemeContainer = findViewById(R.id.ll_navigation_scheme_container);
        mTVRedGreedCount = findViewById(R.id.mine_red_greed_count);
        mTVStartNavigation = findViewById(R.id.mine_start_navigation);
        mTVStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigation();
            }
        });
        tvBack = findViewById(R.id.mine_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTwo();
            }
        });

        setStatueColor(R.color.map_main_color);
        mIvCar.setSelected(true);

        mAMapNavigation.setUseInnerVoice(!mBuilder.isOpenXFYunVoice());
        if (mBuilder.isOpenXFYunVoice()) {
            // 初始化合成对象
            mTts = SpeechSynthesizer.createSynthesizer(this, this);
        }
    }

    @Override
    public void onBackPressed() {
        showTwo();
    }

    private void showTwo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("确定退出导航？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(DefaultMapActivity.this, "已退出导航", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    private void setStatueColor(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    private void startNavigation() {
        //开启导航
        setStatueColor(R.color.navigation_statue_color);
        mLToolbar.setVisibility(View.GONE);
        mLlNavigationSchemeContainer.setVisibility(View.GONE);
        mLToolbar.setAnimation(AnimationUtil.moveToViewTop());
        mLlNavigationSchemeContainer.setAnimation(AnimationUtil.moveToViewBottom());
        mAMapNavigation.startNavi(mBuilder.getNaviType());
        mAMapNavigationView.getViewOptions().setLayoutVisible(true);
        //开启讯飞语音
        setParam();

    }

    private void stopNavigation() {

    }

    @Override
    public void onGetNavigationText(String text) {
        super.onGetNavigationText(text);
        System.out.println(text);
        if (wordList != null) {
            wordList.addLast(text);
        }
        handler.obtainMessage(CHECK_TTS_PLAY).sendToTarget();
//        int code = mTts.startSpeaking(text, mTtsListener);
////			/**
////			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
////			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
////			*/
////			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
////			int code = mTts.synthesizeToUri(text, path, mTtsListener);
//
//        if (code != ErrorCode.SUCCESS) {
//            Log.d("讯飞语音合成", "语音合成失败,错误码: " + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
//        }
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
            showTip("开始播放：" + System.currentTimeMillis());
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                showTip("session id =" + sid);
            }

            //实时音频流输出参考
			/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
				byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
				Log.e("MscSpeechLog", "buf is =" + buf);
			}*/
        }
    };

    private void showTip(String plainDescription) {
        Log.d("讯飞语音合成", plainDescription);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mAMapNavigationView.getMap().clear();
        clearRout();
        mapNaviPathSparseArray.clear();
        vNavigationScheme.clear();
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        try {
            //strategyConvert(congestion-躲避拥堵, avoidhightspeed-不走高速, cost-避免收费, hightspeed-高速优先, multipleroute-多路径)
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavigation.strategyConvert(mBuilder.isCongestion(), mBuilder.isAvoidHighWay(), mBuilder.isAvoidCost(), mBuilder.isHighWay(), mBuilder.isMultipleRoute());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sList.add(stLocation);
        eList.add(endLocation);
        mAMapNavigation.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    public void navigationTypeClick(View view) {
        mIvCar.setSelected(false);
        mIvBike.setSelected(false);
        mIvWalk.setSelected(false);

        int id = view.getId();
        if (id == R.id.mine_tv_car) {
            mIvCar.setSelected(true);
            mAMapNavigation.calculateDriveRoute(sList, eList, mWayPointList, strategy);
        } else if (id == R.id.mine_tv_bike) {
            mIvBike.setSelected(true);
            mAMapNavigation.calculateRideRoute(stLocation, endLocation);
        } else if (id == R.id.mine_tv_step) {
            mIvWalk.setSelected(true);
            mAMapNavigation.calculateWalkRoute(stLocation, endLocation);
        }
    }


    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
//        System.out.println("路线规划成功:" + aMapCalcRouteResult.toString());

        clearRout();
        mapNaviPathSparseArray.clear();
        vNavigationScheme.clear();
        int[] routeid = aMapCalcRouteResult.getRouteid();
        for (int i = 0; i < routeid.length; i++) {
            AMapNaviPath naviPath = mAMapNavigation.getNaviPaths().get(routeid[i]);
            drawRoutes(routeid[i], naviPath);
        }
        if (routeOverlays.size() >= 3) {
            for (int i = 0; i < mLinearLayout3.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) mLinearLayout3.getChildAt(i);
                vNavigationScheme.add(layout);

                AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(i);
                initNavigationSchemeView(layout, naviPath, i);
            }
            mLinearLayout3.setVisibility(View.VISIBLE);
            mLinearLayout2.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.GONE);
        } else if (routeOverlays.size() == 2) {
            for (int i = 0; i < mLinearLayout2.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) mLinearLayout2.getChildAt(i);
                vNavigationScheme.add(layout);

                AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(i);
                initNavigationSchemeView(layout, naviPath, i);
            }
            mLinearLayout2.setVisibility(View.VISIBLE);
            mLinearLayout3.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < mLinearLayout1.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) mLinearLayout1.getChildAt(i);
                vNavigationScheme.add(layout);

                AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(i);
                initNavigationSchemeView(layout, naviPath, i);
            }
            mLinearLayout3.setVisibility(View.GONE);
            mLinearLayout2.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.VISIBLE);
        }

        selectLine(0);
    }

    @SuppressLint("SetTextI18n")
    private void initNavigationSchemeView(LinearLayout layout, AMapNaviPath naviPath, final int index) {
        TextView tvTime = layout.findViewById(R.id.mine_time);
        TextView tvdDistance = layout.findViewById(R.id.mine_distance);
        tvTime.setText(naviPath.getAllTime() / 60 + "分钟");
        float distance = naviPath.getAllLength() / 1000f;
        distance = (float) (Math.round(distance * 10) / 10.0);
        tvdDistance.setText(distance + "公里");

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLine(index);
            }
        });
    }

    private void initView() {

    }

    private void clearRout() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            routeOverlays.valueAt(i).removeFromMap();
        }
        routeOverlays.clear();
    }

    private void selectLine(int index) {
        for (int i = 0; i < vNavigationScheme.size(); i++) {
            vNavigationScheme.get(i).setSelected(false);
        }


        AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(index);
        if (naviPath != null && naviPath.getLightList() != null) {
            String light = naviPath.getLightList().size() > 0 ? "红绿灯" + naviPath.getLightList().size() + "个" : "红绿灯0个";
            mTVRedGreedCount.setText(light);
        }
        vNavigationScheme.get(index).setSelected(true);
        mAMapNavigation.selectRouteId(routeOverlays.keyAt(index));
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        AMap aMap = mAMapNavigationView.getMap();
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(true);
        if (mBuilder.getStIcon() != 0)
            routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), mBuilder.getStIcon()));
        if (mBuilder.getEndIcon() != 0)
            routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), mBuilder.getEndIcon()));
        routeOverLay.addToMap();
        routeOverLay.zoomToSpan(120);
        routeOverlays.put(routeId, routeOverLay);
        mapNaviPathSparseArray.put(routeId, path);
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        System.out.println("路线规划失败:" + aMapCalcRouteResult.getErrorDetail());
    }


    @Override
    public void onInit(int code) {
        Log.d("讯飞语音初始化", "InitListener init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            Log.d("讯飞语音初始化", "初始化失败,错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        } else {
            // 初始化成功，之后可以调用startSpeaking方法
            // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
            // 正确的做法是将onCreate中的startSpeaking调用移至这里
        }
    }

    public static String voicerXtts = "xiaoyan";

    /**
     * 参数设置
     */
    private void setParam() {
        if (mTts == null) return;
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
//        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD))
//        {
//            //设置使用云端引擎
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//            //设置发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);
//
//        }else if(mEngineType.equals(SpeechConstant.TYPE_LOCAL)){
//            //设置使用本地引擎
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//            //设置发音人资源路径
//            mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
//            //设置发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);
//        }else{
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
//            //设置发音人资源路径
//            mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
//            //设置发音人
//            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerXtts);
//        }

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        //设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerXtts);
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, mBuilder.getXfYunOption().getSPEED());
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, mBuilder.getXfYunOption().getPITCH());
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, mBuilder.getXfYunOption().getVOLUME());
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mBuilder.getXfYunOption().getSTREAM_TYPE());
        //	mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");


    }

    //获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        String type = "xtts";

        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type + "/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + voicerXtts + ".jet"));
//        if(mEngineType.equals(SpeechConstant.TYPE_XTTS)){
//            tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type+"/"+TtsDemo.voicerXtts+".jet"));
//        }else {
//            tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + TtsDemo.voicerLocal + ".jet"));
//        }

        return tempBuffer.toString();
    }
}
