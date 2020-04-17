package com.hrw.gdlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.hrw.gdlibrary.location.LocationManager;
import com.hrw.gdlibrary.navi.DefaultMapActivity;
import com.hrw.gdlibrary.nearby.NearbyMapActivity;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.io.Serializable;


/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 9:50
 * @desc:
 */
public class GDHelper {
    private LocationManager mLocationManager;
    private Builder mBuilder;

    private GDHelper(Builder builder, LocationManager mLocationManager) {
        mBuilder = builder;
        this.mLocationManager = mLocationManager;
    }


    public void startSingleLocation(AMapLocationListener aMapLocationListener) {
        mLocationManager.startSingleLocation(aMapLocationListener);
    }

    public void startContinueLocation(AMapLocationListener aMapLocationListener) {
        mLocationManager.startContinueLocation(aMapLocationListener);
    }

    public void openNavigation(Context context, NaviLatLng stLocation, NaviLatLng endLocation) {
        Intent intent = new Intent(context, DefaultMapActivity.class);
        intent.putExtra("Builder", mBuilder);
        intent.putExtra("stLocation", stLocation);
        intent.putExtra("endLocation", endLocation);
        context.startActivity(intent);
    }

    public void openNearby(Activity context, int requestCode) {
        Intent intent = new Intent(context, NearbyMapActivity.class);
        intent.putExtra("Builder", mBuilder);
        context.startActivityForResult(intent, requestCode);
    }

    public static class Builder implements Serializable {
        // 注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
        private String apiKey = "";
        private boolean isCongestion = true;//是否避免拥堵
        private boolean isAvoidHighWay = false;//是否避免高速
        private boolean isAvoidCost = false;//是否避免收费
        private boolean isHighWay = false;//是否高速优先
        private boolean isMultipleRoute = false;//是否启动多路径

        private boolean isOpenXFYunVoice = false;//是否开启讯飞语音播放
        private String xFYunId = "5bee8844";//测试使用ID
        private XFYunOption xfYunOption = new XFYunOption();


        private int stIcon = 0;
        private int endIcon = 0;
        private int mNaviType = NaviType.GPS;//开启导航时类型


        public XFYunOption getXfYunOption() {
            return xfYunOption;
        }

        public Builder setXfYunOption(XFYunOption xfYunOption) {
            this.xfYunOption = xfYunOption;
            return this;
        }


        public boolean isOpenXFYunVoice() {
            return isOpenXFYunVoice;
        }

        public Builder setOpenXFYunVoice(boolean openXFYunVoice) {
            isOpenXFYunVoice = openXFYunVoice;
            return this;
        }

        public String getxFYunId() {
            return xFYunId;
        }

        public Builder setxFYunId(String xFYunId) {
            this.xFYunId = xFYunId;
            return this;
        }

        public int getNaviType() {
            return mNaviType;
        }

        public Builder setNaviType(int naviType) {
            this.mNaviType = naviType;
            return this;
        }

        public int getStIcon() {
            return stIcon;
        }

        public Builder setStIcon(@DrawableRes int stIcon) {
            this.stIcon = stIcon;
            return this;
        }

        public int getEndIcon() {
            return endIcon;
        }

        public Builder setEndIcon(@DrawableRes int endIcon) {
            this.endIcon = endIcon;
            return this;
        }

        public String getApiKey() {
            return apiKey;
        }


        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder setAvoidHighWay(boolean avoidHighWay) {
            isAvoidHighWay = avoidHighWay;
            if (isAvoidHighWay && isHighWay) {
                throw new IllegalArgumentException("AvoidHighWay and highWay cannot be set to true at the same time");
            }
            return this;
        }

        public Builder setHighWay(boolean highWay) {
            isHighWay = highWay;
            if (isAvoidHighWay && isHighWay) {
                throw new IllegalArgumentException("AvoidHighWay and highWay cannot be set to true at the same time");
            }
            return this;
        }

        public Builder setCongestion(boolean congestion) {
            isCongestion = congestion;
            return this;
        }

        public Builder setAvoidCost(boolean avoidCost) {
            isAvoidCost = avoidCost;
            return this;
        }


        public Builder setMultipleRoute(boolean multipleroute) {
            isMultipleRoute = multipleroute;
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

        public GDHelper build(Context context) {
            if (apiKey == null) throw new NullPointerException("must setApiKey() before build");
            AMapNavi.setApiKey(context, apiKey);

            if (isOpenXFYunVoice) {
                StringBuffer param = new StringBuffer();
                param.append("appid=" + xFYunId);
                param.append(",");
                // 设置使用v5+
                param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
                SpeechUtility.createUtility(context, param.toString());
            }
            return new GDHelper(this, LocationManager.getInstance(context));
        }
    }
}
