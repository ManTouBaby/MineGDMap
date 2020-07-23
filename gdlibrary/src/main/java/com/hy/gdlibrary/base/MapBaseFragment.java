package com.hy.gdlibrary.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.hy.gdlibrary.R;

/**
 * Author: MtHappy
 * Date: 2020/7/22 14:37
 * Description: 高德地图Fragment基础类
 */
public abstract class MapBaseFragment extends Fragment {
    protected MapView mMapView;
    protected AMap mAMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_base_layout, container, false);
        RelativeLayout mapBaseContainer = view.findViewById(R.id.map_base_container);
        if (getMapLayout() != 0){
            LayoutInflater.from(getContext()).inflate(getMapLayout(), mapBaseContainer, true);
        }
        mMapView = view.findViewById(R.id.map_base);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        System.out.println("onCreateView-------");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("onViewCreated-------");

        mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
//        // 设置定位的类型为定位模式
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//        // 设置定位的类型为 跟随模式
//        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
//        // 设置定位的类型为根据地图面向方向旋转
//        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        UiSettings mUiSettings = mAMap.getUiSettings();
        mUiSettings.setScaleControlsEnabled(false);//设置地图默认的比例尺是否显示
        mUiSettings.setZoomControlsEnabled(false);//设置地图默认的缩放按钮是否显示
        mUiSettings.setCompassEnabled(false);//设置地图默认的指南针是否显示
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        initDates();
    }

    public AMap getAMap() {
        return mAMap;
    }

    protected abstract void initDates();

    protected abstract int getMapLayout();

    protected <T extends View> T findViewById(@IdRes int id) {
        if (getView() == null) return null;
        return getView().findViewById(id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
