package com.yangguangyulu.sunoleducation.ui.interfaces;

import com.yangguangyulu.sunoleducation.base.IBaseView;
import com.yangguangyulu.sunoleducation.model.QuestionsBean;

import java.util.List;

public interface IQuestionView extends IBaseView {

    void onGetQuestions(QuestionsBean bean);

    void onGetFailed(String message);
}
