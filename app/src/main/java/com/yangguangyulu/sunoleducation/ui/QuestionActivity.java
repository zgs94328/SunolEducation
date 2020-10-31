package com.yangguangyulu.sunoleducation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseMvpActivity;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.model.QuestionOpenBean;
import com.yangguangyulu.sunoleducation.model.QuestionsBean;
import com.yangguangyulu.sunoleducation.model.VideoAnswerBean;
import com.yangguangyulu.sunoleducation.presenter.impl.QuestionPresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IQuestionView;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.Jsons;
import com.yangguangyulu.sunoleducation.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionActivity extends BaseMvpActivity<QuestionPresenter> implements IQuestionView {

    private EducationInfo educationInfo = null;

    @BindView(R.id.btn_back)
    RelativeLayout btnBack;
    @BindView(R.id.txt_Title)
    TextView txtTitle;
    @BindView(R.id.vp_question)
    NoScrollViewPager vpQuestion;
    @BindView(R.id.tv_curr_num)
    TextView tvCurrNum;
    @BindView(R.id.tv_total_count)
    TextView tvTotalCount;

    FragmentManager fragmentManager = getSupportFragmentManager();

    int currPage = 0;

    public List<QuestionOpenBean> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        unbinder = ButterKnife.bind(this);
        if (getIntent().hasExtra("eduInfo")) {
            educationInfo = getIntent().getParcelableExtra("eduInfo");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("学习测验");
        mPresenter.getEducationQuestions(educationInfo.getId() + "");
    }

    @Override
    public QuestionPresenter initPresenter() {
        return new QuestionPresenter();
    }

    public EducationInfo getEducationInfo() {
        return educationInfo;
    }

    private void dealWithResult(QuestionsBean bean) {

        questionList = bean.getQuestionOpens();
        tvCurrNum.setText((currPage + 1) + "");
        tvTotalCount.setText("\b/\b" + questionList.size());
        initViewpager();
    }

    private List<VideoAnswerBean> answerList = new ArrayList<>(10);

    public String getAnswerListString() {
        return Jsons.toJson(answerList);
    }

    public void addAnswer(int index, VideoAnswerBean answerBean) {
        if (answerList.size() < index + 1) {
            answerList.add(index, answerBean);
        } else {
            answerList.set(index, answerBean);
        }
    }

    public NoScrollViewPager getViewPager() {
        return vpQuestion;
    }

    FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(fragmentManager) {

        @NonNull
        @Override
        public Fragment getItem(int position) {
            QuestionFragment fragment = new QuestionFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", questionList.get(position));
            bundle.putInt("num", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

    };

    @Override
    public void onGetFailed(String message) {
        AlertDialogUtil.alert(this, "题目获取失败,点击确定重新获取", "确定", () -> {
                    mPresenter.getEducationQuestions(educationInfo.getId() + "");
                }
        );
    }

    private void initViewpager() {
        vpQuestion.setOffscreenPageLimit(1000);
        vpQuestion.setPageMargin(30);
        vpQuestion.setAdapter(adapter);
        vpQuestion.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvCurrNum.setText((position + 1) + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onGetQuestions(QuestionsBean bean) {
        dealWithResult(bean);

    }

    @Override
    public void onBackPressed() {
        AlertDialogUtil.confirm(this, "退出后将失去答题进度，确认要退出吗？", "确定", "取消", new AlertDialogUtil.ConfirmListener() {
            @Override
            public void onOkClick() {
                QuestionActivity.super.onBackPressed();
            }

            @Override
            public void onCancelClick() {
            }
        });
    }

    @Override
    protected void setBtnBackClick() {
        View view = findViewById(R.id.btn_back);
        if (null != view) {
            view.setOnClickListener(v -> {
                AlertDialogUtil.confirm(this, "退出后将失去答题进度，确认要退出吗？", "确定", "取消", new AlertDialogUtil.ConfirmListener() {
                    @Override
                    public void onOkClick() {
                        QuestionActivity.super.onBackPressed();
                    }

                    @Override
                    public void onCancelClick() {
                    }
                });
            });
        }

    }
}
