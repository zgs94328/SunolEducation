package com.yangguangyulu.sunoleducation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseMvpActivity;
import com.yangguangyulu.sunoleducation.presenter.impl.MyCoursePresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IMyCourseView;
import com.yangguangyulu.sunoleducation.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yinqm on 2018/12/18.
 */
public class MyCourseActivity extends BaseMvpActivity<MyCoursePresenter> implements IMyCourseView {

    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.select_time)
    TextView selectTime;
    @BindView(R.id.month)
    TextView month;
    @BindView(R.id.have_learn_time)
    TextView haveLearnTime;
    @BindView(R.id.learn_status)
    TextView learnStatus;
    @BindView(R.id.frl_my_course)
    FrameLayout frlMyCourse;

    MyOnlineCourseFragment myOnlineCourseFragment = new MyOnlineCourseFragment();

    String currentYearMonth = TimeUtils.getCurrentTime(TimeUtils.DATE_FORMAT_DATE6);

    @Override
    public MyCoursePresenter initPresenter() {
        return new MyCoursePresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);
        ButterKnife.bind(this);
        mPresenter.getStudyStatusInfo(currentYearMonth);
        initView();
    }

    private void initView() {
        month.setText("本月");
        getSupportFragmentManager().beginTransaction().replace(R.id.frl_my_course, myOnlineCourseFragment).commit();
    }


    @OnClick({R.id.back_img, R.id.select_time})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
            case R.id.select_time:
                TimePickerView pvTime = new TimePickerBuilder(this,
                        (date, view) -> {
                            //选中事件回调
                            currentYearMonth = new SimpleDateFormat("yyyy-MM", Locale.CHINA).format(date);
                            mPresenter.getStudyStatusInfo(currentYearMonth);
                            myOnlineCourseFragment.updateYearMonth(currentYearMonth);
                        })
                        .setType(new boolean[]{true, true, false, false, false, false})
                        .build();
                pvTime.setDate(Calendar.getInstance());
                // 注：根据需求来决定是否使用该方法（一般是精确到秒的情况），
                // 此项可以在弹出选择器的时候重新设置当前时间，
                // 避免在初始化之后由于时间已经设定，
                // 导致选中时间与当前时间不匹配的问题。
                pvTime.show();
                break;
        }
    }

    @Override
    public void onGetMyCourseList(List<?> courseInfos) {
    }

    @Override
    public void onGetStudyStatusInfo(int status, String hours, String yearMonth) {
        // 0未完成，
        // 1已完成，
        // 2无任务
        switch (status) {
            case 0:
                learnStatus.setText("未完成");
                haveLearnTime.setText(hours);
                break;
            case 1:
                learnStatus.setText("已完成");
                haveLearnTime.setText(hours);
                break;
            case 2:
                learnStatus.setText("无任务");
                haveLearnTime.setText(hours);
                break;
            case 9:
                learnStatus.setText("无任务");
                haveLearnTime.setText(hours);
                break;
            default:
                learnStatus.setText("无任务");
                haveLearnTime.setText(hours);
                break;
        }
        month.setText(yearMonth);
    }
}
