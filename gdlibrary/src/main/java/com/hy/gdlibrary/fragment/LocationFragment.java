package com.hy.gdlibrary.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.AnimationSet;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.hy.gdlibrary.R;
import com.hy.gdlibrary.base.MapBaseFragment;
import com.hy.gdlibrary.location.LocationManager;

public class LocationFragment extends MapBaseFragment implements View.OnClickListener {
    private LatLng mLocalLatLng;
    private Marker breatheMarker;
    private Marker breatheMarker_center;
    private AMapLocationListener mAMapLocationListener;


    @Override
    protected void initDates() {
        setupLocationStyle();
        findViewById(R.id.location_get_my_local).setOnClickListener(v -> {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocalLatLng, 18));
        });

    }

    @Override
    protected int getMapLayout() {
        return R.layout.mine_map_location;
    }

    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle() {
        LocationManager.getInstance(getContext()).startContinueLocation(mAMapLocationListener = aMapLocation -> {
            if (mOnMyLocationListener != null) {
                mOnMyLocationListener.onLocationChange(aMapLocation);
            }
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    Log.d("Location-->", aMapLocation.getAddress() + " " + aMapLocation.getStreetNum() + "  " + aMapLocation.getStreet());
                    mLocalLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    startBreatheAnimation(mLocalLatLng);
                } else {
                    String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                    Log.e("AmapErr", errText);
                }
            }
        });

    }

    private void startBreatheAnimation(LatLng latlng) {
        // 呼吸动画
        LatLng latLng = new LatLng(latlng.latitude - 0.02, latlng.longitude - 0.02);
        if (breatheMarker == null) {
            breatheMarker = mAMap.addMarker(new MarkerOptions().position(latLng).zIndex(1).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker_circle_64)));
            // 中心的marker
            breatheMarker_center = mAMap.addMarker(new MarkerOptions().position(latLng).zIndex(2).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_marker_circle_64)));

            // 动画执行完成后，默认会保持到最后一帧的状态
            AnimationSet animationSet = new AnimationSet(true);

            AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 0f);
            alphaAnimation.setDuration(2000);
            // 设置不断重复
            alphaAnimation.setRepeatCount(Animation.INFINITE);

            ScaleAnimation scaleAnimation = new ScaleAnimation(1, 3.5f, 1, 3.5f);
            scaleAnimation.setDuration(2000);
            // 设置不断重复
            scaleAnimation.setRepeatCount(Animation.INFINITE);

            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setInterpolator(new LinearInterpolator());
            breatheMarker.setAnimation(animationSet);
            breatheMarker.startAnimation();

            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
        } else {
            breatheMarker.setPosition(latlng);
            breatheMarker_center.setPosition(latlng);
        }
    }

    public interface OnMyLocationListener {
        void onLocationChange(AMapLocation aMapLocation);
    }

    private OnMyLocationListener mOnMyLocationListener;

    public void setLocalChangeListener(OnMyLocationListener mOnMyLocationListener) {
        this.mOnMyLocationListener = mOnMyLocationListener;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationManager.getInstance(getContext()).closeLocationListener(mAMapLocationListener);
        mMapView.onDestroy();
    }


    @Override
    public void onClick(View v) {
        if (mLocalLatLng != null) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocalLatLng, 18));
        }
    }
}
