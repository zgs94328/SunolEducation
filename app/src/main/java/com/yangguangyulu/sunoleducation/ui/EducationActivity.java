package com.yangguangyulu.sunoleducation.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseMvpActivity;
import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.operator.UserManager;
import com.yangguangyulu.sunoleducation.presenter.impl.EducationPresenter;
import com.yangguangyulu.sunoleducation.ui.adapter.SimpleFragmentPagerAdapter;
import com.yangguangyulu.sunoleducation.ui.face.FaceRegisterActivity;
import com.yangguangyulu.sunoleducation.ui.interfaces.IEducationView;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtils;
import com.yangguangyulu.sunoleducation.util.SpUtils;
import com.yangguangyulu.sunoleducation.widget.OnPageChangeListener;
import com.yangguangyulu.sunoleducation.widget.SimpleTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.yangguangyulu.sunoleducation.model.EducationType.CURRENT_AFFAIRS_POLICY;
import static com.yangguangyulu.sunoleducation.model.EducationType.EMPLOYMENT_GUIDANCE;
import static com.yangguangyulu.sunoleducation.model.EducationType.LEGAL_EDUCATION;
import static com.yangguangyulu.sunoleducation.model.EducationType.PSYCHOLOGICAL_COUNSELLING;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/4/23.
 * Description: 视频列表界面
 * Modified:
 */

public class EducationActivity extends BaseMvpActivity<EducationPresenter> implements IEducationView {
    @BindView(R.id.right_img)
    ImageView rightImg;
    @BindView(R.id.txt_Title)
    TextView txtTitle;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.already_finish_study)
    TextView alreadyFinishStudy;
    @BindView(R.id.study_info_layout)
    RelativeLayout studyInfoLayout;
    @BindView(R.id.no_course_task_view)
    TextView noCourseTaskView;
    //没有学习任务
    @BindView(R.id.btn_back)
    TextView btnBack;
    @BindView(R.id.btn_right)
    TextView btnRight;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_total_duration)
    TextView tvTotalDuration;
    @BindView(R.id.tv_less_duration)
    TextView tvLessDuration;
    @BindView(R.id.simple_tab_layout)
    SimpleTabLayout simpleTabLayout;
    @BindView(R.id.base_title_layout)
    RelativeLayout baseTitleLayout;


    private List<Fragment> mFragments = new ArrayList<>(2);


    private String[] titles = new String[]{
            LEGAL_EDUCATION,
            PSYCHOLOGICAL_COUNSELLING,
            EMPLOYMENT_GUIDANCE,
            CURRENT_AFFAIRS_POLICY
    };

    @Override
    public EducationPresenter initPresenter() {
        return new EducationPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        unbinder = ButterKnife.bind(this);
        initView();
        registerBroadCast();
    }

    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE:
                        mPresenter.getStudyStatusInfo();
                        break;
                }
            }
        }
    };

    public void initView() {
        txtTitle.setText("在线教育");
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setImageResource(R.drawable.oedu_hisplay_icon);
        baseTitleLayout.setBackgroundColor(getResources().getColor(R.color.blue_stroke));

        EducationListFragment listFragment1 = new EducationListFragment();
        EducationListFragment listFragment2 = new EducationListFragment();
        EducationListFragment listFragment3 = new EducationListFragment();
        EducationListFragment listFragment4 = new EducationListFragment();
        listFragment1.setResourceType(titles[0], true);
        listFragment2.setResourceType(titles[1], false);
        listFragment3.setResourceType(titles[2], false);
        listFragment4.setResourceType(titles[3], false);
        mFragments.add(listFragment1);
        mFragments.add(listFragment2);
        mFragments.add(listFragment3);
        mFragments.add(listFragment4);

        viewPager.setOffscreenPageLimit(3);
        SimpleFragmentPagerAdapter mAdapter = new SimpleFragmentPagerAdapter(this, mFragments);
        viewPager.setAdapter(mAdapter);
        //给ViewPager设置适配器

        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                simpleTabLayout.scroll(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                simpleTabLayout.onPageSelected(position);
                EducationListFragment fragment = ((EducationListFragment) mFragments.get(position));
                if (position == 0 && fragment.canShowAllType()) {
                    fragment.setResourceType(titles[0], true);
                    fragment.onRefresh();
                }
            }
        });

        simpleTabLayout.setTitles(titles);
        simpleTabLayout.setOnItemTabClickListener((position) -> {
            EducationListFragment fragment = ((EducationListFragment) mFragments.get(position));
            if (position == 0 && fragment.canShowAllType()) {
                simpleTabLayout.onPageSelected(position);
                fragment.setResourceType(titles[0], true);
                fragment.onRefresh();
            }
            viewPager.setCurrentItem(position);
        });
    }

    @OnClick(R.id.right_img)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.right_img:
                Intent intent = new Intent(EducationActivity.this, MyCourseActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示注册人脸对话框
     */
    private void showFaceRegDialog() {
        AlertDialogUtils.showDialog(this, "检测到没有注册人脸特征，请开始注册", "退出", "开始认证", new AlertDialogUtils.OnAlertClickListener() {
            @Override
            public void onCancel() {
                onBackPressed();
            }

            @Override
            public void onConfirm() {
                startActivity(FaceRegisterActivity.class);
            }
        });
    }

    public void activeEngine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                FaceEngine faceEngine = new FaceEngine();
                int activeCode = faceEngine.active(EducationActivity.this,
                        Constants.faceConstants.FACE_APP_ID,
                        Constants.faceConstants.FACE_SDK_KEY);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast("识别引擎激活成功");
                            SpUtils.put(getContext(), "hasRegFaceEngine", true);
                            if (UserManager.getArcFaceUrl(getContext()).isEmpty()) {
                                showFaceRegDialog();
                            }
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast("识别引擎已经激活");
                            if (UserManager.getArcFaceUrl(getContext()).isEmpty()) {
                                showFaceRegDialog();
                            }
                        } else {
                            showToast("识别引擎激活失败，请重新尝试");
                            SpUtils.put(getContext(), "hasRegFaceEngine", false);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onGetStudyStatusInfo(String haveLearnDuration, String remainDuration, int status, String totalDuration) {
        tvDuration.setText(haveLearnDuration);
        tvLessDuration.setText(remainDuration);
        tvTotalDuration.setText(totalDuration);
        if (status == 1 || status >= 2) {  //若是已完成 或 无任务，则隐藏状态
            studyInfoLayout.setVisibility(View.GONE);
            if (status == 1) {
                alreadyFinishStudy.setVisibility(View.VISIBLE);
            }
            if (status >= 2) {
                noCourseTaskView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(Boolean) SpUtils.get(getContext(), "hasRegFaceEngine", false)) {
            activeEngine();
        } else {
            if (UserManager.getArcFaceUrl(getContext()).isEmpty()) {
                showFaceRegDialog();
            }
        }
        mPresenter.getStudyStatusInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
