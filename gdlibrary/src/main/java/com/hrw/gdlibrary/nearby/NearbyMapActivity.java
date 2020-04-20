package com.hrw.gdlibrary.nearby;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hrw.gdlibrary.CodeManager;
import com.hrw.gdlibrary.R;
import com.hrw.gdlibrary.location.LocationManager;
import com.hrw.gdlibrary.utils.MarkerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author:MtBaby
 * @date:2020/04/17 9:30
 * @desc:
 */
public class NearbyMapActivity extends AppCompatActivity {
    MapView mMapView;
    private AMap mAMap;
    private RecyclerView mNearbyLocationShow;
    private LinearLayout mLoadNearbyError;
    private LinearLayout mRelativeLayout;
    private TextView mLoadError;
    private ImageView mGoMyLocal;
    private NearbyLocationAdapter mLocationAdapter;
    private int nearbyPageSize = 20;
    private int nearbyNum = 0;
    private LatLonPoint mMovePoint;
    private LatLng mMyLocal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_map_nearby_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mLoadError = findViewById(R.id.mt_load_error);
        mGoMyLocal = findViewById(R.id.mt_go_my_local);
        mRelativeLayout = findViewById(R.id.rv_load_nearby);
        mLoadNearbyError = findViewById(R.id.rv_load_error);
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
            doSearchNearby();
        });
        mGoMyLocal.setOnClickListener(v -> mAMap.animateCamera(CameraUpdateFactory.newLatLng(mMyLocal)));

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        initLocation();
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                mMovePoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                nearbyNum = 0;
                doSearchNearby();
            }
        });
    }

    AMapLocationListener mAMaoAMapLocationListener;

    private void initLocation() {
        LocationManager.getInstance(this).startSingleLocation(mAMaoAMapLocationListener = aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                System.out.println("NearbyMapActivity启用定位:" + aMapLocation.getAddress() + " " + aMapLocation.getStreetNum() + "  " + aMapLocation.getStreet());
                mMyLocal = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                addMarkerInScreenCenter(mMyLocal);
            }
        });
    }


    /**
     * 添加选点marker
     */
    private void addMarkerInScreenCenter(LatLng latLng) {
//        LatLng latLng = mAMap.getCameraPosition().target;
        //当前位置点
        mAMap.addMarker(MarkerUtil.getMarker(latLng, 0.5f, 0.1f, R.mipmap.icon_location_circle));

        Point screenPosition = mAMap.getProjection().toScreenLocation(mAMap.getCameraPosition().target);
        //选中点
        Marker mSelectMarker = mAMap.addMarker(MarkerUtil.getMarker(0.5f, 0.5f, R.mipmap.icon_location));
        //设置Marker在屏幕上,不跟随地图移动
        mSelectMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

    }


    //搜索附近
    private void doSearchNearby() {
        if (!mLocationAdapter.isOnLoadMore()) startLoadNearby();
        PoiSearch.Query query = new PoiSearch.Query("", "", "");
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
}
