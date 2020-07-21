package com.hy.gdlibrary.location;

import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.hy.gdlibrary.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationFragment extends Fragment implements LocationSource, View.OnClickListener {
    ImageView mMyLocal;
    protected MapView mMapView;
    protected AMap mAMap;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private UiSettings mUiSettings;
    private OnLocationChangedListener mListener;
    private AMapLocationListener mAMapLocationListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_map_location, null);
        mMapView = view.findViewById(R.id.mine_map_show);
        mMapView.onCreate(savedInstanceState);
        mMyLocal = view.findViewById(R.id.location_get_my_local);
        mMyLocal.setOnClickListener(this);
        init();
        return view;
    }

    /**
     * 初始化
     */
    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mUiSettings = mAMap.getUiSettings();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle();
    }

    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle() {
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));

//        // 设置定位的类型为定位模式
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//        // 设置定位的类型为 跟随模式
//        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
//        // 设置定位的类型为根据地图面向方向旋转
//        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        mUiSettings.setScaleControlsEnabled(false);//设置地图默认的比例尺是否显示
        mUiSettings.setZoomControlsEnabled(false);//设置地图默认的缩放按钮是否显示
        mUiSettings.setCompassEnabled(false);//设置地图默认的指南针是否显示
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮


        LocationManager.getInstance(getContext()).startContinueLocation(mAMapLocationListener = aMapLocation -> {
            if (mOnMyLocationListener != null) {
                mOnMyLocationListener.onLocationChange(aMapLocation);
            }
            if (mListener != null && aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
//                    mLocationErrText.setVisibility(View.GONE);
                    Log.d("Location-->", aMapLocation.getAddress() + " " + aMapLocation.getStreetNum() + "  " + aMapLocation.getStreet());
                    mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                } else {
                    String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                    Log.e("AmapErr", errText);
//                    mLocationErrText.setVisibility(View.VISIBLE);
//                    mLocationErrText.setText(errText);
                }
            }
        });

    }

    public static interface OnMyLocationListener {
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
        mListener = null;
        LocationManager.getInstance(getContext()).closeLocationListener(mAMapLocationListener);
        mMapView.onDestroy();
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onClick(View v) {
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }
}
