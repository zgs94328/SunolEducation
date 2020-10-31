package com.yangguangyulu.sunoleducation.widget.recyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.util.DensityUtil;
import com.yangguangyulu.sunoleducation.util.Strings;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 描 述: 滑到底部，自动加载更多的RecyclerView
 * 注意：该控件的父控件，或者本身的visibility属性不能是GONE，否则不会走onCreateViewHolder()方法，导致mFooterViewHolder始终为null
 * 修 改 人: tangjian
 * 修改时间: 2017-3-29
 */
@SuppressWarnings("all")
public class DMRecyclerView extends RecyclerView {

    /**
     * RecyclerView的垂直滑动监听
     */
    public interface DMRecyclerViewScrollListener {
        /**
         * 垂直滑动监听事件
         *
         * @param x 水平滑动距离
         * @param y 垂直滑动距离
         */
        void onScrolled(RecyclerView recyclerView, int x, int y);
    }

    //item 类型
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_HEADER = 1;// 头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = 2;// 底部--往往是loading_more
    public final static int TYPE_LIST = 3;// 代表item展示的模式是list模式
    public final static int TYPE_STAGGER = 4;// 代码item展示模式是网格模式

    private boolean mIsLoadingMore;  //标记是否正在加载更多，防止再次调用加载更多接口
    private LoadMoreListener mListener;  //加载更多的监听-业务需要实现加载数据

    private AutoLoadAdapter mAutoLoadAdapter;  //自定义实现了头部和底部加载更多的adapter
    private AutoLoadAdapter.FooterViewHolder mFooterViewHolder;
    private boolean mIsFooterEnable = false;// 是否允许加载更多

    private View mHeaderView;  // 顶部header  局限性：只能添加一个headerView
    private boolean hasMoreData;  //是否有更多的数据

    private SparseArray<Long> lastClickTimes;  //保存上一次点击时间
    private DMRecyclerViewScrollListener dmRecyclerViewScrollListener;

    private boolean isEmptyData = false;  //列表是否没有数据
    private String emptyText = "暂无更多数据";  //数据为空时的展示文字
    // 是否显示数据为空的图片   数据为空的界面可以考虑做成跟footerView或headerView一样
    private boolean isShowFooterEmptyImg = false;
    private int footerEmptyImgResource = R.drawable.anonymous;  //显示数据为空的图片资源ID
    private String footerEmptyText = "";

    private String logTag = getClass().getSimpleName();  //方便看日志用

    /**
     * 方便看日志用
     */
    public void setLogTag(String logTag) {
        this.logTag = logTag;
    }

    public void setShowFooterEmptyImg(boolean showFooterEmptyImg, int emptyImgResourceId, String footerEmptyText) {
//        if (emptyImgResourceId <= 0 || StringUtils.isEmptyOrNull(footerEmptyText)) {
//            throw new IllegalArgumentException("emptyImgResourceId or footerEmptyText is wrong");
//        }
        this.isShowFooterEmptyImg = showFooterEmptyImg;
        this.footerEmptyImgResource = emptyImgResourceId;
        this.footerEmptyText = footerEmptyText;
    }

    public void setShowFooterEmptyImg(boolean showFooterEmptyImg, String footerEmptyText) {
        this.isShowFooterEmptyImg = showFooterEmptyImg;
        this.footerEmptyText = footerEmptyText;
    }

    /***
     * 在OverdueListActivity中用到该方法，临时写的。
     */
    public void setNoMoreFootText(String text, int textSize, int textColor) {
        if (null != mFooterViewHolder) {
            notifyMoreFinish(false);
            mFooterViewHolder.getFooterTextView().setText(text);
            mFooterViewHolder.getFooterTextView().setTextSize(textSize);
            mFooterViewHolder.getFooterTextView().setTextColor(textColor);
        }
    }

    public DMRecyclerView(Context context) {
        super(context);
        init();
    }

