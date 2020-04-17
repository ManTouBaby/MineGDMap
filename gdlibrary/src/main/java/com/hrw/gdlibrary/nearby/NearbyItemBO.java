package com.hrw.gdlibrary.nearby;

import com.amap.api.services.core.PoiItem;

/**
 * @author:MtBaby
 * @date:2020/04/17 17:35
 * @desc:
 */
public class NearbyItemBO {
    private PoiItem poiItem;
    private int itemType;

    public PoiItem getPoiItem() {
        return poiItem;
    }

    public void setPoiItem(PoiItem poiItem) {
        this.poiItem = poiItem;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
