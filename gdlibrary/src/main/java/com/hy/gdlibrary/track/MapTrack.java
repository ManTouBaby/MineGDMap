package com.hy.gdlibrary.track;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.hy.gdlibrary.R;

import java.util.ArrayList;


/**
 * Author: MtHappy
 * Date: 2020/7/23 10:24
 * Description: 实现轨迹回放
 */
public class MapTrack extends AppCompatActivity {
    protected MapView mMapView;
    protected AMap mAMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_track_show);
        Intent intent = getIntent();
        String startLocal = intent.getStringExtra("startLocal");
        String endLocal = intent.getStringExtra("endLocal");
        ArrayList<LatLng> points = intent.getParcelableArrayListExtra("points");

        TextView mStartView = findViewById(R.id.map_start_local);
        TextView mEndView = findViewById(R.id.map_end_local);
        mStartView.setText(startLocal);
        mEndView.setText(endLocal);

        findViewById(R.id.map_play_track).setOnClickListener(v -> {
            System.out.println("实现轨迹播放");
        });
        mMapView = findViewById(R.id.map_base);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        initView();
    }

    private void initView() {

    }

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