    public DMRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DMRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化-添加滚动监听
     * <p/>
     * 回调加载更多方法，前提是
     * <pre>
     *    1、有监听并且支持加载更多：null != mListener && mIsFooterEnable
     *    2、目前没有在加载，正在上拉（dy>0），当前最后一条可见的view是否是当前数据列表的最好一条--及加载更多
     * </pre>
     */
    private void init() {
        lastClickTimes = new SparseArray<>();

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dmRecyclerViewScrollListener != null) {
                    dmRecyclerViewScrollListener.onScrolled(recyclerView, 0, getScrollYDistance());
                }
                if (null != mListener && mIsFooterEnable && !mIsLoadingMore && dy > 0) {
                    int lastVisiblePosition = getLastVisiblePosition();
                    if (lastVisiblePosition + 1 == mAutoLoadAdapter.getItemCount()) {
                        setLoadingMore(true);

                        // 有更多数据才去加载更多 -- 只能在此判断，否则不会出现加载view
                        if (hasMoreData && null != mListener) {
                            mListener.onLoadMore();
                        }
                    }
                }
            }
        });
    }

    /**
     * 当父控件是NestedScrollView时，onScrolled()方法就判断不了是否滑到底部了
     */
    public void setNestedScrollView(NestedScrollView nestedScrollView) {
        if (null != nestedScrollView) {
            //解决NestedScrollView + RecyclerView滑动卡顿的问题
            setNestedScrollingEnabled(false);
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        if (hasMoreData && null != mListener) {
                            mListener.onLoadMore();
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取垂直方向滑动距离，注意：只适合LayoutManager用的是LinearLayoutManager
     */
    public int getScrollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisibleChildView.getHeight();
        return (position) * itemHeight - firstVisibleChildView.getTop();
    }

    public void setDMRecyclerViewScrollListener(DMRecyclerViewScrollListener dmRecyclerViewScrollListener) {
        this.dmRecyclerViewScrollListener = dmRecyclerViewScrollListener;
    }

    /**
     * 设置加载更多的监听
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        mListener = listener;
    }

    /**
     * 设置 加载更多 的状态
     */
    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
        if (loadingMore) {
            mFooterViewHolder.getLoadMoreAnimation().start();
        } else {
            mFooterViewHolder.getLoadMoreAnimation().stop();
        }
    }

    /**
     * 加载更多监听
     */
    public interface LoadMoreListener {
        void onLoadMore();
    }

    public class AutoLoadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private RecyclerView.Adapter mInternalAdapter;  //数据adapter

        AutoLoadAdapter(RecyclerView.Adapter adapter) {
            mInternalAdapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
//            DMLog.e(logTag, "getItemViewType position = " + position);
            int headerPosition = 0;
            int footerPosition = getItemCount() - 1;

            if (headerPosition == position && !isEmptyData && null != mHeaderView) {
                return TYPE_HEADER;
            }
            if (footerPosition == position && mIsFooterEnable) {
                return TYPE_FOOTER;
            }
            // 这么做保证layoutManager切换之后能及时的刷新上对的布局
            if (getLayoutManager() instanceof LinearLayoutManager) {
                return TYPE_LIST;
            } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                return TYPE_STAGGER;
            } else {
                return TYPE_NORMAL;
            }
        }

        /**
         * https://blog.csdn.net/qibin0506/article/details/49716795
         * 若当前位置是Footer的位置，那么该item占据getSpanCount()个单元格，正常情况下占据1个单元格
         */
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getItemViewType(position) == DMRecyclerView.TYPE_FOOTER
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            DMLog.e(logTag, "onCreateViewHolder viewType = " + viewType);
            if (viewType == TYPE_HEADER) {
                if (mHeaderView != null) {
                    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) mHeaderView.getLayoutParams();
                            layoutParams.width = DensityUtil.getScreenWidth(getContext());
                            mHeaderView.setLayoutParams(layoutParams);
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                    return new HeaderViewHolder(mHeaderView);
                }
            }
            if (viewType == TYPE_FOOTER) {
                if (mFooterViewHolder == null) {
                    mFooterViewHolder = new FooterViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(R.layout.pulldown_footer, parent, false));
                }
                return mFooterViewHolder;
            } else {
                // type normal
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {
            private View view;
            private TextView mFooterTextView;
            private View mFooterProgressLL;
            private ImageView mFooterEmptyImage;
            private TextView mFooterEmptyText;
            private View empty_ll;
            private AnimationDrawable loadMoreAnimation;

            FooterViewHolder(View itemView) {
                super(itemView);
                this.view = itemView;
                mFooterTextView = view.findViewById(R.id.pulldown_footer_text);
                mFooterEmptyImage = view.findViewById(R.id.empty_img);
                mFooterEmptyText = view.findViewById(R.id.empty_text);
                empty_ll = view.findViewById(R.id.empty_ll);
                mFooterProgressLL = view.findViewById(R.id.footer_loading_progress_ll);

                loadMoreAnimation = (AnimationDrawable) ContextCompat
                        .getDrawable(getContext(), R.drawable.loading_progress_round);
                view.findViewById(R.id.imageView).setBackground(loadMoreAnimation);
                loadMoreAnimation.start();
            }

            View getFooterView() {
                return view;
            }

            TextView getFooterTextView() {
                return mFooterTextView;
            }

            ImageView getFooterEmptyImage() {
                return mFooterEmptyImage;
            }

            TextView getFooterEmptyText() {
                return mFooterEmptyText;
            }

            View getEmptyLayout() {
                return empty_ll;
            }

            View getFooterLoadingProgressLl() {
                return mFooterProgressLL;
            }

            AnimationDrawable getLoadMoreAnimation() {
                return loadMoreAnimation;
            }
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
//            DMLog.e(logTag, "onBindViewHolder position = " + position);
            int type = getItemViewType(position);
            if (type != TYPE_FOOTER && type != TYPE_HEADER) {
                int tempPosition = null != mHeaderView ? position - 1 : position;
                if (tempPosition < 0) {
                    return;
                }
                mInternalAdapter.onBindViewHolder(holder, tempPosition);
            }
        }

        /**
         * 需要计算上加载更多和添加的头部俩个
         */
        @Override
        public int getItemCount() {
//            DMLog.e(logTag, "getItemCount");
            int count = mInternalAdapter.getItemCount();
            if (mIsFooterEnable) count++;
            if (!isEmptyData && null != mHeaderView) count++;

            return count;
        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (null == adapter) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": the param adapter should not be null");
        }
        mAutoLoadAdapter = new AutoLoadAdapter(adapter);
        super.swapAdapter(mAutoLoadAdapter, true);
    }

    /**
     * 切换layoutManager（还有问题）
     * <p/>
     * 为了保证切换之后页面上还是停留在当前展示的位置，记录下切换之前的第一条展示位置，切换完成之后滚动到该位置
     * 另外切换之后必须要重新刷新下当前已经缓存的itemView，否则会出现布局错乱（俩种模式下的item布局不同），
     * RecyclerView提供了swapAdapter来进行切换adapter并清理老的itemView cache
     */
    public void switchLayoutManager(LayoutManager layoutManager) {
        int firstVisiblePosition = getFirstVisiblePosition();
//        getLayoutManager().removeAllViews();
        setLayoutManager(layoutManager);
        //super.swapAdapter(mAutoLoadAdapter, true);
        getLayoutManager().scrollToPosition(firstVisiblePosition);
    }

    /**
     * 获取第一条展示的位置
     */
    private int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     */
    private int getMinPositions(int[] positions) {
        int minPosition = Integer.MAX_VALUE;
        for (int position : positions) {
            minPosition = Math.min(minPosition, position);
        }
        return minPosition;
    }

    /**
     * 获取最后一条展示的位置
     */
    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     */
    private int getMaxPosition(int[] positions) {
        int maxPosition = Integer.MIN_VALUE;
        for (int position : positions) {
            maxPosition = Math.min(maxPosition, position);
        }
        return maxPosition;
    }

    /**
     * 添加头部view
     */
    public void addHeaderView(View view) {
        mHeaderView = view;
    }

    /**
     * 设置是否展示自动加载更多
     */
    public void setAutoLoadMoreEnable(boolean enable) {
        mIsFooterEnable = enable;
    }

    /**
     * 此次加载更多数据的操作完毕，通知刷新view  必须要调用该方法，不然列表展示不出来
     *
     * @param hasMoreData 是否有更多的数据待加载
     */
    public void notifyMoreFinish(boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
        isEmptyData = false;
        setLoadingMore(false);
        refreshFooterView(hasMoreData);
        getAdapter().notifyDataSetChanged();
    }

    /**
     * 网络连接失败
     */
    public void netNotReady() {
        setLoadingMore(false);
        mFooterViewHolder.getFooterView().setVisibility(View.VISIBLE);
        mFooterViewHolder.getFooterLoadingProgressLl().setVisibility(View.GONE);
        mFooterViewHolder.getFooterTextView().setVisibility(View.VISIBLE);
        mFooterViewHolder.getFooterTextView().setText(getContext().getString(R.string.more_data));
    }

    /**
     * 加载数据出错,点击再次加载
     * 在界面初始化时 调用一次setAdapter()后mFooterViewHolder才不会为空，不然当数据第一次加载完后，mFooterViewHolder会是空
     */
    public void loadMoreError() {
        setLoadingMore(false);
        if (mFooterViewHolder != null) {
            mFooterViewHolder.getFooterView().setVisibility(View.VISIBLE);
            mFooterViewHolder.getFooterLoadingProgressLl().setVisibility(View.GONE);
            mFooterViewHolder.getFooterTextView().setVisibility(View.VISIBLE);
            mFooterViewHolder.getFooterTextView().setText("加载数据出错，点击重新加载~");
            mFooterViewHolder.getFooterView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkClick(view.getId())) {
                        if (mListener != null) {
                            refreshFooterView(hasMoreData);
                            mListener.onLoadMore();
                        }
                    }
                }
            });
        }
    }

    /**
     * 刷新底部加载view
     * 在界面初始化时 调用一次setAdapter()后mFooterViewHolder才不会为空，不然当数据第一次加载完后，mFooterViewHolder会是空
     */
    private void refreshFooterView(boolean hasMoreData) {
        if (null != mFooterViewHolder) {
            mFooterViewHolder.getEmptyLayout().setVisibility(GONE);
            mFooterViewHolder.getFooterView().setVisibility(View.VISIBLE);
            if (hasMoreData) {
                mFooterViewHolder.getFooterLoadingProgressLl().setVisibility(View.VISIBLE);
                mFooterViewHolder.getFooterTextView().setVisibility(View.GONE);
                mFooterViewHolder.getFooterTextView().setText("加载中");
            } else {
                mFooterViewHolder.getFooterLoadingProgressLl().setVisibility(View.GONE);
                mFooterViewHolder.getFooterTextView().setVisibility(View.VISIBLE);
                mFooterViewHolder.getFooterTextView().setText("暂无更多数据");
            }
        }
    }

    /**
     * 空数据
     */
    public void hasEmptyData() {
        if (null != mFooterViewHolder) {
            mFooterViewHolder.getFooterView().setVisibility(View.VISIBLE);
            mFooterViewHolder.getFooterLoadingProgressLl().setVisibility(View.GONE);

            if (!isShowFooterEmptyImg || footerEmptyImgResource <= 0) {
                mFooterViewHolder.getFooterTextView().setVisibility(View.VISIBLE);
                mFooterViewHolder.getEmptyLayout().setVisibility(GONE);

                mFooterViewHolder.getFooterTextView().setText(getEmptyText());
                getAdapter().notifyDataSetChanged();
            } else {
                isEmptyData = true;
                mFooterViewHolder.getEmptyLayout().setVisibility(VISIBLE);
                int emptyImageHeight = mFooterViewHolder.getFooterEmptyImage().getMeasuredHeight();
                if (emptyImageHeight == 0) {
                    getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            LinearLayout.LayoutParams emptyImageLayoutParams =
                                    (LinearLayout.LayoutParams) mFooterViewHolder.getFooterEmptyText().getLayoutParams();
                            int emptyImageHeight = mFooterViewHolder.getFooterEmptyImage().getMeasuredHeight()
                                    + emptyImageLayoutParams.topMargin + emptyImageLayoutParams.bottomMargin;

                            LinearLayout.LayoutParams emptyTextLayoutParams =
                                    (LinearLayout.LayoutParams) mFooterViewHolder.getFooterEmptyText().getLayoutParams();
                            int emptyTextHeight = mFooterViewHolder.getFooterEmptyText().getMeasuredHeight()
                                    + emptyTextLayoutParams.topMargin + emptyTextLayoutParams.bottomMargin;

                            View emptyLayout = mFooterViewHolder.getEmptyLayout();
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) emptyLayout.getLayoutParams();
                            //这里减去10，是因为在pulldown_footer.xml中根布局设置了paddingTop和paddingBottom
                            layoutParams.height = getMeasuredHeight() - DensityUtil.dip2px(getContext(), 10);
                            layoutParams.width = getMeasuredWidth();
                            int topPadding = (layoutParams.height - emptyImageHeight - emptyTextHeight) / 2;
                            emptyLayout.setPadding(0, topPadding, 0, 0);
                            emptyLayout.setLayoutParams(layoutParams);

                            mFooterViewHolder.getFooterTextView().setVisibility(View.GONE);
                            mFooterViewHolder.getFooterEmptyImage().setImageResource(footerEmptyImgResource);
                            if (!Strings.isEmptyOrNull(footerEmptyText)) {
                                mFooterViewHolder.getFooterEmptyText().setText(footerEmptyText);
                            }

                            mFooterViewHolder.getFooterTextView().setText(getEmptyText());
                            getAdapter().notifyDataSetChanged();
                        }
                    });
                } else {
                    mFooterViewHolder.getFooterTextView().setText(getEmptyText());
                    getAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 设置空数据时，底部view展示的文字提示
     */
    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    /**
     * 获取空数据时，底部view展示的文字提示
     */
    public String getEmptyText() {
        return this.emptyText;
    }

    /**
     * 检查是否可执行点击操作 防重复点击
     *
     * @return 返回true则可执行
     */
    private boolean checkClick(int id) {
        Long lastTime = lastClickTimes.get(id);
        Long thisTime = System.currentTimeMillis();
        lastClickTimes.put(id, thisTime);
        return !(lastTime != null && thisTime - lastTime < 800);
    }

}
