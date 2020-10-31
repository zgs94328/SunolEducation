package com.yangguangyulu.sunoleducation.presenter.interfaces;

import com.yangguangyulu.sunoleducation.base.IBasePresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IQuestionView;

public interface IQuestionPresenter extends IBasePresenter<IQuestionView> {

    void getEducationQuestions(String educationId);
}
