package com.bedrock.module_base.listviewadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;


/**
 * 通用的Adapter支持RecyleView形式的Adapter使用方式
 *
 * Created by fortunexiao on 2016/3/1.
 */
public abstract class BaseAdapter<T> extends ArrayAdapter<T> {

    static final String TAG = BaseAdapter.class.getSimpleName();

    public BaseAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = onCreateViewHolder(parent, getItemViewType(position));
            holder.setAdapter(this);
            convertView = holder.getItemView();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, position);
        return convertView;
    }


    protected abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    protected abstract  void onBindViewHolder(ViewHolder holder, int position);

    /**
     * ViewHolder表示ItemView的容器
     * 每ViewHolder包含一个ListView的ItemView
     */
    public static class ViewHolder {

        private View itemView;

        private android.widget.BaseAdapter mAdapter;

        public ViewHolder() {
        }

        public ViewHolder(View v) {
            setContentView(v);
        }

        public void setContentView(View itemView) {
            this.itemView = itemView;
//            ButterKnife.bind(this, itemView);
        }

//        protected void init(View v) {
//            itemView = v;
//            ButterKnife.bind(this, itemView);
//        }

        private void setAdapter(android.widget.BaseAdapter adapter) {
            this.mAdapter = adapter;
        }

        public android.widget.BaseAdapter getAdapter() {
            return mAdapter;
        }

        public View getItemView() {
            return itemView;
        }

        public View findViewById(int id) {
            if (itemView != null) {
                return itemView.findViewById(id);
            }
            return null;
        }
    }

}
