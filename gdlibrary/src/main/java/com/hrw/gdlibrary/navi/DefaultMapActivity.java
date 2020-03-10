package com.hrw.gdlibrary.navi;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.hrw.gdlibrary.AnimationUtil;
import com.hrw.gdlibrary.GDHelper;
import com.hrw.gdlibrary.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;

/**
 * @author:MtBaby
 * @date:2020/03/05 13:01
 * @desc:
 */
public class DefaultMapActivity extends BaseMapActivity implements AMapNaviListener {
    private LinearLayout mLToolbar;
    private ImageView mIvCar;
    private ImageView mIvBike;
    private ImageView mIvWalk;

    private LinearLayout mLlNavigationSchemeContainer;
    private LinearLayout mLinearLayout1;
    private LinearLayout mLinearLayout2;
    private LinearLayout mLinearLayout3;
    private TextView mTVRedGreedCount;
    private TextView mTVStartNavigation;
    private TextView tvBack;


    private List<LinearLayout> vNavigationScheme = new ArrayList<>();

    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<>();//保存当前算好的路线
    private SparseArray<AMapNaviPath> mapNaviPathSparseArray = new SparseArray<>();//保存当前算好的路线

    private int strategy;
    private GDHelper.Builder mBuilder;
    private NaviLatLng stLocation;
    private NaviLatLng endLocation;

    @Override
    protected int createLayout() {
        return R.layout.mine_map_base_activity;
    }

    @Override
    protected void init() {
        mBuilder = (GDHelper.Builder) getIntent().getSerializableExtra("Builder");
        stLocation = getIntent().getParcelableExtra("stLocation");
        endLocation = getIntent().getParcelableExtra("endLocation");

        mIvCar = findView(R.id.mine_tv_car);
        mIvBike = findView(R.id.mine_tv_bike);
        mIvWalk = findView(R.id.mine_tv_step);

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
        tvBack = findViewById(R.id.mine_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTwo();
            }
        });

        setStatueColor(R.color.map_main_color);
        mIvCar.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        showTwo();
    }

    private void showTwo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("确定退出导航？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(DefaultMapActivity.this, "已退出导航", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
//                        Toast.makeText(DefaultMapActivity.this, "关闭按钮", Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
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
        mAMapNavigation.startNavi(NaviType.GPS);
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
        clearRout();
        mapNaviPathSparseArray.clear();
        vNavigationScheme.clear();

    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        try {
            //strategyConvert(congestion-躲避拥堵, avoidhightspeed-不走高速, cost-避免收费, hightspeed-高速优先, multipleroute-多路径)
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavigation.strategyConvert(mBuilder.isCongestion(), mBuilder.isAvoidHighWay(), mBuilder.isAvoidCost(), mBuilder.isHighWay(), mBuilder.isMultipleRoute());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sList.add(stLocation);
        eList.add(endLocation);
        mAMapNavigation.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    public void navigationTypeClick(View view) {
        mIvCar.setSelected(false);
        mIvBike.setSelected(false);
        mIvWalk.setSelected(false);

        int id = view.getId();
        if (id == R.id.mine_tv_car) {
            mIvCar.setSelected(true);
            mAMapNavigation.calculateDriveRoute(sList, eList, mWayPointList, strategy);
        } else if (id == R.id.mine_tv_bike) {
            mIvBike.setSelected(true);
            mAMapNavigation.calculateRideRoute(stLocation, endLocation);
        } else if (id == R.id.mine_tv_step) {
            mIvWalk.setSelected(true);
            mAMapNavigation.calculateWalkRoute(stLocation, endLocation);
        }
    }


    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
//        System.out.println("路线规划成功:" + aMapCalcRouteResult.toString());

        clearRout();
        mapNaviPathSparseArray.clear();
        vNavigationScheme.clear();
        int[] routeid = aMapCalcRouteResult.getRouteid();
        for (int i = 0; i < routeid.length; i++) {
            AMapNaviPath naviPath = mAMapNavigation.getNaviPaths().get(routeid[i]);
            drawRoutes(routeid[i], naviPath);
        }
        if (routeOverlays.size() >= 3) {
            for (int i = 0; i < mLinearLayout3.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) mLinearLayout3.getChildAt(i);
                vNavigationScheme.add(layout);

                AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(i);
                initNavigationSchemeView(layout, naviPath, i);
            }
            mLinearLayout3.setVisibility(View.VISIBLE);
            mLinearLayout2.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.GONE);
        } else if (routeOverlays.size() == 2) {
            for (int i = 0; i < mLinearLayout2.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) mLinearLayout2.getChildAt(i);
                vNavigationScheme.add(layout);

                AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(i);
                initNavigationSchemeView(layout, naviPath, i);
            }
            mLinearLayout2.setVisibility(View.VISIBLE);
            mLinearLayout3.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < mLinearLayout1.getChildCount(); i++) {
                LinearLayout layout = (LinearLayout) mLinearLayout1.getChildAt(i);
                vNavigationScheme.add(layout);

                AMapNaviPath naviPath = mapNaviPathSparseArray.valueAt(i);
                initNavigationSchemeView(layout, naviPath, i);
            }
            mLinearLayout3.setVisibility(View.GONE);
            mLinearLayout2.setVisibility(View.GONE);
            mLinearLayout1.setVisibility(View.VISIBLE);
        }

        selectLine(0);
    }

    @SuppressLint("SetTextI18n")
    private void initNavigationSchemeView(LinearLayout layout, AMapNaviPath naviPath, final int index) {
        TextView tvTime = layout.findViewById(R.id.mine_time);
        TextView tvdDistance = layout.findViewById(R.id.mine_distance);
        tvTime.setText(naviPath.getAllTime() / 60 + "分钟");
        float distance = naviPath.getAllLength() / 1000f;
        distance = (float) (Math.round(distance * 10) / 10.0);
        tvdDistance.setText(distance + "公里");

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLine(index);
            }
        });
    }

    private void initView() {

    }

    private void clearRout() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            routeOverlays.valueAt(i).removeFromMap();
        }
        routeOverlays.clear();
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
        if (mBuilder.getStIcon() != 0)
            routeOverLay.setStartPointBitmap(BitmapFactory.decodeResource(getResources(), mBuilder.getStIcon()));
        if (mBuilder.getEndIcon() != 0)
            routeOverLay.setEndPointBitmap(BitmapFactory.decodeResource(getResources(), mBuilder.getEndIcon()));
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


}
