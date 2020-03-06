package com.hrw.gdlibrary.baseUI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.hrw.gdlibrary.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author:MtBaby
 * @date:2020/03/05 15:16
 * @desc:
 */
public class BaseFragment extends Fragment implements AMapNaviViewListener {
    AMapNaviView mMapView;
    private AMap mAMap;

    private BaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_base_fragment, container, false);
        mMapView = view.findViewById(R.id.mine_map_view);
        mMapView.setAMapNaviViewListener(this);
        mMapView.onCreate(savedInstanceState);
        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setLayoutVisible(false);
        options.setTrafficLine(false);
        options.setTrafficBarEnabled(false);
        options.setTilt(30);
        options.setAutoChangeZoom(true);
        options.setAfterRouteAutoGray(true);
        mMapView.setViewOptions(options);
        mAMap = mMapView.getMap();
        mAMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(23.090364, 113.238183)));
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

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

    public static BaseFragment getInstance() {
        return new BaseFragment();
    }

    public AMapNaviView getMapView() {
        return mMapView;
    }

    public AMap getAMap() {
        return mAMap;
    }

    @Override
    public void onNaviSetting() {
        System.out.println("onNaviSetting");
    }

    @Override
    public void onNaviCancel() {
        System.out.println("单击取消导航按钮");
    }

    @Override
    public boolean onNaviBackClick() {
        System.out.println("onNaviBackClick");
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {
        System.out.println("onNaviMapMode");

    }

    @Override
    public void onNaviTurnClick() {
        System.out.println("onNaviTurnClick");
    }

    @Override
    public void onNextRoadClick() {
        System.out.println("onNextRoadClick");
    }

    @Override
    public void onScanViewButtonClick() {
        System.out.println("onScanViewButtonClick");
    }

    @Override
    public void onLockMap(boolean b) {
        System.out.println("onLockMap");
    }

    @Override
    public void onNaviViewLoaded() {
        System.out.println("onNaviViewLoaded");
    }

    @Override
    public void onMapTypeChanged(int i) {
        System.out.println("onMapTypeChanged");
    }

    @Override
    public void onNaviViewShowMode(int i) {
        System.out.println("onNaviViewShowMode");
    }
}
