package com.hy.gdlibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.hy.gdlibrary.R;
import com.hy.gdlibrary.location.LocationManager;

/**
 * Author: MtHappy
 * Date: 2020/7/22 11:55
 * Description: 地图上基本的地图显示
 */
public abstract class MapBaseAC extends AppCompatActivity {
    protected MapView mMapView;
    protected AMap mAMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_base_layout);
        RelativeLayout mapBaseContainer = findViewById(R.id.map_base_container);
        LayoutInflater.from(this).inflate(getMapLayout(), mapBaseContainer, true);
        mMapView = findViewById(R.id.map_base);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        initData();
    }

    protected abstract void initData();

    protected abstract int getMapLayout();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
