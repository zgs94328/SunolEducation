package com.yangguangyulu.sunoleducation.presenter.interfaces;

import com.yangguangyulu.sunoleducation.base.IBasePresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.IMyCourseView;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2018/8/9.
 * Description:
 * Modified:
 */

public interface IMyCoursePresenter extends IBasePresenter<IMyCourseView> {

    void getMyCourseList(int pageIndex, String yearMonth);

    void getStudyStatusInfo(String yearMonth);
}
