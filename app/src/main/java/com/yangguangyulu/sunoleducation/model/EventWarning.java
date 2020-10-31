package com.yangguangyulu.sunoleducation.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yinqm on 2018/12/18.
 */
public class EventWarning implements Parcelable {
    //            "id": 794,
//            "correctPersonInfoId": 42,
//            "correctPersonInfoName": "里欧",
//            "correctPersonInfoIdcard": "431121198605098899",
//            "status": 0,
//            "missonLength": 480,
//            "alreadyTime": 28,
//            "yearMonths": "2018-12",
//            "insertTime": 1543593601000,
//            "updateId": 8,
//            "updateName": "lba",
//            "updateTime": 1543798201000
    private long id;
    private long correctPersonInfoId;
    private String correctPersonInfoName;
    private String correctPersonInfoIdcard;
    private int status;
    private int missonLength;
    private double missionLength;
    private double missionCompleteLength;
    private int alreadyTime;
    private String yearMonths;
    private String monthYear;
    private long insertTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMissonLength() {
        return missonLength;
    }

    public void setMissonLength(int missonLength) {
        this.missonLength = missonLength;
    }

    public int getAlreadyTime() {
        return alreadyTime;
    }

    public void setAlreadyTime(int alreadyTime) {
        this.alreadyTime = alreadyTime;
    }

    public String getYearMonths() {
        return yearMonths;
    }

    public void setYearMonths(String yearMonths) {
        this.yearMonths = yearMonths;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public double getMissionCompleteLength() {
        return missionCompleteLength;
    }

    public void setMissionCompleteLength(double missionCompleteLength) {
        this.missionCompleteLength = missionCompleteLength;
    }

    public double getMissionLength() {
        return missionLength;
    }

    public void setMissionLength(double missionLength) {
        this.missionLength = missionLength;
    }

    public EventWarning() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.correctPersonInfoId);
        dest.writeString(this.correctPersonInfoName);
        dest.writeString(this.correctPersonInfoIdcard);
        dest.writeInt(this.status);
        dest.writeInt(this.missonLength);
        dest.writeDouble(this.missionLength);
        dest.writeDouble(this.missionCompleteLength);
        dest.writeInt(this.alreadyTime);
        dest.writeString(this.yearMonths);
        dest.writeString(this.monthYear);
        dest.writeLong(this.insertTime);
    }

    protected EventWarning(Parcel in) {
        this.id = in.readLong();
        this.correctPersonInfoId = in.readLong();
        this.correctPersonInfoName = in.readString();
        this.correctPersonInfoIdcard = in.readString();
        this.status = in.readInt();
        this.missonLength = in.readInt();
        this.missionLength = in.readDouble();
        this.missionCompleteLength = in.readDouble();
        this.alreadyTime = in.readInt();
        this.yearMonths = in.readString();
        this.monthYear = in.readString();
        this.insertTime = in.readLong();
    }

    public static final Creator<EventWarning> CREATOR = new Creator<EventWarning>() {
        @Override
        public EventWarning createFromParcel(Parcel source) {
            return new EventWarning(source);
        }

        @Override
        public EventWarning[] newArray(int size) {
            return new EventWarning[size];
        }
    };
}
