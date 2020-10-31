package com.yangguangyulu.sunoleducation.ui.interfaces;

import com.yangguangyulu.sunoleducation.base.IBaseView;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2018/8/10.
 * Description:
 * Modified:
 */

public interface IEducationView extends IBaseView {
    /**
     * @param haveLearnDuration 已学习时长
     * @param remainDuration  剩余学习时长
     * @param status 是否已完成学习任务
     * @param totalDuration 总时长
     */
    void onGetStudyStatusInfo(String haveLearnDuration, String remainDuration, int status, String totalDuration);
}
