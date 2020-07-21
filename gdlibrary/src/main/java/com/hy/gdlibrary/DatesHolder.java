package com.hy.gdlibrary;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;

/**
 * @author:MtBaby
 * @date:2020/03/05 16:25
 * @desc:
 */
public class DatesHolder {
    protected static NaviLatLng HuaDiWang = new NaviLatLng(23.087008,113.234095);
    protected static NaviLatLng GongYuanQian = new NaviLatLng(23.125435,113.264297);
    protected static NaviLatLng AiJiaGY = new NaviLatLng(23.090364,113.238183);
    protected static NaviLatLng ChaJiaoYEY = new NaviLatLng(23.090442,113.226446);

    private static NaviLatLng endLatlng = new NaviLatLng(39.917337, 116.397056);
    private static NaviLatLng startLatlng = new NaviLatLng(39.904556, 116.427231);

    LatLng p1 = new LatLng(39.993266, 116.473193);//首开广场
    LatLng p2 = new LatLng(39.917337, 116.397056);//故宫博物院
    LatLng p3 = new LatLng(39.904556, 116.427231);//北京站
    LatLng p4 = new LatLng(39.773801, 116.368984);//新三余公园(南5环)
    LatLng p5 = new LatLng(40.041986, 116.414496);//立水桥(北5环)

    public static NaviLatLng getEndLatlng() {
        return endLatlng;
    }

    public static NaviLatLng getStartLatlng() {
        return startLatlng;
    }

    public static NaviLatLng getHuaDiWang() {
        return HuaDiWang;
    }

    public static NaviLatLng getAiJiaGY() {
        return AiJiaGY;
    }

    public static NaviLatLng getGongYuanQian() {
        return GongYuanQian;
    }

    public static NaviLatLng getChaJiaoYEY() {
        return ChaJiaoYEY;
    }
}
