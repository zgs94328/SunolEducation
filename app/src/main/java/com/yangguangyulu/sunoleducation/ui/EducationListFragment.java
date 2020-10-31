package com.yangguangyulu.sunoleducation.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseFragment;
import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.presenter.impl.VideoListPresenter;
import com.yangguangyulu.sunoleducation.ui.adapter.EducationListAdapter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IVideoListView;
import com.yangguangyulu.sunoleducation.widget.DMSwipeRefreshLayout;
import com.yangguangyulu.sunoleducation.widget.recyclerview.DMRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yinqm on 2018/8/10.
 * Description:
 * Modified:
 */
public class EducationListFragment extends BaseFragment<VideoListPresenter> implements
        IVideoListView,
        DMRecyclerView.LoadMoreListener,
        EducationListAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_view)
    DMRecyclerView recyclerView;
    @BindView(R.id.empty_view_layout)
    View emptyViewLayout;
    @BindView(R.id.swipe_refresh_view)
    DMSwipeRefreshLayout swipeRefreshLayout;

    private int pageIndex = 1;
    private List<EducationInfo> dataList = new ArrayList<>(10);
    private EducationListAdapter adapter;
    private String type;  //资源类别
    private boolean shouldRequestAll = false; //是否请求所以类别的资源
    private boolean isFirstIn = true;

    public void setResourceType(String resourceType, boolean requestAllType) {
        type = resourceType;
        shouldRequestAll = requestAllType;
    }

    @Override
    public VideoListPresenter initPresenter() {
        return new VideoListPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_education_list, container, false);
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        registerBroadCast();
    }

    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE);
        LocalBroadcastManager.getInstance(mView.getContext()).registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE:
                        int educationId = intent.getIntExtra("educationId", 0);
                        for (EducationInfo educationInfo : dataList) {
                            if (educationId == educationInfo.getId()) {
                                educationInfo.setStatus(1);
                                adapter.updateList(recyclerView, dataList);
                                return;
                            }
                        }
                        break;
                }
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirstIn && getUserVisibleHint()) {
            isFirstIn = false;
            mPresenter.getEducationList(shouldRequestAll ? "" : type, 1, pageIndex);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAutoLoadMoreEnable(true);
        recyclerView.setLoadMoreListener(this);

        adapter = new EducationListAdapter(getContext(), dataList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyText("暂无更多资源");
    }

    @Override
    public void onLoadMore() {
        mPresenter.getEducationList(shouldRequestAll ? "" : type, 1, pageIndex);
    }

    @Override
    public void onItemClick(int position) {
        if (dataList.get(position).isVideo()) {

//            Intent intent = new Intent(getActivity(), ThirdWebActivity.class);
//            intent.putExtra("url", BASE_SOURCE_URL + "questionForApp/?educationId=" + dataList.get(position).getId());
//            intent.putExtra("title", "答题");
//            intent.putExtra("eduInfo", dataList.get(position));
//            startActivity(intent);

//            视频
            Intent intent = new Intent(getContext(), EducationDetailActivity.class);
            intent.putExtra("educationInfo", dataList.get(position));
            startActivity(intent);

//            Intent intent = new Intent(getActivity(), QuestionActivity.class);
//            intent.putExtra("educationInfo", dataList.get(position));
//            startActivity(intent);
        }

    }

    @Override
    public void onGetVideoList(List<EducationInfo> educationInfos, int totalCount) {
        if (null != educationInfos) {
            if (pageIndex == 1) {
                //第一次加载
                dataList.clear();
            }
            pageIndex += 1;
            dataList.addAll(educationInfos);
            if (dataList.size() == 0) {
                emptyViewLayout.setVisibility(View.VISIBLE);
            } else {
                emptyViewLayout.setVisibility(View.GONE);
                adapter.updateList(recyclerView, dataList);
                recyclerView.notifyMoreFinish(dataList.size() < totalCount);

                if (pageIndex == 2) {
                    //如果是请求的第一页数据，则回到顶部显示第一条数据
                    recyclerView.scrollToPosition(0);
                }
            }
        } else {
            if (pageIndex == 1) {
                dataList.clear();
                adapter.updateList(recyclerView, dataList);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        pageIndex = 1;
        mPresenter.getEducationList(shouldRequestAll ? "" : type, 1, pageIndex);
    }

    @Override
    public void onCommitAnswer(boolean success) {

    }

    /**
     * 在RequiredCourseFragment中用到，用来判断是否加载的是所有数据
     * 如果是，则改为加载对应type类型下的数据
     */
    public boolean canShowAllType() {
        return shouldRequestAll;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != receiver) {
            LocalBroadcastManager.getInstance(mView.getContext()).unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
