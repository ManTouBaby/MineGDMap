package com.hy.minegdmap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.query.entity.HistoryTrack;
import com.amap.api.track.query.entity.Point;
import com.amap.api.track.query.entity.TrackPoint;
import com.amap.api.track.query.model.HistoryTrackRequest;
import com.amap.api.track.query.model.HistoryTrackResponse;
import com.amap.api.track.query.model.LatestPointRequest;
import com.amap.api.track.query.model.LatestPointResponse;
import com.amap.api.track.query.model.QueryTerminalRequest;
import com.amap.api.track.query.model.QueryTerminalResponse;
import com.hy.gdlibrary.GDHelper;
import com.hy.gdlibrary.base.MultipointBo;
import com.hy.gdlibrary.fragment.MultipointFragment;
import com.hy.gdlibrary.track.SimpleOnTrackListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: MtHappy
 * Date: 2020/7/22 15:53
 * Description: 展示多点
 */
public class ACMultipoint extends AppCompatActivity implements MultipointFragment.OnMarkerClickListener {
    private MultipointFragment multipointFragment;
    private GDHelper mGdHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_my_local);
        mGdHelper = GDManager.getInstance().getGdHelper();
        multipointFragment = mGdHelper.getMultipointFragment();
        multipointFragment.setOnMarkerClickListener(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map_container, multipointFragment).commit();
        initDates();
    }

    @Override
    public void onMarkerClick(Marker marker) {
        System.out.println("图标:" + marker.getObject());
    }


    private void initDates() {
        AMapTrackClient aMapTrackClient = mGdHelper.getMapTrackManger().getAMapTrackClient();
        // 先查询terminal id，然后用terminal id查询轨迹
        // 查询符合条件的所有轨迹点，并绘制
        aMapTrackClient.queryTerminal(new QueryTerminalRequest(mGdHelper.getServiceId(), MainActivity.userId), new SimpleOnTrackListener() {
            @Override
            public void onQueryTerminalCallback(QueryTerminalResponse queryTerminalResponse) {
                if (queryTerminalResponse.isSuccess()) {
                    if (queryTerminalResponse.isTerminalExist()) {
                        long tid = queryTerminalResponse.getTid();
                        // 搜索最近12小时以内上报的轨迹
                        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(
                                mGdHelper.getServiceId(),
                                tid,
                                System.currentTimeMillis() - 12 * 60 * 60 * 1000,
                                System.currentTimeMillis(),
                                0,
                                0,
                                5000,   // 距离补偿，只有超过5km的点才启用距离补偿
                                0,  // 由旧到新排序
                                1,  // 返回第1页数据
                                100,    // 一页不超过100条
                                ""  // 暂未实现，该参数无意义，请留空
                        );
                        aMapTrackClient.queryHistoryTrack(historyTrackRequest, new SimpleOnTrackListener() {
                            @Override
                            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                                if (historyTrackResponse.isSuccess()) {
                                    HistoryTrack historyTrack = historyTrackResponse.getHistoryTrack();
                                    if (historyTrack == null || historyTrack.getCount() == 0) {
                                        Toast.makeText(ACMultipoint.this, "未获取到轨迹点", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    List<Point> points = historyTrack.getPoints();
                                    drawTrackOnMap(points, historyTrack.getStartPoint(), historyTrack.getEndPoint());
                                } else {
                                    Toast.makeText(ACMultipoint.this, "查询历史轨迹点失败，" + historyTrackResponse.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ACMultipoint.this, "Terminal不存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    showNetErrorHint(queryTerminalResponse.getErrorMsg());
                }
            }
        });

    }

    private void drawTrackOnMap(List<Point> points, TrackPoint startPoint, TrackPoint endPoint) {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE).width(20);

//        if (startPoint != null && startPoint.getLocation() != null) {
//            LatLng latLng = new LatLng(startPoint.getLocation().getLat(), startPoint.getLocation().getLng());
//            Map<String, String> props = startPoint.getLocation().getProps();
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            endMarkers.add(textureMapView.getMap().addMarker(markerOptions));
//        }
//
//        if (endPoint != null && endPoint.getLocation() != null) {
//            LatLng latLng = new LatLng(endPoint.getLocation().getLat(), endPoint.getLocation().getLng());
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            endMarkers.add(textureMapView.getMap().addMarker(markerOptions));
//        }
        List<MultipointBo> multipointBos = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);

//            LatLng latLng = new LatLng(p.getLat(), p.getLng());
//            polylineOptions.add(latLng);
//            boundsBuilder.include(latLng);
//            String point = DatesTest.points[i];
//            String[] split = point.split("-");
            MultipointBo multipointBo = new MultipointBo();
            multipointBo.setLatitude(p.getLat());
            multipointBo.setLongitude(p.getLng());
            multipointBo.setTagObject(i);
//            multipointBo.setLocationIcon(R.mipmap.map_more_local);
            multipointBos.add(multipointBo);
        }


        multipointFragment.updateMultipointBos(multipointBos);
//        Polyline polyline = textureMapView.getMap().addPolyline(polylineOptions);
//        polylines.add(polyline);
//        textureMapView.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 30));
    }

}
