package com.example.testpermission.ad;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.testpermission.DensityUtil;
import com.example.testpermission.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 相册下面的小点
 * Created by fortunexiao on 2016/3/2.
 */
public class DotView extends HorizontalScrollView {

    static final String TAG = DotView.class.getSimpleName();

    private int resId;
    private int dotSize;
    private int dotPadding;

    private List<View> mDotViews;

    private LinearLayout root;

    public DotView(Context context) {
        super(context);
        init();
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);

        root = new LinearLayout(getContext());
        root.setGravity(Gravity.CENTER);
        addView(root, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        dotSize = DensityUtil.dip2px(getContext(), 6);
        dotPadding = DensityUtil.dip2px(getContext(), 4);

        mDotViews = new ArrayList<>();


        setDotDrawable(R.drawable.discover_top_ad_gallery_dot);
    }

    /**
     * 设置小点的图片资源
     * 资源必须是selector的状态列表，切包含正常状态和selected状态
     * @param resId
     */
    public void setDotDrawable(int resId) {
        this.resId = resId;
    }

    public void setDotCount(int count) {
        Log.v(TAG, "setDotCount:" + count);
        if (count <= 0) {
            return;
        }

        root.removeAllViews();
        mDotViews.clear();

        MarginLayoutParams mlp = new MarginLayoutParams(dotSize, dotSize);

        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(resId);

            mDotViews.add(imageView);

            FrameLayout container = new FrameLayout(getContext());
            container.setPadding(dotPadding, 0, dotPadding, 0);
            container.addView(imageView, mlp);

            root.addView(container);
        }

        if (mDotViews.size() > 0) {
            mDotViews.get(0).setSelected(true);
        }
    }

    public void selectedDot(int index) {
        if (index >= mDotViews.size() || index < 0) {
            return;
        }

        for (int i = 0; i <mDotViews.size() ; i++) {
            ImageView view = (ImageView) mDotViews.get(i);
            view.setSelected(false);
        }
        ImageView view = (ImageView) mDotViews.get(index);
        view.setSelected(true);
        scrollToCenter(view);
    }


    private void scrollToCenter(View view) {
        //首先获取View的中点
        int[] viewLoc = new int[2];
        view.getLocationInWindow(viewLoc);
        int viewCenterX = viewLoc[0] + view.getWidth() /2;
        // 获取屏幕的水平中点
        int windowCenterX = getWidth() / 2;
        // 要滑动到中间点View的中点减去屏幕的中点为ScrollView要滑动的距离
        int dx =  viewCenterX - windowCenterX;
        // 滑动ScrollView
        smoothScrollBy(dx, 0);
    }
}
