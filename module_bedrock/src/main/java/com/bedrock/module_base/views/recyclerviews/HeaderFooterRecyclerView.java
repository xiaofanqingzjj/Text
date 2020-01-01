package com.bedrock.module_base.views.recyclerviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * 支持添加header和footer的RecyclerView
 *
 * Created on 2015/11/27 by donnyliu.
 */
public class HeaderFooterRecyclerView extends RecyclerView {

    static final String TAG = "HeaderFooterRecyclerView";

    private Adapter proxyAdapter;


    private AdapterDataObserver proxyAdapterObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter adapter = getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            Adapter adapter = getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            Adapter adapter = getAdapter();
            if (adapter != null) {
                adapter.notifyItemRangeChanged(positionStart + headers.size(), itemCount, payload);
            }
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            // do nothing
            Adapter adapter = getAdapter();
            if (adapter != null) {
                adapter.notifyItemRangeInserted(positionStart+ headers.size(), itemCount);
            }
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            Adapter adapter = getAdapter();
            if (adapter != null) {
                adapter.notifyItemRangeRemoved(positionStart+ headers.size(), itemCount);
            }
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            Adapter adapter = getAdapter();
            if (adapter != null) {
                adapter.notifyItemMoved(fromPosition + headers.size(), itemCount);
            }
        }
    };

    private final List<View> headers =  new ArrayList<>();
    private final List<View> footers = new ArrayList<>();


    public HeaderFooterRecyclerView(Context context) {
        super(context);
        init(null, 0);
    }

    public HeaderFooterRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HeaderFooterRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    protected void init(AttributeSet attrs, int defStyle) {
//        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(
//                1,
//                StaggeredGridLayoutManager.VERTICAL
//        );
        setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public LayoutParams generateDefaultLayoutParams() {
                return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,   ViewGroup.LayoutParams.WRAP_CONTENT);
//                return super.generateDefaultLayoutParams();
            }

            @Override
            public void collectAdjacentPrefetchPositions(int dx, int dy, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
                try {
                    super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
                } catch (Exception e) {
                }
            }
        });

//        setItemAnimator(null);

        //addItemDecoration(new GridSpacingDecoration(Math.round(gap / 2), Math.round(gap)));

//        headers = new ArrayList<>();
//        footers = new ArrayList<>();
        addOnScrollListener(new OnRcvScrollListener() {

            @Override
            public void onBottom(RecyclerView rv) {
                HeaderFooterRecyclerView.this.onBottom();
            }
        });
    }

    protected void onBottom() {
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (proxyAdapter != null) {
            proxyAdapter.unregisterAdapterDataObserver(proxyAdapterObserver);
        }

        proxyAdapter = adapter;

        if (proxyAdapter != null) {
            proxyAdapter.registerAdapterDataObserver(proxyAdapterObserver);
        }

        super.setAdapter(new HeaderFooterAdapter());
    }

    public void addHeaderView(View view) {
//        if (getAdapter() != null)
//            throw new IllegalStateException("You should do this before setAdapter !");
        if (headers.add(view)) {
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    public int getHeadersCount() {
        return headers.size();
    }

    public int getFootersCount() {
        return footers.size();
    }

    public void addFooterView(View view) {
//        if (getAdapter() != null)
//            throw new IllegalStateException("You should do this before setAdapter !");

        if (footers.add(view)) {
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    public List<View> getFooters() {
        return footers;
    }

    public List<View> getHeaders() {
        return headers;
    }


    public void removeFooterView(View view) {
        if (footers.remove(view)) {
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    public void removeHeaderView(View view) {
        if (headers.remove(view)) {
            if (getAdapter() != null) {
                getAdapter().notifyDataSetChanged();
            }
        }

    }

    @SuppressWarnings("unchecked")
    protected void onBindItem(ViewHolder holder, int position, int count) {
        if (proxyAdapter != null) {
            proxyAdapter.onBindViewHolder(holder, position);
        }
    }

    private class HeaderFooterAdapter extends Adapter {

        private static final int HEADER_FOOTER_TYPE_OFFSET = -123000;

        @Override
        public int getItemViewType(int position) {
            int headerCount = headers.size();
            int footerCount = footers.size();

            if (position < headerCount) { // header
                return HEADER_FOOTER_TYPE_OFFSET - position;
            } else if (position < getItemCount() - footerCount) { // normal proxy adapter view type
                return proxyAdapter.getItemViewType(position - headerCount);
            } else { // footer
                return HEADER_FOOTER_TYPE_OFFSET - position;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType <= HEADER_FOOTER_TYPE_OFFSET) {
                final int position = -(viewType - HEADER_FOOTER_TYPE_OFFSET);
                if (position < getHeadersCount()) {
                    return new ViewHolder(headers.get(position)) {
                        @Override
                        public String toString() {
                            return "Header " + position + "," + itemView;
                        }
                    };
                } else {
                    final int footerIndex = position - getHeadersCount() - getRealItemCount();
                    return new ViewHolder(footers.get(footerIndex)) {
                        @Override
                        public String toString() {
                            return "Footer footerIndex: " + footerIndex + ", itemView:" + itemView;
                        }
                    };
                }

            } else {
                return proxyAdapter.onCreateViewHolder(parent, viewType);
            }
        }



        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(ViewHolder holder, final int position) {
            int viewType = getItemViewType(position);

            if (viewType <= HEADER_FOOTER_TYPE_OFFSET) {
//                StaggeredGridLayoutManager.LayoutParams layoutParams =
//                        (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//
//                if (layoutParams == null) {
//                    layoutParams = new StaggeredGridLayoutManager.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
//                }
//
//                layoutParams.setFullSpan(true);
//                holder.itemView.setLayoutParams(layoutParams);
            } else {
                int headerCount = getHeadersCount();
                int realPos = position - headerCount;

                onBindItem(holder, realPos, getRealItemCount());
            }
        }

        @Override
        public int getItemCount() {
            return headers.size() + getRealItemCount() + footers.size();
        }

        private int getRealItemCount() {
            return proxyAdapter == null ? 0 : proxyAdapter.getItemCount();
        }

    }

}
