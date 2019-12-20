package com.ch.animdemo.demo.transition.imageviewer.viewpager;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;


/**
 * 一个可以方便创建PagerAdapter的基类
 *
 * 这个是专门给FollowPagerAdapter
 *
 *
 * @param <V>
 *
 * @author  fortunexiao
 */
public abstract class ViewPagerAdapter<V extends View> extends FollowPagerAdapter {

    static final String TAG = "ViewPagerAdapter";

    public final Map<V, Integer> instantiatedViews = new HashMap<>();


    /**
     * 回收器，回收的时候，会把位置一起存起来，使用的时候优先使用同位置的Item
     *
     */
    private Recycler mRecycler = new Recycler();

    boolean withRecycler = true;

    public void setWithRecycler(boolean withRecycler) {
        this.withRecycler = withRecycler;
    }

    @NonNull
    @Override
    public V instantiateItem(@NonNull ViewGroup container, int position) {




        // 先从回收器中使用View，看有没有能用的
        View convertView = null;

        if (withRecycler) {
            convertView = mRecycler.from(position);
        }

        Log.i(TAG,"instantiateItem, fromRecycer pos:" + position + ", view:" + convertView + ", recyclerSize:" + mRecycler.mRecyclerBin.size());

        V view = createView(container, convertView, position);
        bindView(view, position);
        instantiatedViews.put(view, position);
        container.addView(view);
        return view;
    }


    protected abstract V createView(ViewGroup container, View convertView, int position);

    protected abstract void bindView(V view, int position);


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            bindView(entry.getKey(), position);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @SuppressWarnings("unchecked")
    // `key` is the object we return in `instantiateItem(ViewGroup container, int position)`
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object key) {
        V view = (V) key;
        container.removeView(view);
        instantiatedViews.remove(view);

        // 移除屏幕的View放入回收器中
        if (withRecycler) {
            mRecycler.add(position, view);
        }

        Log.d(TAG,"destroyItem, addTopRecycer pos:" + position + ", view:" + view + ", recyclerSize:" + mRecycler.mRecyclerBin.size());
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object key) {
        return view.equals(key);
    }


    /**
     * 一个简单的View回收器
     */
    private static class Recycler {
        /**
         * 回收器，回收的时候，会把位置一起存起来，使用的时候优先使用同位置的Item
         *
         */
        private Map<Integer, View> mRecyclerBin = new HashMap<>();

        /**
         * 把View保存在回收器
         * @param position
         * @param view
         */
        void add(int position, View view) {
            mRecyclerBin.put(position, view);
        }

        /**
         * 从回收器中获取一个可用的View
         * @param position
         * @return
         */
        View from(int position) {
            if (mRecyclerBin.containsKey(position)) {
                return mRecyclerBin.remove(position);
            } else {
                Object[] keys = mRecyclerBin.keySet().toArray();
                if (keys != null && keys.length > 0) {
                    Integer someKey = (Integer)keys[0];
                    return mRecyclerBin.remove(someKey);
                }
            }
            return null;
        }
    }
}
