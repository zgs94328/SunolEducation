package com.yangguangyulu.sunoleducation.model;

import java.util.List;

/**
 * Created by yinqm on 2018/12/18.
 */
public class MyOnlineCourseRecord {

    List<MyOnlineCourseItemBean> studyDetails;
    EventWarning offlineEventWarning;


    public List<MyOnlineCourseItemBean> getStudyDetails() {
        return studyDetails;
    }

    public void setStudyDetails(List<MyOnlineCourseItemBean> studyDetails) {
        this.studyDetails = studyDetails;
    }

    public EventWarning getOfflineEventWarning() {
        return offlineEventWarning;
    }

    public void setOfflineEventWarning(EventWarning offlineEventWarning) {
        this.offlineEventWarning = offlineEventWarning;
    }


}
