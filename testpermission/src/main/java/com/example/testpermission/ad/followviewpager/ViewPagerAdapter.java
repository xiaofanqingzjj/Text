package com.example.testpermission.ad.followviewpager;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 一个可以方便创建PagerAdapter的基类
 *
 * @param <V>
 */
public abstract class ViewPagerAdapter<V extends View> extends PagerAdapter {

    private static final String ILLEGAL_STATE_ID_ON_VIEW = "The view created for position %d has an ID. Page view IDs must be set by the adapter to deal with state saving and restoring. Make sure your inflated views have an ID of View.NO_ID";

    private final Map<V, Integer> instantiatedViews = new HashMap<>();
    private final ViewIdGenerator viewIdGenerator = new ViewIdGenerator();

    private ViewPagerAdapterState viewPagerAdapterState = ViewPagerAdapterState.newInstance();

    @NonNull
    @Override
    public V instantiateItem(@NonNull ViewGroup container, int position) {
        V view = createView(container, position);
        SparseArray<Parcelable> viewState = viewPagerAdapterState.getViewState(position);

        assertViewHasNoId(position, view);
        int restoredId = viewPagerAdapterState.getId(position);
        view.setId(restoredId == View.NO_ID ? viewIdGenerator.generateViewId() : restoredId);

        bindView(view, position, viewState);
        instantiatedViews.put(view, position);
        container.addView(view);

        // key with which to associate this view
        return view;
    }

    private void assertViewHasNoId(int position, V view) {
        if (view.getId() != View.NO_ID) {
            String errorMessage = String.format(Locale.US, ILLEGAL_STATE_ID_ON_VIEW, position);
            throw new IllegalStateException(errorMessage);
        }
    }

    protected abstract V createView(ViewGroup container, int position);

    private void bindView(V view, int position, @Nullable SparseArray<Parcelable> viewState) {
        bindView(view, position);
        restoreHierarchyState(view, position, viewState);
    }

    protected abstract void bindView(V view, int position);

    protected void restoreHierarchyState(V view, int position, @Nullable SparseArray<Parcelable> viewState) {
        if (viewState != null) {
            view.restoreHierarchyState(viewState);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            SparseArray<Parcelable> viewState = viewPagerAdapterState.getViewState(position);
            bindView(entry.getKey(), position, viewState);
        }
    }

    @SuppressWarnings("unchecked")
    // `key` is the object we return in `instantiateItem(ViewGroup container, int position)`
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object key) {
        V view = (V) key;
        saveViewState(position, view);
        container.removeView(view);
        instantiatedViews.remove(view);
    }

    private void saveViewState(int position, V view) {
        SparseArray<Parcelable> viewState = new SparseArray<>();
        saveHierarchyState(view, position, viewState);
        viewPagerAdapterState.put(view.getId(), position, viewState);
    }

    /**
     * Save state on the given page view.
     *
     * @param view      the page view to restore state on
     * @param position  the position of the data set that is to be represented by this view
     * @param viewState the state of the view
     */
    protected void saveHierarchyState(V view, int position, SparseArray<Parcelable> viewState) {
        view.saveHierarchyState(viewState);
    }

    @Override
    public Parcelable saveState() {
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            V view = entry.getKey();
            saveViewState(position, view);
        }
        return viewPagerAdapterState;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state instanceof ViewPagerAdapterState) {
            this.viewPagerAdapterState = ((ViewPagerAdapterState) state);
        } else {
            super.restoreState(state, loader);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object key) {
        return view.equals(key);
    }
}


class ViewIdGenerator {

