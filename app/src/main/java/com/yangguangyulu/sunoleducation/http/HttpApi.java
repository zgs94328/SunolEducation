package com.yangguangyulu.sunoleducation.http;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by TANGJIAN on 2017/2/3.
 * Description:
 * Modified by TANGJIAN on 2017/2/3.
 * http://blog.csdn.net/jdsjlzx/article/details/51511413
 * http://www.jianshu.com/p/acfefb0a204f
 */

@SuppressWarnings("unused")
public interface HttpApi {
    @GET("education/getEducationQuestions")
    Observable<String> getEducationQuestions(@Query("educationId") String educationId);

    @POST("educationOpenController/openLogin")
    Observable<String> openLogin(@Query("code") String code);

    @POST("correctPersonInfo/appLogin")
    Observable<String> login(@Body RequestBody body);

    @GET("correctPersonInfo/selectCorrectPersonInfo")
    Observable<String> getUserInfo();  //查询基本信息
    @GET("correctPersonInfo/getUserFaceUrl")
    Observable<String> getUserFaceUrl();  //查询人脸图片

    @GET("education/educationManageByPage")
    Observable<String> getEducationList(@Query("resourceType") String resourceType, @Query("studyType") int studyType,
                                        @Query("currentPage") int currentPage, @Query("pageSize") int pageSize); //获取在线教育资源

    @GET("advisoryManage/appVersion")
    Observable<String> checkUpdate(@Query("devType") String devType);  //devType: 1、android; 2、ios

    @GET("studyManage/selectStudydetailByPage")
    Observable<String> getUserCourse(@Query("selectDate") String selectDate,   //我的课程 (xxxx年xx月)
                                     @Query("currentPage") int currentPage, @Query("pageSize") int pageSize);

    @GET("studyManage/selectStudyManageByPage")
    Observable<String> getUserCourseDate(
            @Query("currentPage") int currentPage, @Query("pageSize") int pageSize);  //我的课程学习时间列表

    @GET("education/selectStudyInfoBytime")
    Observable<String> getStudyTimeByMonth(@Query("yearMonth") String yearMonth);  //根据月份查询学习时长(xxxx年xx月)

    @POST("education/addEducationTimeLong")
    Observable<String> commitStudyDuration(@Body RequestBody body);  //提交教育资源学习时长

    @POST("education/addEducationTimeLongByAnswer")
    Observable<String> commitStudyDurationByAnswer(@Body RequestBody body);

    @GET("education/newVideoRecommend")
    Observable<String> getRecommendEduList(
            @Query("currentPage") int currentPage, @Query("pageSize") int pageSize);  //获取推荐的视频资源

    @GET("education/newVideoRecommend")
    Observable<String> getRecommendEduList(@Query("studyType") int studyType, @Query("resourceType") String resourceType,
                                           @Query("currentPage") int currentPage, @Query("pageSize") int pageSize);  //获取推荐的视频资源

    @GET("studyManage/getByMonthList")
    Observable<String> myCourseList(@Query("currentPage") int currentPage,
                                    @Query("pageSize") int pageSize,
                                    @Query("yearMonth") String yearMonth,
                                    @Query("type") String type);

    @Multipart
    @POST("correctPersonInfo/addPersonFace")
    Observable<String> addPersonFace(@Part MultipartBody.Part file);

    @GET
    Call<ResponseBody> downloadFile(@Url String url);
}