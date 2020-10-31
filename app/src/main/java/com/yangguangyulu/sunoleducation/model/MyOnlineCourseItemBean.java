package com.yangguangyulu.sunoleducation.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yinqm on 2018/12/18.
 */
public class MyOnlineCourseItemBean implements Parcelable {

//    {
//        "id": 105,
//            "studyManageId": 794,
//            "correctPersonInfoId": 42,
//            "correctPersonInfoName": "里欧",
//            "correctPersonInfoIdcard": "431121198605098899",
//            "timeLong": 28,
//            "insertTime": 1545119203000,
//            "yearMonths": "2018-12",
//            "educationId": 44,
//            "educationName": "《今日说法》20181108潜入牛棚"
//    }

    private long id;
    private long studyManageId;
    private long correctPersonInfoId;
    private String correctPersonInfoName;
    private String correctPersonInfoIdcard;
    private long timeLong;
    private long allTimeLong;
    private int rightNum;
    private int allNum;
    private long insertTime;
    private String yearMonths;
    private long educationId;
    private String educationName;
    private EducationInfoBean education;

    public EducationInfoBean getEducation() {
        return education;
    }

    public void setEducation(EducationInfoBean education) {
        this.education = education;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStudyManageId() {
        return studyManageId;
    }

    public void setStudyManageId(long studyManageId) {
        this.studyManageId = studyManageId;
    }

    public long getCorrectPersonInfoId() {
        return correctPersonInfoId;
    }

    public void setCorrectPersonInfoId(long correctPersonInfoId) {
        this.correctPersonInfoId = correctPersonInfoId;
    }

    public String getCorrectPersonInfoName() {
        return correctPersonInfoName;
    }

    public void setCorrectPersonInfoName(String correctPersonInfoName) {
        this.correctPersonInfoName = correctPersonInfoName;
    }

    public String getCorrectPersonInfoIdcard() {
        return correctPersonInfoIdcard;
    }

    public void setCorrectPersonInfoIdcard(String correctPersonInfoIdcard) {
        this.correctPersonInfoIdcard = correctPersonInfoIdcard;
    }

    public long getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(long timeLong) {
        this.timeLong = timeLong;
    }

    public long getAllTimeLong() {
        return allTimeLong;
    }

    public void setAllTimeLong(long allTimeLong) {
        this.allTimeLong = allTimeLong;
    }

    public int getRightNum() {
        return rightNum;
    }

    public void setRightNum(int rightNum) {
        this.rightNum = rightNum;
    }

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }


    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public String getYearMonths() {
        return yearMonths;
    }

    public void setYearMonths(String yearMonths) {
        this.yearMonths = yearMonths;
    }

    public long getEducationId() {
        return educationId;
    }

    public void setEducationId(long educationId) {
        this.educationId = educationId;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }

    public MyOnlineCourseItemBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.studyManageId);
        dest.writeLong(this.correctPersonInfoId);
        dest.writeString(this.correctPersonInfoName);
        dest.writeString(this.correctPersonInfoIdcard);
        dest.writeLong(this.timeLong);
        dest.writeLong(this.allTimeLong);
        dest.writeInt(this.rightNum);
        dest.writeInt(this.allNum);
        dest.writeLong(this.insertTime);
        dest.writeString(this.yearMonths);
        dest.writeLong(this.educationId);
        dest.writeString(this.educationName);
        dest.writeParcelable(this.education, flags);
    }

    protected MyOnlineCourseItemBean(Parcel in) {
        this.id = in.readLong();
        this.studyManageId = in.readLong();
        this.correctPersonInfoId = in.readLong();
        this.correctPersonInfoName = in.readString();
        this.correctPersonInfoIdcard = in.readString();
        this.timeLong = in.readLong();
        this.allTimeLong = in.readLong();
        this.rightNum = in.readInt();
        this.allNum = in.readInt();
        this.insertTime = in.readLong();
        this.yearMonths = in.readString();
        this.educationId = in.readLong();
        this.educationName = in.readString();
        this.education = in.readParcelable(EducationInfoBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<MyOnlineCourseItemBean> CREATOR = new Parcelable.Creator<MyOnlineCourseItemBean>() {
        @Override
        public MyOnlineCourseItemBean createFromParcel(Parcel source) {
            return new MyOnlineCourseItemBean(source);
        }

        @Override
        public MyOnlineCourseItemBean[] newArray(int size) {
            return new MyOnlineCourseItemBean[size];
        }
    };
}
