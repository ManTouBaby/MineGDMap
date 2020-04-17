package com.hrw.minegdmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.services.core.PoiItem;
import com.hrw.gdlibrary.CodeManager;
import com.hrw.gdlibrary.DatesHolder;
import com.hrw.gdlibrary.GDHelper;


public class MainActivity extends AppCompatActivity {
    private final static int Ask_Local_nearby_code = 0x100;
    private GDHelper gdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gdHelper = GDManager.getInstance().getGdHelper();
        findViewById(R.id.bt_goto_nearby).setOnClickListener(v -> gdHelper.openNearby(MainActivity.this, Ask_Local_nearby_code));
        findViewById(R.id.bt_goto_navi).setOnClickListener(v -> gdHelper.openNavigation(MainActivity.this, DatesHolder.getAiJiaGY(), DatesHolder.getGongYuanQian()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Ask_Local_nearby_code:
                    PoiItem poiItem = data.getParcelableExtra(CodeManager.PoiItem);
                    System.out.println("获得的数据:" + poiItem.toString());
                    break;
            }
        }
    }
}
