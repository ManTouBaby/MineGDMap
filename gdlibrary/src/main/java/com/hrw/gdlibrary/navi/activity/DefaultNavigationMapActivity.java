package com.hrw.gdlibrary.navi.activity;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviPath;
import com.hrw.gdlibrary.R;

import java.util.HashMap;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 14:45
 * @desc:
 */
public class DefaultNavigationMapActivity extends BaseNaviMapActivity {


    @Override
    protected int createLayout() {
        return R.layout.mine_map_default_activity;
    }

    @Override
    protected void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.trance_parent_color));
        }
        AMapNaviViewOptions options = new AMapNaviViewOptions();
        options.setLaneInfoShow(false);
        options.setTrafficLine(true);
        options.setTilt(25);
        mAMapNavigationView.setViewOptions(options);

    }


    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
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
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavigation.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavigation.calculateDriveRoute(sList, eList, mWayPointList, strategy);

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        super.onCalculateRouteSuccess(aMapCalcRouteResult);
        HashMap<Integer, AMapNaviPath> naviPaths = mAMapNavigation.getNaviPaths();

    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        mAMapNavigation.startNavi(NaviType.EMULATOR);
    }

}
