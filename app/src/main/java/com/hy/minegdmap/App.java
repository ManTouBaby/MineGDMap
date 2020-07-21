package com.hy.minegdmap;

import android.app.Application;

/**
 * @author:MtBaby
 * @date:2020/03/21 15:33
 * @desc:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        GDManager.init(this);
        super.onCreate();
    }
}
