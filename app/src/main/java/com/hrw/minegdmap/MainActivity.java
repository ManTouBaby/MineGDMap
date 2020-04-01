package com.hrw.minegdmap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hrw.gdlibrary.DatesHolder;
import com.hrw.gdlibrary.GDHelper;


public class MainActivity extends AppCompatActivity {

    private GDHelper gdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gdHelper = GDManager.getInstance().getGdHelper();
    }

    public void onBtnClick(View view) {
        gdHelper.openNavigation(this, DatesHolder.getAiJiaGY(),DatesHolder.getGongYuanQian());
    }

}
