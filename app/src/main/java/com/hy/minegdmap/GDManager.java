package com.hy.minegdmap;

import android.content.Context;

import com.amap.api.navi.enums.NaviType;
import com.hy.gdlibrary.GDHelper;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2020/03/03 15:11
 * @desc:
 */
public class GDManager {
    private static GDManager mGdManager;
    private GDHelper mGdHelper;

    private GDManager(Context context) {
        mGdHelper = new GDHelper.Builder()
                .setOpenLocal(true)
                .setOpenTrack(true)
                .setServiceId(164385)
                .build(context);
    }

    public static void init(Context context) {
        if (mGdManager == null) {
            mGdManager = new GDManager(context);
        }
    }

    public static GDManager getInstance() {
        return mGdManager;
    }

    public GDHelper getGdHelper() {
        return mGdHelper;
    }
}
