package com.example.testpermission;

import android.content.Context;
import android.os.Bundle;
import androidx.core.app.FragmentManager;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 支持ViewTypeData的Adapter for RecycleView
 *
 * 该类允许子类设置特定的ViewType，并为每个ViewType配置一个特定的ViewTypeViewHolder类，并把不同ViewType
 * 绑定数据的代码分发到特定的ViewTypeHolder,以防止Adapter代码的臃肿
 *
 * ViewTypeViewHolder允许配置一个特定的布局，以及数据绑定到UI上的方法
 *
 * DEMO:
 * <code>
      class Data implements BaseViewTypeAdapter.ViewTypeData {
            int viewType;
            public int getViewType() {
                return viewType;
            }
        }

       class VC1 extends ViewTypeViewHolder<Data> {
            protected void onViewHolderCreate() {
                super.onViewHolderCreate();
                setContentView(R.layout.list_item1);
            }
            protected void bindData(int position, Data data) {
                // binding data
            }
        }

       class VC2 extends ViewTypeViewHolder<Data> {
            protected void onViewHolderCreate() {
                super.onViewHolderCreate();
                setContentView(R.layout.list_item2);
            }
            protected void bindData(int position, Data data) {
                // binding data
            }
       }

        class CardAdapter extends BaseViewTypeAdapter<Data> {
            public CardAdapter(Context context, List<Data> datas) {
                super(context, datas);
                addViewType(0, VC1.class);
                addViewType(1, VC2.class);
            }
        }

            Userage:

        RecyclerView rv = (RecyclerView) findViewById(R.id.my_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Random random = new Random();
        List<Data> datas = new ArrayList<>();
        for (int i=0; i<30; i++) {
            Data d= new Data();
            d.viewType = random.nextInt(3);
            datas.add(d);
        }
        CardAdapter adapter = new CardAdapter(this, datas);
        rv.setAdapter(adapter);
            </code>
 * Created by fortunexiao on 2016/12/15.
 */
public class BaseViewTypeAdapter<T> extends RecyclerView.Adapter {

//    private static final String TAG = BaseViewTypeAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_DEFAULT = 1;

    private final Context mContext;
    private final SparseArray<Class<? extends ViewTypeViewHolder>> mItems = new SparseArray<>();

    protected List<T> mData = new ArrayList<>();

    private ViewTypeViewHolderHook<T> viewTypeViewHolderHook;

    private FragmentManager fragmentManager;

    /**
     * 构造器
     *
     * @param context 上下文
     * @param data   数据列表
     */
    public BaseViewTypeAdapter(Context context, List<T> data) {
        this(context, null, data);
    }

    public BaseViewTypeAdapter(Context context, FragmentManager fragmentManager, List<T> data) {
        this.mContext = context;
        this.mData = data;
        this.fragmentManager = fragmentManager;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 当List只有一种ViewType的时候使用默认的ViewType
     * @param viewHolderClass ViewTypeViewHolder
     */
    public void addViewType(Class<? extends ViewTypeViewHolder> viewHolderClass) {
        addViewType(VIEW_TYPE_DEFAULT, viewHolderClass);
    }

    /**
     * 增加一个ViewType
     * @param viewType        viewType
     * @param viewHolderClass ViewType对应的ViewHolder类
     */
    public void addViewType(int viewType, Class<? extends ViewTypeViewHolder> viewHolderClass) {
        mItems.put(viewType, viewHolderClass);
    }

    public void setViewTypeViewHolderHook(ViewTypeViewHolderHook<T> viewTypeViewHolderHook) {
        this.viewTypeViewHolderHook = viewTypeViewHolderHook;
    }

    @Override
    public int getItemViewType(int position) {
        T t = getItem(position);
        if (t != null && t instanceof ViewTypeData) {
            return ((ViewTypeData)t).getViewType();
        }
        return VIEW_TYPE_DEFAULT;
    }

    public void addDatas(List<T> datas) {
        if (datas != null) {
            if (this.mData == null) {
                this.mData = new ArrayList<>();
            }
            this.mData.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void addData(T data) {
        if (data != null) {
            if (this.mData == null) {
                this.mData = new ArrayList<>();
            }
            this.mData.add(data);

            notifyDataSetChanged();
        }
    }

    /**
     * 删除一个item
     * <br/>1.默认调用notifyItemRemoved更新界面
     * <br/>2.当数据全部被删除完时，调用notifyDataSetChanged更新界面
     * @param data
     */
    public void removeData(T data) {
        removeData(data, false);
    }

    /**
     *  删除一个item
     * @param data
     * @param isNotifyDataSetChanged true 调用notifyDataSetChanged更新界面；false 调用notifyItemRemoved更新界面
     */
    public void removeData(T data, boolean isNotifyDataSetChanged) {
        if (data == null || mData == null) {
            return;
        }
        int removePosition = mData.indexOf(data);
        if (removePosition == -1) {
            return;
        }

        mData.remove(data);

        if(isNotifyDataSetChanged || mData.size() == 0) {
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(removePosition);
        }

    }


    public List<T> getDatas() {
        return mData;
    }

    public void setDatas(List<T> datas) {
        if (datas != null) {
            this.mData = new ArrayList<>(datas);
        } else {
            this.mData = null;
        }
        notifyDataSetChanged();
    }

    @Override
    public final StubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<? extends ViewTypeViewHolder> viewHolderClazz = mItems.get(viewType);

        if (viewHolderClazz == null) {
            viewHolderClazz = DefaultViewHolder.class;
        }

        ViewTypeViewHolder holder;
        try {
            holder = viewHolderClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        holder.setContext(mContext);
        holder.fragmentManager = fragmentManager;
        holder.setAdapter(this);
        holder.setViewTypeViewHolderHook(viewTypeViewHolderHook);

        holder.onCreate();
        onViewHolderCreated(holder);
        holder.onPostCreate();

        // 在onCreate方法
        if (holder.viewHolder == null) {
            throw new RuntimeException("You should call setContentView in onViewHolderCreate method");
        }

        return holder.viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StubViewHolder) {
            StubViewHolder<T> svh = (StubViewHolder<T>) holder;
            if (svh.viewTypeViewHolder != null) {
                svh.viewTypeViewHolder.bindDataInner(position, getItem(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    /**
     * ViewHolder被创建的回调
     * 允许用户在ViewHolder创建的时候个性初始化ViewHolder
     *
     * 该方法在ViewHolder的onCreate之后在onPostCreate之前调用
     *
     * @param holder 创建的ViewHolder
     */
    protected void onViewHolderCreated(ViewTypeViewHolder holder) {

        // 首先判断Adapter是否设置了OnItemClickListener
        if (onItemClickListener != null) {

            // 在看下Adapter的onItemClick是否自己处理了，如果处理了，则忽略ViewHolder的onItemClick
            final OnItemClickListener orgHolderItemClickListener = holder.onItemClickListener;

            holder.onItemClickListener = new OnItemClickListener<T>() {
                @Override
                public boolean onItemClick(int position, T data) {
                    if (!onItemClickListener.onItemClick(position, data)) {
                        if (orgHolderItemClickListener != null) {
                            orgHolderItemClickListener.onItemClick(position, data);
                        }
                    }
                    return false;
                }
            };
        }

        if (onItemLongClickListener != null) {

            // 在看下Adapter的onItemClick是否自己处理了，如果处理了，则忽略ViewHolder的onItemClick
            final OnItemLongClickListener orgHolderItemLongClickListener = holder.onItemLongClickListener;

            holder.onItemLongClickListener = new OnItemLongClickListener<T>() {
                @Override
                public boolean onItemLongClick(int position, T data) {
                    if (!onItemLongClickListener.onItemLongClick(position, data)) {
                        if (orgHolderItemLongClickListener != null) {
                            orgHolderItemLongClickListener.onItemLongClick(position, data);
                        }
                    }
                    return false;
                }
            };

        }
    }

    protected T getItem(int position) {
        return (mData != null && position < mData.size()) ? mData.get(position) : null;
    }


    /*Item点击监听器*/

    private OnItemClickListener<T> onItemClickListener;

    /**
     * 设置点击Item监听
     * @param listener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    /**
     * Adapter也有onItemClickListener
     * @param <U>
     */
    public interface OnItemClickListener<U> {
        boolean onItemClick(int position, U data);
    }


    /*Item点击监听器*/

    private OnItemLongClickListener<T> onItemLongClickListener;

    /**
     * 设置点击Item监听
     * @param listener 监听器
     */
    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.onItemLongClickListener = listener;
    }

    /**
     * Adapter也有onItemClickListener
     * @param <U>
     */
    public interface OnItemLongClickListener<U> {
        boolean onItemLongClick(int position, U data);
    }


    /**
     * 当用户配置ViewType的时候同时需要配置一个ViewTypeViewHolder类
     * 该类负责为特定的ViewType绑定数据
     *
     * @param <U>
     */
    public static abstract class ViewTypeViewHolder<U> {

        private StubViewHolder<U> viewHolder;
        private BaseViewTypeAdapter mAdapter;

        private Context context;

        private int currentBindPosition;
        private U currentBindData;

        private Bundle mArgs;

        private ViewTypeViewHolderHook<U> viewTypeViewHolderHook;

        private FragmentManager fragmentManager;

        public final Map<String, Object> extra = new HashMap<>();

        // === methods ===


        public Bundle getArgs() {
            return mArgs;
        }

        public void setArgs(Bundle mArgs) {
            this.mArgs = mArgs;
        }

        private void setContext(Context context) {
            this.context = context;
        }

        public final Context getContext() {
            return context;
        }

        public FragmentManager getFragmentManager() {
            return fragmentManager;
        }

        protected void onCreate() {
            if (viewTypeViewHolderHook != null) {
                viewTypeViewHolderHook.onViewHolderCreate(this);
            }
        }

        protected void onPostCreate() {
            if (viewTypeViewHolderHook != null) {
                viewTypeViewHolderHook.onViewHoldPostCreate(this);
            }
        }

        public void setContentView(int resId) {
            setContentViewInner(LayoutInflater.from(getContext()).inflate(resId, null));
        }

        public void setContentView(View view) {
            setContentViewInner(view);
        }

        private void setContentViewInner(View view) {
            viewHolder = new StubViewHolder<>(view, this);

//            ButterKnife.bind(this, viewHolder.itemView);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(currentBindPosition, currentBindData);
                    }
                    onItemClick(currentBindPosition, currentBindData);
                    if (viewTypeViewHolderHook != null) {
                        viewTypeViewHolderHook.onViewHolderItemClick(ViewTypeViewHolder.this, currentBindData);
                    }
                }
            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(currentBindPosition, currentBindData);
                    }
                    boolean isHandle = onItemLongClick(currentBindPosition, currentBindData);
                    if (viewTypeViewHolderHook != null) {
                        viewTypeViewHolderHook.onViewHolderItemLongClick(ViewTypeViewHolder.this, currentBindData);
                    }
                    return isHandle;
                }
            });

            if (viewTypeViewHolderHook != null) {
                viewTypeViewHolderHook.onViewHolderSetContentViewView(this, viewHolder.itemView);
            }
        }

        public void setViewTypeViewHolderHook(ViewTypeViewHolderHook<U> viewTypeViewHolderHook) {
            this.viewTypeViewHolderHook = viewTypeViewHolderHook;
        }

        private void setAdapter(BaseViewTypeAdapter adapter) {
            this.mAdapter = adapter;
        }

        public BaseViewTypeAdapter getAdapter() {
            return mAdapter;
        }

        public <T extends View> T findViewById(int id) {
            if (viewHolder != null) {
                return viewHolder.itemView.findViewById(id);
            }
            return null;
        }

        private void bindDataInner(int position, U data) {
            currentBindPosition = position;
            currentBindData = data;
            bindData(position, data);
            if (viewTypeViewHolderHook != null) {
                viewTypeViewHolderHook.onViewHolderBindData(this, data);
            }
        }

        /**
         * 绑定item的数据
         *
         * @param position 位置
         * @param data     数据
         */
        protected abstract void bindData(int position, U data);

        /**
         * 返回当前ViewHolder bind的Data对象
         * @return 当前绑定的数据对象
         */
        protected U getCurrentBindData() {
            return currentBindData;
        }

        /**
         * 返回当前ViewHolder绑定的position
         * @return ViewHolder绑定的position
         */
        protected int getCurrentBindPosition() {
            return currentBindPosition;
        }

        protected View getItemView() {
            return viewHolder != null ? viewHolder.itemView : null;
        }

        /*Item点击监听器*/

        private OnItemClickListener<U> onItemClickListener;
        private OnItemLongClickListener<U> onItemLongClickListener;

        protected void onItemClick(int position, U data) {}

        protected boolean onItemLongClick(int position, U data) { return false;}

//        /**
//         * 设置点击Item监听
//         * @param listener 监听器
//         */
//        private void setOnItemClickListener(OnItemClickListener<U> listener) {
//            this.onItemClickListener = listener;
//        }
//
//        private OnItemClickListener<U> getOnItemClickListener() {
//            return onItemClickListener;
//        }
//
//        /**
//         * ViewHolder 有自己的onItemClickListener监听器
//         * @param <K>
//         */
//        private interface OnItemClickListener<K> {
//            void onItemClick(int position, K data);
//        }


        public final boolean isRecyclable() {
            return viewHolder.isRecyclable();
        }
    }


    /**
     * ViewHolder的一些方法的Hook，允许外部通过后门来做一些羞羞的事
     * @param <T>
     */
    public interface ViewTypeViewHolderHook<T> {
        void onViewHolderCreate(ViewTypeViewHolder<T> holder);
        void onViewHolderSetContentViewView(ViewTypeViewHolder<T> holder, View contentView);
        void onViewHoldPostCreate(ViewTypeViewHolder<T> holder);
        void onViewHolderBindData(ViewTypeViewHolder<T> holder, T data);
        void onViewHolderItemClick(ViewTypeViewHolder<T> holder, T data);
        void onViewHolderItemLongClick(ViewTypeViewHolder<T> holder, T data);
    }

    public static class AbsViewTypeViewHolderHook<T> implements ViewTypeViewHolderHook<T> {

        @Override
        public void onViewHolderCreate(ViewTypeViewHolder<T> holder) {}

        @Override
        public void onViewHolderSetContentViewView(ViewTypeViewHolder<T> holder, View contentView) {}

        @Override
        public void onViewHoldPostCreate(ViewTypeViewHolder<T> holder) {

        }

        @Override
        public void onViewHolderBindData(ViewTypeViewHolder<T> holder, T data) {}

        @Override
        public void onViewHolderItemClick(ViewTypeViewHolder<T> holder, T data) {}

        @Override
        public void onViewHolderItemLongClick(ViewTypeViewHolder<T> holder, T data) {}
    }


    public final static class StubViewHolder<U> extends RecyclerView.ViewHolder {
        public final ViewTypeViewHolder<U> viewTypeViewHolder;

        private StubViewHolder(View itemView, ViewTypeViewHolder<U> viewTypeViewHolder) {
            super(itemView);
            this.viewTypeViewHolder = viewTypeViewHolder;
        }
    }

    /**
     * 要支持ViewTypeAdapter数据源必须实现该接口
     */
    public interface ViewTypeData {

        /**
         * 返回数据源对应的viewType
         *
         * @return viewType
         */
        int getViewType();
    }

    /**
     * 当数据源返回一个没有配置的ViewType的时候，该Item隐藏
     *
     * PS:这个类还不能设置成private，因为通过Class.newInstance来创建对象
     */
    public final static class DefaultViewHolder<T> extends ViewTypeViewHolder<T> {

        @Override
        protected void onCreate() {
            super.onCreate();
            View emptyView = new View(getContext());
            emptyView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            setContentView(emptyView);
        }

        @Override
        public void bindData(int position, T data) {
            // do nothing
        }
    }
}
