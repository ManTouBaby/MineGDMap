package com.hrw.minegdmap;

import android.content.Context;

import com.amap.api.navi.enums.NaviType;
import com.hrw.gdlibrary.GDHelper;

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
//                .setApiKey("29ee6dfa46f2774ccb76586221194f50")//公司电脑
                .setApiKey("2bff4221a6149f587e5cf0b8c60d4715")//家庭电脑
                .setOpenXFYunVoice(false)
                .setStIcon(R.mipmap.start)
                .setEndIcon(R.mipmap.end)
                .setNaviType(NaviType.EMULATOR)
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
