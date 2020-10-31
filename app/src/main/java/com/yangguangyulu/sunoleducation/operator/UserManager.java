package com.yangguangyulu.sunoleducation.operator;

import android.content.Context;

import com.yangguangyulu.sunoleducation.util.SpUtils;

public class UserManager {

//    private String correctStart;  //入矫日期
//    private String solutionsCorrectDate;  //解矫日期
//    private int correctStatus; //'矫正状态：0待入矫、1矫正、2解矫',
//    private int studyStatus; //课程学习状态  //0未完成；1已完成；2、3无任务

    public static final String SP_USER_INFO = "sp_user_info";

    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_USER_NAME = "key_user_name";
    public static final String KEY_CURRECT_STATUS = "key_currect_status";
    public static final String KEY_ARC_FACE_URL = "key_arc_face_url";

    public static final String KEY_IDCARD_NUMBER = "key_idcard_number";
    public static final String KEY_COMMUNITY = "key_community";
    public static final String KEY_MOBILE = "key_mobile";
    public static final String KEY_CORRECT_START = "key_correct_start";
    public static final String KEY_SOLUTION_DATE = "key_solution_date";

    public static final String KEY_STUDY_STATUS = "key_study_status";

    public static int getStudyStatus(Context context) {
        return (int) SpUtils.get(context, SP_USER_INFO, KEY_STUDY_STATUS, -1);
    }

    public static void putStudyStatus(Context context, int studyStatus) {
        SpUtils.put(context, SP_USER_INFO, KEY_STUDY_STATUS, studyStatus);
    }

    public static String getCorrectStart(Context context) {
        return (String) SpUtils.get(context, SP_USER_INFO, KEY_CORRECT_START, "");
    }

    public static void putCorrectStart(Context context, String correctStart) {
        SpUtils.put(context, SP_USER_INFO, KEY_CORRECT_START, correctStart);
    }

    public static String getSolutionDate(Context context) {
        return (String) SpUtils.get(context, SP_USER_INFO, KEY_SOLUTION_DATE, "");
    }

    public static void putSolutionDate(Context context, String solutionDate) {
        SpUtils.put(context, SP_USER_INFO, KEY_SOLUTION_DATE, solutionDate);
    }

    public static String getIdcardNumber(Context context) {
        return (String) SpUtils.get(context, SP_USER_INFO, KEY_IDCARD_NUMBER, "");
    }

    public static void putIdcardNumber(Context context, String idcardNumber) {
        SpUtils.put(context, SP_USER_INFO, KEY_IDCARD_NUMBER, idcardNumber);
    }

    public static String getCommunity(Context context) {
        return (String) SpUtils.get(context, SP_USER_INFO, KEY_COMMUNITY, "");
    }

    public static void putCommunity(Context context, String commnunity) {
        SpUtils.put(context, SP_USER_INFO, KEY_COMMUNITY, commnunity);
    }

    public static String getMobile(Context context) {
        return (String) SpUtils.get(context, SP_USER_INFO, KEY_MOBILE, "");
    }

    public static void putMobile(Context context, String mobile) {
        SpUtils.put(context, SP_USER_INFO, KEY_MOBILE, mobile);
    }

    public static int getUserId(Context context) {
        return (int) SpUtils.get(context, SP_USER_INFO, KEY_USER_ID, -1);
    }

    public static void putUserId(Context context, int userId) {
        SpUtils.put(context, SP_USER_INFO, KEY_USER_ID, userId);
    }

    public static String getUserName(Context context) {
        return (String) SpUtils.get(context, SP_USER_INFO, KEY_USER_NAME, "");
    }

    public static void putUserName(Context context, String userName) {
        SpUtils.put(context, SP_USER_INFO, KEY_USER_NAME, userName);
    }

    public static int getCurrectStatus(Context context) {
        return (int) SpUtils.get(context, SP_USER_INFO, KEY_CURRECT_STATUS, -1);
    }

    public static void putCurrectStatus(Context context, int currectStatus) {
        SpUtils.put(context, SP_USER_INFO, KEY_CURRECT_STATUS, currectStatus);
    }

    public static String getArcFaceUrl(Context context) {
        return (String) SpUtils.get(context, SP_USER_INFO, KEY_ARC_FACE_URL, "");
    }

    public static void putArcFaceUrl(Context context, String arcFaceUrl) {
        SpUtils.put(context, SP_USER_INFO, KEY_ARC_FACE_URL, arcFaceUrl);
    }

    public static void removeAll(Context context) {
        SpUtils.clear(context, SP_USER_INFO);
    }

}
