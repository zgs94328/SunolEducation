package com.yangguangyulu.sunoleducation.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.util.JsonUtils;
import com.yangguangyulu.sunoleducation.util.TimeUtils;

import org.json.JSONObject;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/5/4.
 * Description:
 * Modified:
 */
@SuppressWarnings("unused")
public class EducationInfo implements Parcelable {
    private int id;
    private String videoUrl;
    private String videoName;
    private long uploadDate;
    private String videoImgUrl;
    private String videoType;  //法制教育等
    private String videoDescription;
    private String durationTime;  //视频时长
    private boolean topStatus;  //是否置顶  1、是；0、否
    private int studyType;  //1 必修; 2 选修
    private int status; //视频完成状态status：0未完成、 1已完成
    private String educationFormat;  //教育资源格式  avi, mp4, ppt, pdf, word等

    public EducationInfo(JSONObject jsonObject) {
        id = JsonUtils.getInt(jsonObject, "id");

        String temp = JsonUtils.getString(jsonObject, "videoPath");
        if (null != temp && temp.startsWith("/")) {
            temp = temp.substring(1, temp.length());
        }
        if (!temp.startsWith("http")) {
            videoUrl = Constants.BASE_SOURCE_URL + temp;
        } else {
            videoUrl = temp;
        }

        videoName = JsonUtils.getString(jsonObject, "resourceName");
        uploadDate = JsonUtils.getLong(jsonObject, "uploadDate");

        String tempImgUrl = JsonUtils.getString(jsonObject, "picturePath");
        if (null != tempImgUrl && tempImgUrl.startsWith("/")) {
            tempImgUrl = tempImgUrl.substring(1, tempImgUrl.length());
        }
        if (!tempImgUrl.startsWith("http")) {
            videoImgUrl = Constants.BASE_SOURCE_URL + tempImgUrl;
        } else {
            videoImgUrl = tempImgUrl;
        }

        videoType = JsonUtils.getString(jsonObject, "resourceType");
        videoDescription = JsonUtils.getString(jsonObject, "resourceDescribe");
        durationTime = JsonUtils.getString(jsonObject, "timeLength");
        topStatus = JsonUtils.getInt(jsonObject, "topStatus") == 1;
        studyType = JsonUtils.getInt(jsonObject, "studyType");
        status = JsonUtils.getInt(jsonObject, "status");
        educationFormat = JsonUtils.getString(jsonObject, "formats");

//        try {
//            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
////            mmr.setDataSource(videoUrl);  //获取本地视频的时长
//            mmr.setDataSource(videoUrl, new HashMap());  //获取网络视频的时长
//            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
//            DMLog.e(this.getClass().getSimpleName(), "duration = " + duration);
//        } catch (Exception e) {
//        }
    }

    protected EducationInfo(Parcel in) {
        id = in.readInt();
        videoUrl = in.readString();
        videoName = in.readString();
        uploadDate = in.readLong();
        videoImgUrl = in.readString();
        videoType = in.readString();
        videoDescription = in.readString();
        durationTime = in.readString();
        topStatus = in.readByte() != 0;
        studyType = in.readInt();
        status = in.readInt();
        educationFormat = in.readString();
    }

    public static final Creator<EducationInfo> CREATOR = new Creator<EducationInfo>() {
        @Override
        public EducationInfo createFromParcel(Parcel in) {
            return new EducationInfo(in);
        }

        @Override
        public EducationInfo[] newArray(int size) {
            return new EducationInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getUploadDate() {
        String uploadTime = TimeUtils.getTime(uploadDate, TimeUtils.DATE_FORMAT_DATE5);
        uploadTime = uploadTime.replace(" ", "   ");
        return uploadTime;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getVideoImgUrl() {
        return videoImgUrl;
    }

    public void setVideoImgUrl(String videoImgUrl) {
        this.videoImgUrl = videoImgUrl;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getDurationTime() {
        return durationTime.startsWith("0") ? durationTime.substring(1) : durationTime;
    }

    public boolean isTopStatus() {
        return topStatus;
    }

    public void setTopStatus(boolean topStatus) {
        this.topStatus = topStatus;
    }

    public int getStudyType() {
        return studyType;
    }

    public void setStudyType(int studyType) {
        this.studyType = studyType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDurationTime(String durationTime) {
        this.durationTime = durationTime;
    }

    public String getEducationFormat() {
        return educationFormat;
    }

    public void setEducationFormat(String educationFormat) {
        this.educationFormat = educationFormat;
    }

    /**
     * 是否视频
     *
     * @return true 视频； false 文档
     */
    public boolean isVideo() {
        return "mp4".equals(educationFormat) || "avi".equals(educationFormat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(videoUrl);
        dest.writeString(videoName);
        dest.writeLong(uploadDate);
        dest.writeString(videoImgUrl);
        dest.writeString(videoType);
        dest.writeString(videoDescription);
        dest.writeString(durationTime);
        dest.writeByte((byte) (topStatus ? 1 : 0));
        dest.writeInt(studyType);
        dest.writeInt(status);
        dest.writeString(educationFormat);
    }
}
