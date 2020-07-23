package com.hy.gdlibrary.track;

import android.app.Application;
import android.content.Context;

import com.amap.api.track.AMapTrackClient;

/**
 * Author: MtHappy
 * Date: 2020/7/23 17:25
 * Description: 地图轨迹管理类
 */
public class MapTrackManger {
    private static MapTrackManger mMapTrackManger;
    private AMapTrackClient mAMapTrackClient;

    private MapTrackManger(Context application) {
        mAMapTrackClient = new AMapTrackClient(application);
    }

    public static MapTrackManger getInstance(Context application) {
        if (mMapTrackManger == null) {
            synchronized (MapTrackManger.class) {
                if (mMapTrackManger == null) {
                    mMapTrackManger = new MapTrackManger(application);
                }
            }
        }
        return mMapTrackManger;
    }

    public AMapTrackClient getAMapTrackClient() {
        return mAMapTrackClient;
    }

    //查询里程
    public void queryDistance() {
//        mAMapTrackClient.queryDistance();
    }

    //查询历史轨迹
    public void queryHistoryTrack() {

    }

    //查询最新轨迹点
    public void queryLatestPoint() {

    }

    //查询terminal
    public void queryTerminal() {

    }

    //查询终端下的轨迹
    public void queryTerminalTrack() {

    }
}
