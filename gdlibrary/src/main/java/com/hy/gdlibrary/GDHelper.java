package com.hy.gdlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.track.AMapTrackClient;
import com.hy.gdlibrary.fragment.LocationFragment;
import com.hy.gdlibrary.fragment.MultipointFragment;
import com.hy.gdlibrary.location.LocationManager;
import com.hy.gdlibrary.navigation.DefaultMapActivity;
import com.hy.gdlibrary.nearby.NearbyMapActivity;
import com.hy.gdlibrary.track.MapTrack;
import com.hy.gdlibrary.track.MapTrackManger;
import com.hy.gdlibrary.track.MapTrackService;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 9:50
 * @desc:
 */
public class GDHelper {
    private static GDHelper mGdHelper;
    private static Builder mBuilder;

    public static GDHelper getInstance() {
        return mGdHelper;
    }

    private GDHelper(Builder builder) {
        mBuilder = builder;
    }

    private static GDHelper init(Builder builder) {
        if (mGdHelper == null) {
            synchronized (GDHelper.class) {
                if (mGdHelper == null) {
                    mGdHelper = new GDHelper(builder);
                }
            }
        }
        return mGdHelper;
    }


    public MapTrackManger getMapTrackManger() {
        return mBuilder.mMapTrackManger;
    }

    public int getServiceId() {
        return mBuilder.serviceId;
    }

    public void startSingleLocation(AMapLocationListener aMapLocationListener) {
        mBuilder.mLocationManager.startSingleLocation(aMapLocationListener);
    }

    public void startContinueLocation(AMapLocationListener aMapLocationListener) {
        mBuilder.mLocationManager.startContinueLocation(aMapLocationListener);
    }

    public void closeLocationListener(AMapLocationListener aMapLocationListener) {
        mBuilder.mLocationManager.closeLocationListener(aMapLocationListener);
    }

    //打开导航界面
    public void openNavigation(Context context, NavigationClient navigationClient, NaviLatLng stLocation, NaviLatLng endLocation) {
        Intent intent = new Intent(context, DefaultMapActivity.class);
        intent.putExtra("NavigationClient", navigationClient);
        intent.putExtra("stLocation", stLocation);
        intent.putExtra("endLocation", endLocation);
        context.startActivity(intent);
    }

    //打开导航界面
    public void openNavigation(Context context, NaviLatLng stLocation, NaviLatLng endLocation) {
        NavigationClient navigationClient = new NavigationClient(context);
        openNavigation(context, navigationClient, stLocation, endLocation);
    }

