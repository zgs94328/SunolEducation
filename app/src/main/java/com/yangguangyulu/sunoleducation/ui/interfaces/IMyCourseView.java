package com.yangguangyulu.sunoleducation.ui.interfaces;

import com.yangguangyulu.sunoleducation.base.IBaseView;

import java.util.List;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2018/8/9.
 * Description:
 * Modified:
 */

public interface IMyCourseView extends IBaseView {

    void onGetMyCourseList(List<?> courseInfos);

    void onGetStudyStatusInfo(int status, String hours, String yearMonth);
}
