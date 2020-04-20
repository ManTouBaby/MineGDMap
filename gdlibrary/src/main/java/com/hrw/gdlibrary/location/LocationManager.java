package com.hrw.gdlibrary.location;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 12:25
 * @desc:
 */
public class LocationManager {
    //声明mlocationClient对象
    private AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption;
    private static LocationManager mLocationManager;

    private LocationManager(Context context) {
        mLocationClient = new AMapLocationClient(context);
        //设置定位监听
//        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mLocationOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mLocationOption.setLocationCacheEnable(false);//设置是否返回缓存中位置，默认是true
    }

    public static LocationManager getInstance(Context context) {
        if (mLocationManager == null) {
            synchronized (LocationManager.class) {
                if (mLocationManager == null) {
                    mLocationManager = new LocationManager(context);
                }
            }
        }
        return mLocationManager;
    }

    public void startSingleLocation(AMapLocationListener aMapLocationListener) {
        mLocationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mLocationClient.setLocationOption(mLocationOption); //设置定位参数
        mLocationClient.setLocationListener(aMapLocationListener);
        mLocationClient.startLocation();
    }

    public void startContinueLocation(AMapLocationListener aMapLocationListener) {
        mLocationOption.setOnceLocation(false);
        mLocationOption.setInterval(2000); //设置定位间隔,单位毫秒,默认为2000ms
        mLocationClient.setLocationOption(mLocationOption); //设置定位参数
        mLocationClient.setLocationListener(aMapLocationListener);

        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mLocationClient.startLocation();
    }

    public void closeLocationListener(AMapLocationListener aMapLocationListener){
        mLocationClient.unRegisterLocationListener(aMapLocationListener);
    }


    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation.getErrorCode() == 0) {
            //定位成功回调信息，设置相关消息
            aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
            aMapLocation.getLatitude();//获取纬度
            aMapLocation.getLongitude();//获取经度
            aMapLocation.getAccuracy();//获取精度信息
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(aMapLocation.getTime());
            df.format(date);//定位时间
            System.out.println("启用定位:" + aMapLocation.getAddress() + " " + aMapLocation.getStreetNum() + "  " + aMapLocation.getStreet());
        }
    }
}