    //打开附近搜索
    public void openNearby(Activity context, int requestCode) {
        Intent intent = new Intent(context, NearbyMapActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public void uploadTrack(Context context, String terminalName) {
        if (mBuilder.serviceId == 0) {
            Toast.makeText(context, "调用轨迹上传之前，需要设置ServiceID", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, MapTrackService.class);
        intent.putExtra(MapTrackService.controlTypeTag, 0);
        intent.putExtra(MapTrackService.terminalNameTag, terminalName);
        context.startService(intent);
    }

    public void colseUploadTrack(Context context, String terminalName) {
        if (mBuilder.serviceId == 0) {
            Toast.makeText(context, "关闭轨迹上传之前，需要设置ServiceID", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, MapTrackService.class);
        intent.putExtra(MapTrackService.controlTypeTag, 1);
        intent.putExtra(MapTrackService.terminalNameTag, terminalName);
        context.startService(intent);
    }

    /**
     * 打开轨迹回放
     */
    public void openMapTrack(Activity context, @NonNull String startLocal, @NonNull String endLocal, List<LatLng> points) {
        Intent intent = new Intent(context, MapTrack.class);
        intent.putExtra("startLocal", startLocal);
        intent.putExtra("endLocal", endLocal);
        if (points != null) intent.putExtra("points", new ArrayList<>(points));
        context.startActivity(intent);
    }

    public LocationFragment getLocalFragment() {
        return new LocationFragment();
    }


    public MultipointFragment getMultipointFragment() {
        return new MultipointFragment();
    }

    public static class Builder {
        private int serviceId;//服务的唯一编号
        private LocationManager mLocationManager;
        private MapTrackManger mMapTrackManger;
        private boolean isOpenLocal;//是否开启定位功能
        private boolean isOpenTrack;//是否轨迹功能


        public Builder setServiceId(int serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder setOpenTrack(boolean openTrack) {
            isOpenTrack = openTrack;
            return this;
        }

        public Builder setOpenLocal(boolean openLocal) {
            isOpenLocal = openLocal;
            return this;
        }

        public GDHelper build(Context context) {
            if (isOpenLocal) mLocationManager = LocationManager.getInstance(context);
            if (isOpenTrack) {
                mMapTrackManger = MapTrackManger.getInstance(context.getApplicationContext());
            }
            return new GDHelper(this);
        }
    }

    public static class NavigationClient implements Serializable {
        // 注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
        private boolean isCongestion = true;//是否避免拥堵
        private boolean isAvoidHighWay = false;//是否避免高速
        private boolean isAvoidCost = false;//是否避免收费
        private boolean isHighWay = false;//是否高速优先
        private boolean isMultipleRoute = false;//是否启动多路径
        private int mNaviType = NaviType.GPS;//开启导航时类型
        private int stIcon = 0;
        private int endIcon = 0;

        private boolean isOpenXFYunVoice = false;//是否开启讯飞语音播放
        private String xFYunId = "5bee8844";//测试使用ID
        private XFYunOption xfYunOption = new XFYunOption();

        public int getNaviType() {
            return mNaviType;
        }


        public NavigationClient setNaviType(int naviType) {
            this.mNaviType = naviType;
            return this;
        }

        public XFYunOption getXfYunOption() {
            return xfYunOption;
        }

        public int getStIcon() {
            return stIcon;
        }

        public NavigationClient setStIcon(@DrawableRes int stIcon) {
            this.stIcon = stIcon;
            return this;
        }

        public int getEndIcon() {
            return endIcon;
        }

        public NavigationClient setEndIcon(@DrawableRes int endIcon) {
            this.endIcon = endIcon;
            return this;
        }

        public NavigationClient setXfYunOption(XFYunOption xfYunOption) {
            this.xfYunOption = xfYunOption;
            return this;
        }

        public NavigationClient setAvoidHighWay(boolean avoidHighWay) {
            isAvoidHighWay = avoidHighWay;
            if (isAvoidHighWay && isHighWay) {
                throw new IllegalArgumentException("AvoidHighWay and highWay cannot be set to true at the same time");
            }
            return this;
        }

        public NavigationClient setHighWay(boolean highWay) {
            isHighWay = highWay;
            if (isAvoidHighWay && isHighWay) {
                throw new IllegalArgumentException("AvoidHighWay and highWay cannot be set to true at the same time");
            }
            return this;
        }

        public NavigationClient setCongestion(boolean congestion) {
            isCongestion = congestion;
            return this;
        }

        public NavigationClient setAvoidCost(boolean avoidCost) {
            isAvoidCost = avoidCost;
            return this;
        }


        public NavigationClient setMultipleRoute(boolean multipleroute) {
            isMultipleRoute = multipleroute;
            return this;
        }

        public boolean isOpenXFYunVoice() {
            return isOpenXFYunVoice;
        }

        public NavigationClient setOpenXFYunVoice(boolean openXFYunVoice) {
            isOpenXFYunVoice = openXFYunVoice;
            return this;
        }

        public String getxFYunId() {
            return xFYunId;
        }

        public NavigationClient setxFYunId(String xFYunId) {
            this.xFYunId = xFYunId;
            return this;
        }

        public boolean isCongestion() {
            return isCongestion;
        }

        public boolean isAvoidHighWay() {
            return isAvoidHighWay;
        }

        public boolean isAvoidCost() {
            return isAvoidCost;
        }

        public boolean isHighWay() {
            return isHighWay;
        }

        public boolean isMultipleRoute() {
            return isMultipleRoute;
        }

        public NavigationClient(Context context) {
            if (isOpenXFYunVoice) {
                StringBuffer param = new StringBuffer();
                param.append("appid=" + xFYunId);
                param.append(",");
                // 设置使用v5+
                param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
                SpeechUtility.createUtility(context, param.toString());
            }
        }
    }
}
