package com.yangguangyulu.sunoleducation.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseFragment2;
import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.http.HttpParams;
import com.yangguangyulu.sunoleducation.http.retrofit.HttpManager;
import com.yangguangyulu.sunoleducation.http.retrofit.RequestResponse;
import com.yangguangyulu.sunoleducation.model.AnswerInfoBean;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.model.QuestionOpenBean;
import com.yangguangyulu.sunoleducation.model.VideoAnswerBean;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.DensityUtil;
import com.yangguangyulu.sunoleducation.util.Jsons;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class QuestionFragment extends BaseFragment2 {

    QuestionOpenBean questionOpenBean;

    @BindView(R.id.tv_question_name)
    TextView tvQuestionName;
    @BindView(R.id.rg_question_option)
    RadioGroup rgQuestionOption;
    @BindView(R.id.btn_previous)
    Button btnPrevious;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    QuestionActivity activity;

    int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_question, container, false);
        unbinder = ButterKnife.bind(this, mView);
        questionOpenBean = getArguments().getParcelable("data");
        activity = (QuestionActivity)getActivity();
        position = (getArguments().getInt("num", -1)) + 1;
        if (activity.questionList.size() == 1) {
            btnPrevious.setVisibility(View.GONE);
        } else {
            if (position == 1) {
                btnPrevious.setVisibility(View.GONE);
            }
            if (position == activity.questionList.size()) {
                btnPrevious.setVisibility(View.VISIBLE);
            }
        }

        dealWithData(questionOpenBean);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void dealWithData(QuestionOpenBean bean) {


        tvQuestionName.setText(bean.getQuestion());

        for (int i = 0; i < bean.getAnswers().size(); i++) {
            createOptions(bean.getAnswers().get(i));
        }
    }

    private void createOptions(String answer) {
        RadioButton button = new RadioButton(getContext());
        LinearLayout.LayoutParams layoutParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 24, 0, 0);
        button.setLayoutParams(layoutParams);
        Drawable drawable = getResources().getDrawable(
                R.drawable.timu_selector);
        drawable.setBounds(0, 0, DensityUtil.dip2px(getActivity(), 20), DensityUtil.dip2px(getActivity(), 20));
        button.setButtonDrawable(null);
        button.setCompoundDrawables(null, null, drawable, null);
        button.setTextColor(getResources().getColor(R.color.text_black_3));
        button.setTextSize(14);
        button.setText(answer);
        button.setOnClickListener(v -> {
            if (position == activity.questionList.size()) {
                btnCommit.setVisibility(View.VISIBLE);
            }
            VideoAnswerBean answerBean = new VideoAnswerBean();
            answerBean.setAnswer(answer);
            answerBean.setQuestionId(questionOpenBean.getId() + "");
            activity.addAnswer(position - 1, answerBean);
            activity.getViewPager().setCurrentItem(position);
        });
        rgQuestionOption.addView(button, layoutParams);
    }

    @OnClick({R.id.btn_previous, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_previous:
                activity.getViewPager().setCurrentItem(position - 2);
                break;
            case R.id.btn_commit:
                String answerJson = activity.getAnswerListString();
                commitStudyTimeByAnswer(activity.getEducationInfo(), answerJson);
                break;
        }
    }

    /**
     * 提交学习时长
     */
    public void commitStudyTimeByAnswer(EducationInfo educationInfo, String answer) {
        int timeLength = 0;
        if (educationInfo.getStatus() != 1 && educationInfo.getStudyType() == 1) {
            startLoading();
            HttpParams httpParams = new HttpParams();
            httpParams.putParam("educationId", educationInfo.getId());

            if (educationInfo.isVideo()) {
                String duration = educationInfo.getDurationTime();
                String[] durations = duration.split(":");
                if (durations.length == 3) {
                    timeLength = Integer.valueOf(durations[0]) * 60 + Integer.valueOf(durations[1])
                            + (Integer.valueOf(durations[1]) / 60f >= 0.5f ? 1 : 0);
                } else if (durations.length == 2) {
                    timeLength = Integer.valueOf(durations[0]) + (Integer.valueOf(durations[1]) / 60f >= 0.5f ? 1 : 0);
                } else {
                    timeLength = 1;  //若是视频时长不到1分钟，则记为1分钟
                }
            }
            httpParams.putParam("timeLong", timeLength);
            httpParams.putParam("answers", answer);

            Observable<String> result = HttpManager.getInstance().createApiService().commitStudyDurationByAnswer(httpParams.getRequestBody());
            HttpManager.getInstance().toSubscribe(mView.getContext(),
                    result, HttpManager.getSubscriber(new RequestResponse<String>() {
                        @Override
                        public void onSuccess(String code, String message, JSONObject result) {
                            stopLoading();
                            if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                                Log.d("yinqm", result.toString());
                                educationInfo.setStatus(1);
                                AnswerInfoBean answerInfoBean = Jsons.fromJson(result.toString(), AnswerInfoBean.class);

                                AlertDialogUtil.alert(mView.getContext(), answerInfoBean.getNote(), "确定",
                                        () -> {
                                            Intent intent = new Intent(Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE);
                                            intent.putExtra("educationId", educationInfo.getId());
                                            LocalBroadcastManager.getInstance(mView.getContext()).sendBroadcast(intent);
                                            finishActivity();
                                        });
                            } else {
                                onCodeError(code, message);
                                showToast(message);
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            stopLoading();
                            super.onFailure(e);
                        }
                    }));
        }
    }
}
