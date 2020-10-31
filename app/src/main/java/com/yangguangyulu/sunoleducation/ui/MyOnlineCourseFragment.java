package com.yangguangyulu.sunoleducation.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseFragment;
import com.yangguangyulu.sunoleducation.model.MyOnlineCourseItemBean;
import com.yangguangyulu.sunoleducation.presenter.impl.MyCoursePresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IMyCourseView;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.TimeUtils;
import com.yangguangyulu.sunoleducation.widget.recyclerview.DMRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by yinqm on 2018/12/18.
 */
public class MyOnlineCourseFragment extends BaseFragment<MyCoursePresenter> implements IMyCourseView {

    @BindView(R.id.recycler_view)
    DMRecyclerView recyclerView;
    @BindView(R.id.empty_view_layout)
    View emptyViewLayout;
    @BindView(R.id.empty_tv)
    TextView emptyTv;

    Unbinder unbinder;

    String currentYearMonth = TimeUtils.getCurrentTime(new SimpleDateFormat("yyyy-MM"));

    private int pageIndex = 1;
    private MyCourseListAdapter adapter;
    private List<MyOnlineCourseItemBean> courseInfoList = new ArrayList<>(5);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_course_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    protected void initView() {
        emptyTv.setText("您还没有学习记录");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAutoLoadMoreEnable(true);
        recyclerView.setShowFooterEmptyImg(true, "您还没有学习记录");
        recyclerView.setLoadMoreListener(() -> {
            pageIndex += 1;
            mPresenter.getMyCourseList(pageIndex, currentYearMonth);
        });
        adapter = new MyCourseListAdapter(getContext(), courseInfoList);
        adapter.setOnItemClickListener(position -> {
                    String content = "总时长："
                            + courseInfoList.get(position).getAllTimeLong()
                            + "分，获得时长："
                            + courseInfoList.get(position).getTimeLong()
                            + "分，<br/>总题目数："
                            + courseInfoList.get(position).getAllNum()
                            + "，答对题目数："
                            + courseInfoList.get(position).getRightNum();
                    AlertDialogUtil.alert(getContext(), content, "确定");
                }
        );
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getMyCourseList(pageIndex, currentYearMonth);
    }

    public void updateYearMonth(String yearMonth) {
        currentYearMonth = yearMonth;
        pageIndex = 1;
        mPresenter.getMyCourseList(pageIndex, currentYearMonth);
    }

    @Override
    public MyCoursePresenter initPresenter() {
        return new MyCoursePresenter();
    }

    @Override
    public void onGetStudyStatusInfo(int status, String hours, String yearMonth) {

    }

    @Override
    public void onGetMyCourseList(List<?> courseInfos) {
        if (pageIndex == 1) {
            this.courseInfoList.clear();
        }
        this.courseInfoList.addAll((List<MyOnlineCourseItemBean>) courseInfos);
        if (this.courseInfoList.size() == 0) {
            adapter.updateAll(this.courseInfoList);
            recyclerView.notifyMoreFinish(false);
            emptyViewLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            adapter.updateAll(this.courseInfoList);
            recyclerView.notifyMoreFinish(courseInfoList.size() == 10);
            emptyViewLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
