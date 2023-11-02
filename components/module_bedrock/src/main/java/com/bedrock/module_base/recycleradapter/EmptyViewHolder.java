package com.bedrock.module_base.recycleradapter;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 当数据源返回一个没有配置的ViewType的时候，该Item隐藏
 * <p>
 * PS:这个类还不能设置成private，因为通过Class.newInstance来创建对象
 */
public class EmptyViewHolder extends BaseViewTypeAdapter.ViewTypeViewHolder<Object> {


    public EmptyViewHolder() {
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        View emptyView = new View(getContext());
        emptyView.setLayoutParams(new RecyclerView.LayoutParams(1, 0));
        setContentView(emptyView);
    }

    @Override
    public void bindData(int position, Object data) {
        // do nothing
    }
}