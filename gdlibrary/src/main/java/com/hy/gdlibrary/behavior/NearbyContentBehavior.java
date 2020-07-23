package com.hy.gdlibrary.behavior;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
    int childHeight = 0;

    public NearbyContentBehavior() {
    }

    public NearbyContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        return dependency instanceof CoordinatorLayout;
    }

    int originalY = 0;

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        if (childHeight == 0) {
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            DisplayMetrics metrics = dependency.getResources().getDisplayMetrics();
            childHeight = layoutParams.height = (int) ((int) dependency.getY() + 64 * metrics.density);
            layoutParams.width = metrics.widthPixels;
            child.setLayoutParams(layoutParams);
        }

        if (originalY != 0) {
            float space = dependency.getY() - originalY;
            child.scrollBy(0, -(int) space / 2);
        }
        originalY = (int) dependency.getY();
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
