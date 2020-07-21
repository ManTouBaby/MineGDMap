package com.hy.gdlibrary.nearby;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.services.core.PoiItem;
import com.hy.gdlibrary.R;
import com.hy.gdlibrary.base.SmartVH;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * @author:MtBaby
 * @date:2020/04/17 15:43
 * @desc:
 */
public class NearbyLocationAdapter extends RecyclerView.Adapter<SmartVH> {
    private List<NearbyItemBO> poiItems = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isOnLoadMore;
    private int selectItem = 0;

    @Override
    public int getItemViewType(int position) {
        return poiItems.get(position).getItemType();
    }

    @Override
    public SmartVH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new SmartVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more_nearby, null));
        } else {
            return new SmartVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearby_show, null));
        }
    }

    @Override
    public void onBindViewHolder(SmartVH holder, int position) {
        NearbyItemBO itemBO = poiItems.get(position);
        if (itemBO.getItemType() != 1) {
            PoiItem nearbyItemBO = itemBO.getPoiItem();
            holder.getText(R.id.mt_location_name).setText(nearbyItemBO.getTitle() == null ? "" : nearbyItemBO.getTitle());
            holder.getText(R.id.mt_distance).setText(String.format("%s米", String.valueOf(nearbyItemBO.getDistance())));
            holder.getText(R.id.mt_location_road).setText(nearbyItemBO.getSnippet() == null ? "" : nearbyItemBO.getSnippet());
            holder.getImage(R.id.iv_select_tag).setVisibility(selectItem == position ? View.VISIBLE : View.GONE);
        }
        holder.getItemView().setOnClickListener(v -> {
            notifyItemChanged(selectItem);
            selectItem = position;
            notifyItemChanged(selectItem);

        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int count = layoutManager.getItemCount();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                if (!isOnLoadMore && newState == SCROLL_STATE_IDLE && lastVisiblePosition == count - 1) {
                    System.out.println("上拉加载更多...");
                    isOnLoadMore = true;
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public PoiItem getSelectItem() {
        return poiItems.get(selectItem).getPoiItem();
    }

    public void setPoiItems(List<PoiItem> poiItems) {
        selectItem = 0;
        List<NearbyItemBO> itemBOS = new ArrayList<>();
        for (int i = 0; i < poiItems.size(); i++) {
            NearbyItemBO itemBO = new NearbyItemBO();
            itemBO.setPoiItem(poiItems.get(i));
            itemBO.setItemType(2);
            itemBOS.add(itemBO);
        }
        NearbyItemBO itemBO = new NearbyItemBO();
        itemBO.setItemType(1);
        itemBOS.add(itemBO);
        this.poiItems = itemBOS;
        notifyDataSetChanged();
    }

    public boolean isOnLoadMore() {
        return isOnLoadMore;
    }

    public void loadMoreComplete() {
        isOnLoadMore = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void addPoiItems(List<PoiItem> poiItems) {
        List<NearbyItemBO> itemBOS = new ArrayList<>();
        for (int i = 0; i < poiItems.size(); i++) {
            NearbyItemBO itemBO = new NearbyItemBO();
            itemBO.setPoiItem(poiItems.get(i));
            itemBO.setItemType(2);
            itemBOS.add(itemBO);
        }
        int beforeAddCount = this.poiItems.size();
        this.poiItems.addAll(beforeAddCount - 1, itemBOS);
        notifyItemRangeInserted(beforeAddCount - 1, itemBOS.size());
    }

    @Override
    public int getItemCount() {
        return poiItems.size();
    }
}
