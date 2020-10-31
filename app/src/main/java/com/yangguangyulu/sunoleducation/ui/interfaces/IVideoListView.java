package com.yangguangyulu.sunoleducation.ui.interfaces;

import com.yangguangyulu.sunoleducation.base.IBaseView;
import com.yangguangyulu.sunoleducation.model.EducationInfo;

import java.util.List;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/5/4.
 * Description:
 * Modified:
 */

public interface IVideoListView extends IBaseView {
    void onGetVideoList(List<EducationInfo> educationInfos, int totalCount);

    void onCommitAnswer(boolean success);
}
