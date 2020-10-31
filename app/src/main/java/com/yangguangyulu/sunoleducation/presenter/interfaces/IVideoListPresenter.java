package com.yangguangyulu.sunoleducation.presenter.interfaces;

import com.yangguangyulu.sunoleducation.base.IBasePresenter;
import com.yangguangyulu.sunoleducation.model.EducationInfo;
import com.yangguangyulu.sunoleducation.ui.interfaces.IVideoListView;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/5/4.
 * Description: 教育资源列表
 * Modified:
 */
public interface IVideoListPresenter extends IBasePresenter<IVideoListView> {
    void getEducationList(String type, int studyType, int pageIndex);

    void getRecommendEducation(EducationInfo educationInfo);

    void commitStudyTimeByAnswer(EducationInfo educationInfo , String answer);
}
