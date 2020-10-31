package com.yangguangyulu.sunoleducation.presenter.impl;

import android.util.Log;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.http.retrofit.HttpManager;
import com.yangguangyulu.sunoleducation.http.retrofit.RequestResponse;
import com.yangguangyulu.sunoleducation.presenter.BasePresenter;
import com.yangguangyulu.sunoleducation.presenter.interfaces.IEducationPresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IEducationView;
import com.yangguangyulu.sunoleducation.util.JsonUtils;
import com.yangguangyulu.sunoleducation.util.TimeUtils;

import org.json.JSONObject;

import rx.Observable;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2018/8/10.
 * Description:
 * Modified:
 */
@SuppressWarnings("unchecked")
public class EducationPresenter extends BasePresenter<IEducationView> implements IEducationPresenter {
    @Override
    public void getStudyStatusInfo() {
        mView.startLoading();
        String yearMonth = TimeUtils.getCurrentTime(TimeUtils.DATE_FORMAT_DATE6);
        Observable<String> result = HttpManager.getInstance()
                .createApiService().getStudyTimeByMonth(yearMonth);
        HttpManager.getInstance().toSubscribe(mView.getContext(),
                result, HttpManager.getSubscriber(new RequestResponse<String>() {
                    @Override
                    public void onSuccess(String code, String message, JSONObject result) {
                        mView.stopLoading();
                        if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                            JSONObject data = JsonUtils.getJsonObject(result, "data");
                            if (null != data) {
                                int alreadyTime = JsonUtils.getInt(data, "alreadyTime");
                                int status = JsonUtils.getInt(data, "status");  //0未完成，1已完成，2无任务

                                int hour = alreadyTime / 60;
                                int minute = alreadyTime % 60;
                                String alreadyLearnDuration = hour > 0 ? hour + "小时" + minute + "分" : minute + "分";

                                String remainDuration = "";
                                int totalDuration = JsonUtils.getInt(data, "missonLength");
                                int totalHour = totalDuration / 60;
                                int totalMinute = totalDuration % 60;
                                String totalNeedDuration = totalHour > 0 ? totalHour + "小时" + totalMinute + "分" : totalMinute + "分";


                                if (alreadyTime < totalDuration) {
                                    // 学习时长小于8小时
                                    int remainTime = totalDuration - alreadyTime;
                                    int remainHour = remainTime / 60;
                                    int remainMinute = remainTime % 60;
                                    remainDuration += remainHour > 0 ? remainHour + "小时" + remainMinute + "分" : remainMinute + "分";
                                }

                                mView.onGetStudyStatusInfo(alreadyLearnDuration, remainDuration, status, totalNeedDuration);
                            } else {
                                mView.onGetStudyStatusInfo("0", "0", 9, "0");
                            }
                        } else {
                            onCodeError(code, message);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        mView.stopLoading();
                    }
                }));
    }
}
