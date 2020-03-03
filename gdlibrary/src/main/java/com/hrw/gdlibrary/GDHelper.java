package com.hrw.gdlibrary;

import android.content.Context;
import android.content.Intent;

import com.amap.api.navi.AMapNavi;
import com.hrw.gdlibrary.navi.activity.DefaultMapActivity;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 9:50
 * @desc:
 */
public class GDHelper {
    private Builder builder;

    private GDHelper(Builder builder) {
    }


    public void addObserver() {

    }

    public void openNavigation(Context context) {
        Intent intent = new Intent(context, DefaultMapActivity.class);
        context.startActivity(intent);
    }

    public static class Builder {
        //        注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
        private String apiKey = "";
        private boolean isCongestion = true;//是否避免拥堵
        private boolean isAvoidHighWay = false;//是否避免高速
        private boolean isAvoidCost = false;//是否避免收费
        private boolean isHighWay = false;//是否高速优先
        private boolean isMultipleroute = false;//是否启动多路径

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


        public Builder setMultipleroute(boolean multipleroute) {
            isMultipleroute = multipleroute;
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

        public boolean isMultipleroute() {
            return isMultipleroute;
        }

        public GDHelper build(Context context) {
            if (apiKey == null) throw new NullPointerException("must setApiKey() before build");
            AMapNavi.setApiKey(context, apiKey);
            return new GDHelper(this);
        }
    }
}
