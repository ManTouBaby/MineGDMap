package com.hy.minegdmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.services.core.PoiItem;
import com.hy.gdlibrary.CodeManager;
import com.hy.gdlibrary.DatesHolder;
import com.hy.gdlibrary.GDHelper;
import com.hy.gdlibrary.location.LocationManager;

import java.text.SimpleDateFormat;
import java.util.Date;


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
       findViewById(R.id.bt_goto_my_local).setOnClickListener(v->{
           Intent intent = new Intent(this,MyLocal.class);
           startActivity(intent);
       });
        LocationManager.getInstance(this).startContinueLocation(aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
//                System.out.println("MainActivity启用定位:" + aMapLocation.getAddress() + " " + aMapLocation.getStreetNum() + "  " + aMapLocation.getStreet());
            }
        });
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
