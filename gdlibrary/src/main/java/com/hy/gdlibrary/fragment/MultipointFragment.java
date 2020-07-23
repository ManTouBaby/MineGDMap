package com.hy.gdlibrary.fragment;

import android.os.Bundle;
import android.view.animation.LinearInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.hy.gdlibrary.base.MapBaseFragment;
import com.hy.gdlibrary.base.MultipointBo;
import com.hy.gdlibrary.utils.MarkerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MtHappy
 * Date: 2020/7/22 14:36
 * Description: 用于展示多点数据
 */
public class MultipointFragment extends MapBaseFragment implements AMap.OnMarkerClickListener {
    private String DATES = "DATES";
    ArrayList<MultipointBo> multipointBos;
    Map<Object, Marker> objectMarkerMap = new HashMap<>();
    private AnimationSet mAnimationSet;
    private OnMarkerClickListener mOnMarkerClickListener;

    @Override
    protected void initDates() {
        mAMap.setOnMarkerClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            multipointBos = (ArrayList<MultipointBo>) bundle.getSerializable(DATES);
            initData();
        }

    }

    private void initData() {
        double stLatitude = 0;
        double stLongitude = 0;
        double endLatitude = 0;
        double endLongitude = 0;
        for (MultipointBo multipointBo : multipointBos) {
            if (stLatitude == 0) {
                stLatitude = endLatitude = multipointBo.getLatitude();
                stLongitude = endLongitude = multipointBo.getLongitude();
            }
            if (multipointBo.getLatitude() < stLatitude) {
                stLatitude = multipointBo.getLatitude();
            }
            if (multipointBo.getLongitude() < stLongitude) {
                stLongitude = multipointBo.getLongitude();
            }

            if (multipointBo.getLatitude() > endLatitude) {
                endLatitude = multipointBo.getLatitude();
            }
            if (multipointBo.getLongitude() > endLongitude) {
                endLongitude = multipointBo.getLongitude();
            }

            Marker marker = objectMarkerMap.get(multipointBo.getTagObject());
            LatLng latLng = new LatLng(multipointBo.getLatitude(), multipointBo.getLongitude());
            if (marker == null) {
                marker = createMarker(latLng, multipointBo.getTagObject(), multipointBo.getLocationIcon());
                objectMarkerMap.put(multipointBo.getTagObject(), marker);
            } else {
                marker.setPosition(latLng);
            }
        }
        LatLng stLatLng = new LatLng(stLatitude, stLongitude);
        LatLng endLatLng = new LatLng(endLatitude, endLongitude);
        LatLngBounds latLngBounds = new LatLngBounds(stLatLng, endLatLng);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 88));
    }


    public void updateMultipointBos(List<MultipointBo> multipointBos) {
        if (mAMap == null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DATES, new ArrayList<>(multipointBos));
            setArguments(bundle);
        } else {
            this.multipointBos = new ArrayList<>(multipointBos);
            initData();
        }
    }

    private Marker createMarker(LatLng latLng, Object objectTag, int markerIcon) {
        Marker marker = mAMap.addMarker(MarkerUtil.getMarker(latLng, 0.5f, 1f, markerIcon));
        marker.setObject(objectTag);
        return marker;
    }

    @Override
    protected int getMapLayout() {
        return 0;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mAnimationSet != null) mAnimationSet.cleanAnimation();
        marker.setAnimation(getAnimation());
        marker.startAnimation();
        if (mOnMarkerClickListener != null) {
            mOnMarkerClickListener.onMarkerClick(marker);
        }
        return false;
    }

    private AnimationSet getAnimation() {
        // 动画执行完成后，默认会保持到最后一帧的状态
        mAnimationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.2f);
        alphaAnimation.setDuration(2000);
        // 设置不断重复
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 2, 1, 2);
        scaleAnimation.setDuration(2000);
        // 设置不断重复
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        mAnimationSet.addAnimation(alphaAnimation);
        mAnimationSet.addAnimation(scaleAnimation);
        mAnimationSet.setInterpolator(new LinearInterpolator());
        return mAnimationSet;
    }

    public void setOnMarkerClickListener(OnMarkerClickListener mOnMarkerClickListener) {
        this.mOnMarkerClickListener = mOnMarkerClickListener;
    }

    public interface OnMarkerClickListener {
        void onMarkerClick(Marker marker);
    }
}
