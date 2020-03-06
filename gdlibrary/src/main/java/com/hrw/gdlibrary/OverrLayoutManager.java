package com.hrw.gdlibrary;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.view.RouteOverLay;

/**
 * @author:MtBaby
 * @date:2020/03/05 12:35
 * @desc:
 */
public class OverrLayoutManager {

    private void drawRoutes(Context context,AMap aMap, int routeId, AMapNaviPath path) {
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, context);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
//        routeOverlays.put(routeId, routeOverLay);
    }

}
