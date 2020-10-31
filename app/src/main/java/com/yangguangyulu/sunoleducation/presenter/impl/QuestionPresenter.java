package com.yangguangyulu.sunoleducation.presenter.impl;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.http.retrofit.HttpManager;
import com.yangguangyulu.sunoleducation.http.retrofit.RequestResponse;
import com.yangguangyulu.sunoleducation.model.QuestionsBean;
import com.yangguangyulu.sunoleducation.presenter.BasePresenter;
import com.yangguangyulu.sunoleducation.presenter.interfaces.IQuestionPresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IQuestionView;
import com.yangguangyulu.sunoleducation.util.Jsons;

import org.json.JSONObject;

import rx.Observable;

public class QuestionPresenter extends BasePresenter<IQuestionView> implements IQuestionPresenter {

    @Override
    public void getEducationQuestions(String educationId) {
        mView.startLoading();
        Observable<String> result = HttpManager.getInstance()
                .createApiService().getEducationQuestions(educationId);
        HttpManager.getInstance().toSubscribe(mView.getContext(), result, HttpManager.getSubscriber(new RequestResponse<String>() {
            @Override
            public void onSuccess(String code, String message, JSONObject result) {
                mView.stopLoading();
                if (code.equals(Constants.HTTP_STATUS.SUCCESS)) {
                    QuestionsBean bean = Jsons.fromJson(result.toString(), QuestionsBean.class);
                    mView.onGetQuestions(bean);
                } else {
                    mView.onGetFailed(message);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mView.stopLoading();
                super.onFailure(t);
                mView.onGetFailed(t.toString());
            }

        }));
    }
}
