package com.bedrock.module_base.listviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.HashMap;
import java.util.List;


/**
 * 支持ViewTypeData的Adapter
 *
 * 该类允许子类设置特定的ViewType，并为每个ViewType配置一个特定的ViewTypeViewHolder类，并把不同ViewType
 * 绑定数据的代码分发到特定的ViewTypeHolder,以防止Adapter代码的臃肿
 *
 * ViewTypeViewHolder允许配置一个特定的布局，以及数据版定到UI上的方法
 *
 * Created by fortunexiao on 2016/3/2.
 */
public class BaseViewTypeAdapter<T> extends BaseAdapter<T> {

    private HashMap<Integer,  Class<? extends ViewTypeViewHolder>> mItems = new HashMap<>();

    public BaseViewTypeAdapter(Context context, List<T> objects) {
        super(context, objects);
    }

    /**
     * 增加一个ViewType
     * @param viewType viewType
     * @param viewHolderClass ViewType对应的ViewHolder类
     */
    public void addViewType(int viewType, Class<? extends ViewTypeViewHolder> viewHolderClass) {
        mItems.put(viewType, viewHolderClass);
    }

    @Override
    public int getItemViewType(int position) {
        T t = getItem(position);
        if (t != null && t instanceof ViewTypeData) {
            return ((ViewTypeData)t).getViewType();
        }
        return 0;
//        int vt = t.getViewType();
//        return vt;
//        if (mItems.keySet().contains(vt)) {  // 只有配置了ViewHolder的ViewType才有效，否则不显示
//            return vt;
//        } else {
//            //未知的ViewType类型
//            return mItems.size();
//        }
    }

    @Override
    public int getViewTypeCount() {
        if (mItems.size() > 0) {
            return mItems.size() + 1; // 增加一种未知类型的ViewType
        }
        return super.getViewTypeCount();
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<? extends ViewTypeViewHolder> viewHolderClazz = mItems.get(viewType);
        ViewTypeViewHolder holder;
        if (viewHolderClazz == null) {
            viewHolderClazz = EmptyViewHoler.class;
        }

        holder = createViewHolder(viewHolderClazz);
        holder.setContext(getContext());
        holder.onCreate();
        holder.onPostCreate();

        onViewHolderCreated(holder);
        return holder;
    }

    private ViewTypeViewHolder createViewHolder(Class<? extends ViewTypeViewHolder> viewHolderClazz) {
        ViewTypeViewHolder holder;
        try {
            holder = viewHolderClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return holder;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof ViewTypeViewHolder) {
            ViewTypeViewHolder vvt = (ViewTypeViewHolder) holder;
            vvt.bindData(position, getItem(position));
        }
    }

    /**
     * ViewHolder被创建的回调
     * 允许用户在ViewHolder创建的时候个性初始化ViewHolder
     * @param holder 创建的ViewHolder
     */
    protected void onViewHolderCreated(ViewTypeViewHolder holder) { }

    /**
     * 当用户配置ViewType的时候同时需要配置一个ViewTypeViewHolder类
     * 该类负责为特定的ViewType绑定数据
     * @param <T>
     */
    public static abstract class ViewTypeViewHolder<T> extends ViewHolder {
        private Context context;

        private void setContext(Context context) {
            this.context = context;
        }

        /**
         * ViewTypeViewHolder被创建的生命周期回调
         * 用户应该在该方法中通过setContentView设置当前Item的布局
         */
        protected void onCreate() { }

        /**
         * 在onCreate之后调用
         * 这个方法看着没什么用，实际上很有用
         * 比如：在子类的onCreate里设置setContentView，父类需要处理一些公共的View的时候，可以在该方法里绑定View
         * 因为findViewById一定要在setContentView之后调用才能拿到对应的View
         */
        protected void onPostCreate() {}

        /**
         * 设置该ViewHolder的界面布局
         * @param resId 布局ID
         */
        public void setContentView(int resId) {
            View itemView = LayoutInflater.from(getContext()).inflate(resId, null);
            setContentView(itemView);
        }

        /**
         * 当Adapter getView的时候会调用该方法，要求把数据绑定到ViewHolder上
         * @param position position
         * @param data position对应的数据
         */
        protected abstract void bindData(int position, T data);

        /**
         * 获取上下文
         * @return 上下文
         */
        public Context getContext() {
            return context;
        }

    }

    /**
     * 要支持ViewTypeAdapter数据源必须实现该接口
     */
    public interface ViewTypeData {

        /**
         * 返回数据源对应的viewType
         * @return viewType
         */
        int getViewType();
    }


    /**
     * 当数据源返回一个没有配置的ViewType的时候，该Item隐藏
     */
    public static class EmptyViewHoler extends ViewTypeViewHolder {

        @Override
        protected void onCreate() {
            super.onCreate();
            View emptyView = new View(getContext());

            emptyView.setLayoutParams(new AbsListView.LayoutParams(0, 0));
            setContentView(emptyView);
        }

        @Override
        public void bindData(int position, Object data) {
            // do nothing
        }
    }
}