    int generateViewId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            return PreApi17ViewIdGenerator.generateViewId();
        }
    }

    private static final class PreApi17ViewIdGenerator { // copied (and reformatted) from View

        private static final AtomicInteger NEXT_GENERATED_ID = new AtomicInteger(1);
        private static final int START_OF_AAPT_GENERATED_IDS = 0x00FFFFFF;

        private PreApi17ViewIdGenerator() {
            // utility class
        }

        /**
         * Generate a value suitable for use in {@link #setId(int)}.
         * This value will not collide with ID values generated at build time by aapt for R.id.
         *
         * @return a generated ID value
         */
        static int generateViewId() {
            while (true) {
                final int result = NEXT_GENERATED_ID.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > START_OF_AAPT_GENERATED_IDS) {
                    newValue = 1; // Roll over to 1, not 0.
                }
                if (NEXT_GENERATED_ID.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
    }
}


class ViewPagerAdapterState implements Parcelable {

    public static final Creator<ViewPagerAdapterState> CREATOR = new Creator<ViewPagerAdapterState>() {

        public ViewPagerAdapterState createFromParcel(Parcel in) {
            return ViewPagerAdapterState.from(in);
        }

        public ViewPagerAdapterState[] newArray(int size) {
            return new ViewPagerAdapterState[size];
        }
    };

    private static final String KEY_VIEW_IDS = "id";
    private static final String KEY_VIEW_STATE = "state";

    private final SparseIntArray viewIds;
    private final SparseArray<SparseArray<Parcelable>> viewStates;

    public static ViewPagerAdapterState newInstance() {
        SparseIntArray viewIds = new SparseIntArray();
        SparseArray<SparseArray<Parcelable>> viewStates = new SparseArray<>();
        return new ViewPagerAdapterState(viewIds, viewStates);
    }

    private static ViewPagerAdapterState from(Parcel in) {
        Bundle bundle = in.readBundle(ViewPagerAdapterState.class.getClassLoader());
        SparseArray<SparseArray<Parcelable>> viewStates = extractViewStatesFrom(bundle);
        SparseIntArray viewIds = extractIdsFrom(bundle);
        return new ViewPagerAdapterState(viewIds, viewStates);
    }

    private static SparseIntArray extractIdsFrom(Bundle bundle) {
        SparseIntArray output = new SparseIntArray();
        int[] ids = bundle.getIntArray(KEY_VIEW_IDS);
        for (int index = 0; index < ids.length; index++) {
            output.put(index, ids[index]);
        }
        return output;
    }

    private static SparseArray<SparseArray<Parcelable>> extractViewStatesFrom(Bundle bundle) {
        Bundle viewStateBundle = bundle.getBundle(KEY_VIEW_STATE);
        SparseArray<SparseArray<Parcelable>> viewStates = new SparseArray<>(viewStateBundle.keySet().size());
        for (String key : viewStateBundle.keySet()) {
            SparseArray<Parcelable> sparseParcelableArray = viewStateBundle.getSparseParcelableArray(key);
            viewStates.put(Integer.parseInt(key), sparseParcelableArray);
        }
        return viewStates;
    }

    private ViewPagerAdapterState(SparseIntArray viewIds, SparseArray<SparseArray<Parcelable>> viewStates) {
        this.viewIds = viewIds;
        this.viewStates = viewStates;
    }

    public void put(int viewId, int position, SparseArray<Parcelable> viewState) {
        viewIds.put(position, viewId);
        viewStates.put(position, viewState);
    }

    public SparseArray<Parcelable> getViewState(int position) {
        return viewStates.get(position);
    }

    public int getId(int position) {
        return viewIds.get(position, View.NO_ID);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        int[] viewIds = viewIdsArray();
        bundle.putIntArray(KEY_VIEW_IDS, viewIds);
        Bundle viewStateBundle = viewStateBundle();
        bundle.putBundle(KEY_VIEW_STATE, viewStateBundle);
        dest.writeBundle(bundle);
    }

    private int[] viewIdsArray() {
        int[] output = new int[viewIds.size()];
        for (int index = 0; index < viewIds.size(); index++) {
            output[index] = viewIds.get(index);
        }
        return output;
    }

    private Bundle viewStateBundle() {
        Bundle viewStateBundle = new Bundle();
        for (int i = 0; i < viewStates.size(); i++) {
            SparseArray<Parcelable> viewState = viewStates.get(i);
            viewStateBundle.putSparseParcelableArray(String.valueOf(i), viewState);
        }
        return viewStateBundle;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}