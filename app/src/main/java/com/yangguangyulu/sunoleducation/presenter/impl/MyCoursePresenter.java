package com.yangguangyulu.sunoleducation.presenter.impl;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.http.retrofit.HttpManager;
import com.yangguangyulu.sunoleducation.http.retrofit.RequestResponse;
import com.yangguangyulu.sunoleducation.model.MyOnlineCourseRecord;
import com.yangguangyulu.sunoleducation.presenter.BasePresenter;
import com.yangguangyulu.sunoleducation.presenter.interfaces.IMyCoursePresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IMyCourseView;
import com.yangguangyulu.sunoleducation.util.JsonUtils;
import com.yangguangyulu.sunoleducation.util.Jsons;

import org.json.JSONObject;

import rx.Observable;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2018/8/9.
 * Description:
 * Modified:
 */
@SuppressWarnings("unchecked")
public class MyCoursePresenter extends BasePresenter<IMyCourseView> implements IMyCoursePresenter {

    @Override
    public void getMyCourseList(int pageIndex, String yearMonth) {
        mView.startLoading();
        Observable<String> result = HttpManager.getInstance()
                .createApiService().myCourseList(pageIndex, 10, yearMonth, "1");
        HttpManager.getInstance().toSubscribe(mView.getContext(), result, HttpManager.getSubscriber(new RequestResponse<String>() {
            @Override
            public void onSuccess(String code, String message, JSONObject result) {
                mView.stopLoading();
                if (code.equals(Constants.HTTP_STATUS.SUCCESS)) {
//                    Log.d("yinqm", result.toString());
                    MyOnlineCourseRecord myOnlineCourseRecord = Jsons.fromJson(result.toString(), MyOnlineCourseRecord.class);
                    mView.onGetMyCourseList(myOnlineCourseRecord.getStudyDetails());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                mView.stopLoading();
            }
        }));
    }

    @Override
    public void getStudyStatusInfo(final String yearMonth) {
        mView.startLoading();
        Observable<String> result = HttpManager.getInstance()
                .createApiService().getStudyTimeByMonth(yearMonth);
        HttpManager.getInstance().toSubscribe(mView.getContext(),
                result, HttpManager.getSubscriber(new RequestResponse<String>() {
                    @Override
                    public void onSuccess(String code, String message, JSONObject result) {
                        mView.stopLoading();
                        if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                            if (JsonUtils.getJsonObject(result, "data") != null) {
                                JSONObject data = JsonUtils.getJsonObject(result, "data");
                                if (null != data) {
                                    int alreadyTime = JsonUtils.getInt(data, "alreadyTime");
                                    int status = JsonUtils.getInt(data, "status");  //0未完成，1已完成，2无任务
                                    String hasStudyTime = "已完成:";
                                    if (alreadyTime == 0) {
                                        hasStudyTime = "还未开始学习";
                                    } else {
                                        int remainHour = alreadyTime / 60;
                                        int remainMinute = alreadyTime % 60;
                                        hasStudyTime += remainHour > 0 ? remainHour + "小时" + remainMinute + "分" : remainMinute + "分";
                                    }
                                    mView.onGetStudyStatusInfo(status, hasStudyTime, yearMonth);
                                }
                            } else {
                                mView.onGetStudyStatusInfo(9, "还未开始学习", yearMonth);
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
