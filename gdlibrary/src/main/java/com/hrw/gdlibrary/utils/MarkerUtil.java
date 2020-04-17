package com.hrw.gdlibrary.utils;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

/**
 * @author:MtBaby
 * @date:2020/04/17 12:42
 * @desc:
 */
public class MarkerUtil {

    public static MarkerOptions getMarker(int id) {
        return getMarker(null, id);
    }

    public static MarkerOptions getMarker(LatLng latLng, int id) {
        return getMarker(latLng, 0, 0, id);
    }

    public static MarkerOptions getMarker(float pointX, float pointY, int id) {
        return getMarker(null, pointX, pointY, id);
    }

    public static MarkerOptions getMarker(LatLng latLng, float pointX, float pointY, int id) {
        MarkerOptions options = new MarkerOptions()
                .anchor(pointX, pointY)
                .icon(BitmapDescriptorFactory.fromResource(id));
        if (latLng != null) options.position(latLng);
        return options;
    }

}
