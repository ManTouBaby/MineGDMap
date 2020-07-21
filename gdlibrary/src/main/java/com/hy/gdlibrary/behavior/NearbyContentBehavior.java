package com.hy.gdlibrary.behavior;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hy.gdlibrary.R;

/**
 * Author: MtHappy
 * Date: 2020/7/21 17:10
 * Description: 实现滚动地图滚动效果
 */
public class NearbyContentBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {
    public NearbyContentBehavior() {
    }

    public NearbyContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        return dependency instanceof CoordinatorLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        layoutParams.width = (int) (dependency.getBottom() - dependency.getY() + 192);
        child.setLayoutParams(layoutParams);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(dependency);
        int peekHeight = bottomSheetBehavior.getPeekHeight();
        int bottom = child.getBottom();
        int i = dependency.getMeasuredHeight() - peekHeight;
        System.out.println("滚动:" + dependency.getMeasuredHeight() + "====" + dependency.getY() + "--" + bottom);
        int v = (int) (dependency.getMeasuredHeight() - dependency.getY());
        child.scrollBy(0, v);
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
