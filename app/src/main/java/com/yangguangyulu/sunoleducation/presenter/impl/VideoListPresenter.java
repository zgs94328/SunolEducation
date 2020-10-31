package com.yangguangyulu.sunoleducation.presenter.impl;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.http.HttpParams;
import com.yangguangyulu.sunoleducation.http.retrofit.HttpManager;
import com.yangguangyulu.sunoleducation.http.retrofit.RequestResponse;
import com.yangguangyulu.sunoleducation.model.AnswerInfoBean;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.presenter.BasePresenter;
import com.yangguangyulu.sunoleducation.presenter.interfaces.IVideoListPresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IVideoListView;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.JsonUtils;
import com.yangguangyulu.sunoleducation.util.Jsons;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by TangJian on 2018/5/4.
 * Description:
 * Modified:
 */

@SuppressWarnings("unchecked")
public class VideoListPresenter extends BasePresenter<IVideoListView> implements IVideoListPresenter {

    @Override
    public void getEducationList(String type, int studyType, int pageIndex) {
        mView.startLoading();
        Observable<String> result = HttpManager.getInstance().createApiService()
                .getEducationList(type, studyType, pageIndex, 10);
        HttpManager.getInstance().toSubscribe(mView.getContext(),
                result, HttpManager.getSubscriber(new RequestResponse<String>() {
                    @Override
                    public void onSuccess(String code, String message, JSONObject result) {
                        mView.stopLoading();
                        if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                            JSONObject data = JsonUtils.getJsonObject(result, "data");
                            if (null != data) {
                                int total = JsonUtils.getInt(data, "total");
                                JSONArray recordList = JsonUtils.getJSONArray(data, "records");
                                if (null != recordList) {
                                    List<EducationInfo> educationInfos = new ArrayList<>(10);
                                    for (int i = 0; i < recordList.length(); i++) {
                                        educationInfos.add(new EducationInfo(JsonUtils.getJsonObject(recordList, i)));
                                    }
                                    mView.onGetVideoList(educationInfos, total);
                                    return;
                                }
                            }
                        } else {
                            onCodeError(code, message);
                            mView.showToast(message);
                        }
                        mView.onGetVideoList(null, 0);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        mView.stopLoading();
                        mView.onGetVideoList(null, 0);
                    }
                }));
    }


    /**
     * 提交学习时长
     */
    @Override
    public void commitStudyTimeByAnswer(final EducationInfo educationInfo, String answer) {
        int timeLength = 0;
        if (educationInfo.getStatus() != 1 && educationInfo.getStudyType() == 1) {
            mView.startLoading();
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
                            mView.stopLoading();
                            if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                                Log.d("yinqm", result.toString());
                                educationInfo.setStatus(1);
                                AnswerInfoBean answerInfoBean = Jsons.fromJson(result.toString(), AnswerInfoBean.class);

                                AlertDialogUtil.alert(mView.getContext(), answerInfoBean.getNote(), "确定",
                                        () -> {
                                            Intent intent = new Intent(Constants.BroadCastReceiver.EDUCATION_STUDY_COMPLETE);
                                            intent.putExtra("educationId", educationInfo.getId());
                                            LocalBroadcastManager.getInstance(mView.getContext()).sendBroadcast(intent);
                                            mView.onCommitAnswer(true);
                                        });
                            } else {
                                onCodeError(code, message);
                                mView.showToast(message);
                                mView.onCommitAnswer(false);
                            }
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            mView.stopLoading();
                            super.onFailure(e);
                            mView.onCommitAnswer(false);
                        }
                    }));
        }
    }


    /**
     * 获取推荐列表数据 详情界面用到
     */
    @Override
    public void getRecommendEducation(EducationInfo educationInfo) {
        mView.startLoading();
        Observable<String> result = HttpManager.getInstance().createApiService()
                .getEducationList(educationInfo.getVideoType(), educationInfo.getStudyType(), 1, 10);
        HttpManager.getInstance().toSubscribe(mView.getContext(),
                result, HttpManager.getSubscriber(new RequestResponse<String>() {
                    @Override
                    public void onSuccess(String code, String message, JSONObject result) {
                        mView.stopLoading();
                        if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                            JSONObject data = JsonUtils.getJsonObject(result, "data");
                            if (null != data) {
                                int total = JsonUtils.getInt(data, "total");
                                JSONArray recordList = JsonUtils.getJSONArray(data, "records");
                                if (null != recordList) {
                                    List<EducationInfo> educationInfos = new ArrayList<>(10);
                                    for (int i = 0; i < recordList.length(); i++) {
                                        educationInfos.add(new EducationInfo(JsonUtils.getJsonObject(recordList, i)));
                                    }
                                    mView.onGetVideoList(educationInfos, total);
                                    return;
                                }
                            }
                        } else {
                            onCodeError(code, message);
                            mView.showToast(message);
                        }
                        mView.onGetVideoList(null, 0);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        mView.stopLoading();
                        mView.onGetVideoList(null, 0);
                    }
                }));
    }
}
