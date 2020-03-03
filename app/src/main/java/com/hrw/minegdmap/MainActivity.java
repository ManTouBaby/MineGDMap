package com.hrw.minegdmap;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hrw.gdlibrary.GDHelper;

public class MainActivity extends AppCompatActivity {

    private GDHelper gdHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gdHelper = GDManager.getInstance(getApplicationContext()).getGdHelper();
    }

    public void onBtnClick(View view) {
        gdHelper.openNavigation(this);
    }

}
