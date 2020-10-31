package com.yangguangyulu.sunoleducation.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yinqm on 2018/12/21.
 */
public class EducationInfoBean implements Parcelable {
//    "id": 44,
//            "uploadDate": 1543203082000,
//            "uploadId": 1,
//            "uploadAuthor": "root",
//            "resourceName": "《今日说法》20181108潜入牛棚",
//            "resourceType": "时事政策",
//            "resourceDescribe": "《今日说法》20181108潜入牛棚",
//            "videoPath": "/upload/videos/2018/11/181dca3f-eddd-48bd-b547-fde71764d264.mp4",
//            "picturePath": "/upload/videos/2018/11/181dca3f-eddd-48bd-b547-fde71764d264.png",
//            "topStatus": 0,
//            "videoName": "《今日说法》20181108潜入牛棚.mp4",
//            "timeLength": "27:57",
//            "studyType": 1,
//            "formats": "mp4",
//            "fineType": 1,
//            "resourceStatus": 1,
//            "updateId": null,
//            "updateName": null,
//            "updateTime": null

    private long id;
    private long uploadDate;
    private long uploadId;
    private String uploadAuthor;
    private String resourceName;
    private String resourceType;
    private String resourceDescribe;
    private String videoPath;
    private String picturePath;
    private int topStatus;
    private String videoName;
    private String timeLength;
    private int studyType;
    private String formats;
    private int fineType;
    private int resourceStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadAuthor() {
        return uploadAuthor;
    }

    public void setUploadAuthor(String uploadAuthor) {
        this.uploadAuthor = uploadAuthor;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceDescribe() {
        return resourceDescribe;
    }

    public void setResourceDescribe(String resourceDescribe) {
        this.resourceDescribe = resourceDescribe;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getTopStatus() {
        return topStatus;
    }

    public void setTopStatus(int topStatus) {
        this.topStatus = topStatus;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public int getStudyType() {
        return studyType;
    }

    public void setStudyType(int studyType) {
        this.studyType = studyType;
    }

    public String getFormats() {
        return formats;
    }

    public void setFormats(String formats) {
        this.formats = formats;
    }

    public int getFineType() {
        return fineType;
    }

    public void setFineType(int fineType) {
        this.fineType = fineType;
    }

    public int getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(int resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.uploadDate);
        dest.writeLong(this.uploadId);
        dest.writeString(this.uploadAuthor);
        dest.writeString(this.resourceName);
        dest.writeString(this.resourceType);
        dest.writeString(this.resourceDescribe);
        dest.writeString(this.videoPath);
        dest.writeString(this.picturePath);
        dest.writeInt(this.topStatus);
        dest.writeString(this.videoName);
        dest.writeString(this.timeLength);
        dest.writeInt(this.studyType);
        dest.writeString(this.formats);
        dest.writeInt(this.fineType);
        dest.writeInt(this.resourceStatus);
    }

    public EducationInfoBean() {
    }

    protected EducationInfoBean(Parcel in) {
        this.id = in.readLong();
        this.uploadDate = in.readLong();
        this.uploadId = in.readLong();
        this.uploadAuthor = in.readString();
        this.resourceName = in.readString();
        this.resourceType = in.readString();
        this.resourceDescribe = in.readString();
        this.videoPath = in.readString();
        this.picturePath = in.readString();
        this.topStatus = in.readInt();
        this.videoName = in.readString();
        this.timeLength = in.readString();
        this.studyType = in.readInt();
        this.formats = in.readString();
        this.fineType = in.readInt();
        this.resourceStatus = in.readInt();
    }

    public static final Creator<EducationInfoBean> CREATOR = new Creator<EducationInfoBean>() {
        @Override
        public EducationInfoBean createFromParcel(Parcel source) {
            return new EducationInfoBean(source);
        }

        @Override
        public EducationInfoBean[] newArray(int size) {
            return new EducationInfoBean[size];
        }
    };
}
