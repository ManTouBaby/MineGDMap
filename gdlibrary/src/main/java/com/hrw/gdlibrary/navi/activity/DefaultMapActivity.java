package com.hrw.gdlibrary.navi.activity;

import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.hrw.gdlibrary.AnimationUtil;
import com.hrw.gdlibrary.DatesHolder;
import com.hrw.gdlibrary.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorRes;

/**
 * @author:MtBaby
 * @date:2020/03/05 13:01
 * @desc:
 */
public class DefaultMapActivity extends BaseNaviMapActivity implements AMapNaviListener {
    private LinearLayout mLToolbar;
    private LinearLayout mLlNavigationSchemeContainer;
    private LinearLayout mLinearLayout1;
    private LinearLayout mLinearLayout2;
    private LinearLayout mLinearLayout3;
    private TextView mTVRedGreedCount;
    private TextView mTVStartNavigation;
    private List<LinearLayout> vNavigationScheme = new ArrayList<>();

    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<>();//保存当前算好的路线
    private SparseArray<AMapNaviPath> mapNaviPathSparseArray = new SparseArray<>();//保存当前算好的路线

    private int strategy;

    @Override
    protected int createLayout() {
        return R.layout.mine_map_base_activity;
    }

    @Override
    protected void init() {
        mLinearLayout1 = findViewById(R.id.mine_ll_navigation_scheme1);
        mLinearLayout2 = findViewById(R.id.mine_ll_navigation_scheme2);
        mLinearLayout3 = findViewById(R.id.mine_ll_navigation_scheme3);
        mLToolbar = findViewById(R.id.mine_map_title);
        mLlNavigationSchemeContainer = findViewById(R.id.ll_navigation_scheme_container);
        mTVRedGreedCount = findViewById(R.id.mine_red_greed_count);
        mTVStartNavigation = findViewById(R.id.mine_start_navigation);
        mTVStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigation();
            }
        });


        setStatueColor(R.color.map_main_color);
    }

    private void setStatueColor(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }

    }

    private void startNavigation() {
        setStatueColor(R.color.navigation_statue_color);
        mLToolbar.setVisibility(View.GONE);
        mLlNavigationSchemeContainer.setVisibility(View.GONE);
        mLToolbar.setAnimation(AnimationUtil.moveToViewTop());
        mLlNavigationSchemeContainer.setAnimation(AnimationUtil.moveToViewBottom());
        mAMapNavigation.startNavi(NaviType.EMULATOR);
        mAMapNavigationView.getViewOptions().setLayoutVisible(true);
    }

    private void stopNavigation() {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNavigationView.getMap().clear();
        routeOverlays.clear();
        mapNaviPathSparseArray.clear();
        vNavigationScheme.clear();

        mAMapNavigation.stopNavi();
        mAMapNavigation.destroy();
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */

        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavigation.strategyConvert(false, false, false, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sList.add(DatesHolder.getStartLatlng());
        eList.add(DatesHolder.getEndLatlng());
        mAMapNavigation.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    public void naviTypeClick(View view) {
        int id = view.getId();
        if (id == R.id.mine_tv_car) {
            mAMapNavigation.calculateDriveRoute(sList, eList, mWayPointList, strategy);
        } else if (id == R.id.mine_tv_bike) {
            mAMapNavigation.calculateRideRoute(DatesHolder.getStartLatlng(), DatesHolder.getEndLatlng());
        } else if (id == R.id.mine_tv_step) {
            mAMapNavigation.calculateWalkRoute(DatesHolder.getStartLatlng(), DatesHolder.getEndLatlng());
        }
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        System.out.println("路线规划成功:" + aMapCalcRouteResult.toString());
        mAMapNavigationView.getMap().clear();
        routeOverlays.clear();
        mapNaviPathSparseArray.clear();
        vNavigationScheme.clear();
        int[] routeid = aMapCalcRouteResult.getRouteid();
        for (int i = 0; i < routeid.length; i++) {
            AMapNaviPath naviPath = mAMapNavigation.getNaviPaths().get(routeid[i]);
            drawRoutes(routeid[i], naviPath);
        }
        if (routeOverlays.size() >= 3) {
            mLinearLayout3.setVisibility(View.VISIBLE);
            mLinearLayout2.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.GONE);
            for (int i = 0; i < mLinearLayout3.getChildCount(); i++) {
                LinearLayout view = (LinearLayout) mLinearLayout3.getChildAt(i);
                vNavigationScheme.add(view);
            }
        } else if (routeOverlays.size() == 2) {
            mLinearLayout2.setVisibility(View.VISIBLE);
            mLinearLayout3.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.GONE);

            for (int i = 0; i < mLinearLayout2.getChildCount(); i++) {
                LinearLayout view = (LinearLayout) mLinearLayout2.getChildAt(i);
                vNavigationScheme.add(view);
            }
        } else {
            mLinearLayout3.setVisibility(View.GONE);
            mLinearLayout2.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.VISIBLE);

            for (int i = 0; i < mLinearLayout1.getChildCount(); i++) {
                LinearLayout view = (LinearLayout) mLinearLayout1.getChildAt(i);
                vNavigationScheme.add(view);
            }
        }
        for (int i = 0; i < vNavigationScheme.size(); i++) {
            LinearLayout layout = vNavigationScheme.get(i);
            final int finalI = i;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectLine(finalI);
                }
            });
        }

        selectLine(0);
    }

    private void selectLine(int index) {
        for (int i = 0; i < vNavigationScheme.size(); i++) {
            vNavigationScheme.get(i).setSelected(false);
        }


        AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(index);
        if (naviPath != null && naviPath.getLightList() != null) {
            String light = naviPath.getLightList().size() > 0 ? "红绿灯" + naviPath.getLightList().size() + "个" : "红绿灯0个";
            mTVRedGreedCount.setText(light);
        }
        vNavigationScheme.get(index).setSelected(true);
        mAMapNavigation.selectRouteId(routeOverlays.keyAt(index));
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        AMap aMap = mAMapNavigationView.getMap();
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(true);
//        routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_start));
//        routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_end));
        routeOverLay.addToMap();
        routeOverLay.zoomToSpan(120);
        routeOverlays.put(routeId, routeOverLay);
        mapNaviPathSparseArray.put(routeId, path);
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        System.out.println("路线规划失败:" + aMapCalcRouteResult.getErrorDetail());
    }

    @Override
    public void onStartNavi(int i) {
        System.out.println("开始导航====");
    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
//        System.out.println("onNaviInfoUpdate：" + naviInfo);
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideCross() {

    }


    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }


    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }


}
