package com.hy.gdlibrary.nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hy.gdlibrary.CodeManager;
import com.hy.gdlibrary.R;
import com.hy.gdlibrary.location.LocationManager;
import com.hy.gdlibrary.utils.MarkerUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/17 9:30
 * @desc:
 */
public class NearbyMapActivity extends AppCompatActivity implements TextWatcher, NearbyLocationAdapter.OnNearbyItemClickListener {
    MapView mMapView;
    private AMap mAMap;
    private RecyclerView mNearbyLocationShow;
    private LinearLayout mLoadNearbyError;
    private LinearLayout mRelativeLayout;
    private TextView mLoadError;
    private ImageView mGoMyLocal;
    private EditText mPoiSearch;
    private TextView mCancelSearch;
    private TextView mSearchClick;

    private NearbyLocationAdapter mLocationAdapter;
    private int nearbyPageSize = 20;
    private int nearbyNum = 1;
    private LatLonPoint mMovePoint;
    private LatLng mMyLocal;
    private InputMethodManager mInputMethodManager;
    private Marker mSelectMarker;
    private boolean isChangeByClickItem;
    private Marker mLocalMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.mine_map_nearby_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mLoadError = findViewById(R.id.mt_load_error);
        mGoMyLocal = findViewById(R.id.mt_go_my_local);
        mRelativeLayout = findViewById(R.id.rv_load_nearby);
        mLoadNearbyError = findViewById(R.id.rv_load_error);
        mSearchClick = findViewById(R.id.map_edit_search_click);
        mPoiSearch = findViewById(R.id.map_edit_search);
        mPoiSearch.addTextChangedListener(this);
        mCancelSearch = findViewById(R.id.map_edit_search_cancel);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

        mSearchClick.setOnClickListener(v -> {
            mSearchClick.setVisibility(View.GONE);
            mPoiSearch.setVisibility(View.VISIBLE);
            mCancelSearch.setVisibility(View.VISIBLE);
            mPoiSearch.requestFocus();
            mInputMethodManager.showSoftInput(mPoiSearch, 0);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        mCancelSearch.setOnClickListener(v -> {
            mSearchClick.setVisibility(View.VISIBLE);
            mPoiSearch.setVisibility(View.GONE);
            mCancelSearch.setVisibility(View.GONE);
            mCancelSearch.setFocusable(false);
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(mPoiSearch.getWindowToken(), 0);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        mPoiSearch.setOnClickListener(v->{
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_COLLAPSED){
                    mInputMethodManager.hideSoftInputFromWindow(mPoiSearch.getWindowToken(), 0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        findViewById(R.id.mt_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.mt_commit).setOnClickListener(v -> {
            PoiItem selectItem = mLocationAdapter.getSelectItem();
            Intent intent = new Intent();
            intent.putExtra(CodeManager.PoiItem, selectItem);
            setResult(RESULT_OK, intent);
            finish();
        });
        mNearbyLocationShow = findViewById(R.id.rv_show_nearby_location);
        mNearbyLocationShow.setLayoutManager(new LinearLayoutManager(this));
        mNearbyLocationShow.setAdapter(mLocationAdapter = new NearbyLocationAdapter());
        mLocationAdapter.setOnLoadMoreListener(() -> {
            nearbyNum++;
            doSearchNearby("");
        });
        mLocationAdapter.setOnNearbyItemClickListener(this);
        mGoMyLocal.setOnClickListener(v -> mAMap.animateCamera(CameraUpdateFactory.newLatLng(mMyLocal)));

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);

        initLocation();
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (isChangeByClickItem) {
                    isChangeByClickItem = false;
                    return;
                }
                mMovePoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                nearbyNum = 1;
                doSearchNearby("");
            }
        });
    }

    AMapLocationListener mAMaoAMapLocationListener;

    private void initLocation() {
        LocationManager.getInstance(this).startContinueLocation(mAMaoAMapLocationListener = aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
//                System.out.println("NearbyMapActivity启用定位:" + aMapLocation.getAddress() + " " + aMapLocation.getStreetNum() + "  " + aMapLocation.getStreet());
                mMyLocal = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                addMarkerInScreenCenter(mMyLocal);
            }
        });
    }


    /**
     * 添加选点marker
     */
    private void addMarkerInScreenCenter(LatLng latLng) {
        if (mLocalMarker == null) {
            //当前位置点
            mLocalMarker = mAMap.addMarker(MarkerUtil.getMarker(latLng, 0.5f, 0.1f, R.mipmap.icon_location_circle));
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        } else {
            mLocalMarker.setPosition(latLng);
        }

        if (mSelectMarker == null) {
            //选中点
            mSelectMarker = mAMap.addMarker(MarkerUtil.getMarker(latLng, 0.5f, 0.5f, R.mipmap.icon_location));

            Point screenPosition = mAMap.getProjection().toScreenLocation(mAMap.getCameraPosition().target);
            //设置Marker在屏幕上,不跟随地图移动
            mSelectMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        }

    }


    //搜索附近
    private void doSearchNearby(String keyWorld) {
        if (!mLocationAdapter.isOnLoadMore()) startLoadNearby();
        PoiSearch.Query query = new PoiSearch.Query(keyWorld, "", "");
        query.setPageSize(nearbyPageSize);
        query.setPageNum(nearbyNum);
        PoiSearch poisearch = new PoiSearch(this, query);
        poisearch.setBound(new PoiSearch.SearchBound(mMovePoint, 500, true));
        poisearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int resultCode) {
                if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
                    stopLoadNearby();
                    if (poiResult != null && poiResult.getPois().size() > 0) {
                        List<PoiItem> poiItems = poiResult.getPois();
                        if (!mLocationAdapter.isOnLoadMore()) {
                            mLocationAdapter.setPoiItems(poiItems);
                        } else {
                            mLocationAdapter.addPoiItems(poiItems);
                        }
                    } else {
                        Toast.makeText(NearbyMapActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
                        if (!mLocationAdapter.isOnLoadMore()) stopLoadNearbyError("无搜索结果");
                    }
                } else {
                    Toast.makeText(NearbyMapActivity.this, "搜索失败，错误 " + resultCode, Toast.LENGTH_SHORT).show();
                    if (!mLocationAdapter.isOnLoadMore())
                        stopLoadNearbyError("搜索失败，错误" + resultCode);
                    mLocationAdapter.loadMoreComplete();
                }
                mLocationAdapter.loadMoreComplete();
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        poisearch.searchPOIAsyn();
    }

    private void startLoadNearby() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        mLoadNearbyError.setVisibility(View.GONE);
        mNearbyLocationShow.setVisibility(View.GONE);
    }

    private void stopLoadNearby() {
        mRelativeLayout.setVisibility(View.GONE);
        mLoadNearbyError.setVisibility(View.GONE);
        mNearbyLocationShow.setVisibility(View.VISIBLE);
    }

    private void stopLoadNearbyError(String msg) {
        mRelativeLayout.setVisibility(View.GONE);
        mLoadNearbyError.setVisibility(View.VISIBLE);
        mNearbyLocationShow.setVisibility(View.GONE);
        mLoadError.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        LocationManager.getInstance(this).closeLocationListener(mAMaoAMapLocationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        doSearchNearby(s.toString());
    }

    @Override
    public void onNearbyClick(PoiItem poiItem) {
        isChangeByClickItem = true;
        LatLonPoint latLonPoint = poiItem.getLatLonPoint();
        mAMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude())));
    }


}
