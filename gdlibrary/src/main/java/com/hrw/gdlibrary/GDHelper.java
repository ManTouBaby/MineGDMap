package com.hrw.gdlibrary;

import android.content.Context;
import android.content.Intent;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.NaviLatLng;
import com.hrw.gdlibrary.navi.DefaultMapActivity;

import java.io.Serializable;

import androidx.annotation.DrawableRes;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 9:50
 * @desc:
 */
public class GDHelper {
    private Builder mBuilder;

    private GDHelper(Builder builder) {
        mBuilder = builder;
    }


    public void addObserver() {

    }


    public void openNavigation(Context context, NaviLatLng stLocation, NaviLatLng endLocation) {
        Intent intent = new Intent(context, DefaultMapActivity.class);
        intent.putExtra("Builder", mBuilder);
        intent.putExtra("stLocation", stLocation);
        intent.putExtra("endLocation", endLocation);
        context.startActivity(intent);
    }

    public static class Builder implements Serializable {
        // 注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
        private String apiKey = "";
        private boolean isCongestion = true;//是否避免拥堵
        private boolean isAvoidHighWay = false;//是否避免高速
        private boolean isAvoidCost = false;//是否避免收费
        private boolean isHighWay = false;//是否高速优先
        private boolean isMultipleRoute = false;//是否启动多路径

        private int stIcon = 0;
        private int endIcon = 0;

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
            return new GDHelper(this);
        }
    }
}
