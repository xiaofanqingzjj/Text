package com.example.testpermission.ad.my;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.testpermission.ad.MyHeadLayout;

import java.util.List;

/**
 * Behavior which should be used by {@link View}s which can scroll vertically and support
 * nested scrolling to automatically scroll any {@link AppBarLayout} siblings.
 */
public class ScrollingViewBehavior extends HeaderScrollingViewBehavior {

    public ScrollingViewBehavior() {
    }

    public ScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

//        final TypedArray a = context.obtainStyledAttributes(attrs,
//                R.styleable.ScrollingViewBehavior_Layout);
//        setOverlayTop(a.getDimensionPixelSize(
//                R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
//        a.recycle();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        // We depend on any AppBarLayouts
        return dependency instanceof MyHeadLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                          View dependency) {
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    @Override
    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child,
                                                   Rect rectangle, boolean immediate) {
        final AppBarLayout header = findFirstDependency(parent.getDependencies(child));
        if (header != null) {
            // Offset the rect by the child's left/top
            rectangle.offset(child.getLeft(), child.getTop());

            final Rect parentRect = mTempRect1;
            parentRect.set(0, 0, parent.getWidth(), parent.getHeight());

            if (!parentRect.contains(rectangle)) {
                // If the rectangle can not be fully seen the visible bounds, collapse
                // the AppBarLayout
                header.setExpanded(false, !immediate);
                return true;
            }
        }
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        final CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
//        if (behavior instanceof AppBarLayout.Behavior) {
            // Offset the child, pinning it to the bottom the header-dependency, maintaining
            // any vertical gap and overlap
            final AppBarLayout.Behavior ablBehavior = (AppBarLayout.Behavior) behavior;
            ViewCompat.offsetTopAndBottom(child, (dependency.getBottom() - child.getTop())
                    + 0 //ablBehavior.mOffsetDelta
                    + getVerticalLayoutGap()
                    - getOverlapPixelsForOffset(dependency));
//        }
    }

    @Override
    float getOverlapRatioForOffset(final View header) {
        if (header instanceof AppBarLayout) {
            final AppBarLayout abl = (AppBarLayout) header;
            final int totalScrollRange = abl.getTotalScrollRange();
            final int preScrollDown = 0 ;//abl.getDownNestedPreScrollRange();
            final int offset = getAppBarLayoutOffset(abl);

            if (preScrollDown != 0 && (totalScrollRange + offset) <= preScrollDown) {
                // If we're in a pre-scroll down. Don't use the offset at all.
                return 0;
            } else {
                final int availScrollRange = totalScrollRange - preScrollDown;
                if (availScrollRange != 0) {
                    // Else we'll use a interpolated ratio of the overlap, depending on offset
                    return 1f + (offset / (float) availScrollRange);
                }
            }
        }
        return 0f;
    }

    private static int getAppBarLayoutOffset(AppBarLayout abl) {
        final CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) abl.getLayoutParams()).getBehavior();
//        if (behavior instanceof Behavior) {
//            return ((Behavior) behavior).getTopBottomOffsetForScrollingSibling();
//        }
        return 0;
    }

    @Override
    AppBarLayout findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (view instanceof AppBarLayout) {
                return (AppBarLayout) view;
            }
        }
        return null;
    }

    @Override
    int getScrollRange(View v) {
        if (v instanceof AppBarLayout) {
            return ((AppBarLayout) v).getTotalScrollRange();
        } else {
            return super.getScrollRange(v);
        }
    }
}