package com.yangguangyulu.sunoleducation.base;

import android.os.Environment;

import java.io.File;

public class Constants {

    public static final String HAS_DOWN_NEW_VERSION = "hasDownNewVersion";  //是否已下载新的APP版本
    public static final String IS_FORCE_TO_UPDATE = "isForceToUpdate";  //是否强制更新
    public static final String HAS_NEW_VERSION_TO_DOWN = "hasNewVersionToDown";  //是否有新版本的apk需要下载


    public interface Config {
        String DEFAULT_SAVE_FILE_PATH = Environment.getExternalStorageDirectory()
                + File.separator
                + "sunshineol"
                + File.separator
                + "download";

        //版本更新时APK的名字
        String APK_NAME = "sunoledu_app.apk";
    }


    public interface AppConfig {
        boolean isUseLog = true;  //测试时，该值为true；上线改为false
    }

    public interface faceConstants {
        String FACE_APP_ID = "4692QL7DP6anz746DB7echbiT5M7EF9v93CS1A17JPat";
        String FACE_SDK_KEY = "66qB29mZGqAu3K3FrULUTCoAuDPwJATZgxdU1tBWgfpZ";

        String SAVE_IMG_DIR = "register" + File.separator + "imgs";
        String SAVE_FEATURE_DIR = "register" + File.separator + "features";
        String SAVE_FEATURE_NAME = "registerface";
    }

    public interface CONSTANT_STRING {
        String PH_LOGIN_USER = "ph_login_user";
        String PH_UPDATE_INFO = "ph_update_info";
    }

    public interface BroadCastReceiver {
        String TOKEN_ERROR = "com.yangguangyulu.sunoleducation_token_error";
        String EDUCATION_STUDY_COMPLETE = "com.yangguangyulu.sunoleducation_education_study_complete";
        String DOWNLOAD_APK_COMPLETE = "com.yangguangyulu.sunoleducation_download_apk_complete";
    }

    public interface HTTP_STATUS {
        String SUCCESS = "success";
        String ERR = "err";
    }

//    public static String BASE_API_URL = "https://cp.xxsfjzpg.com/proxy/";  //生产环境
//    public static String BASE_API_URL = "http://192.168.1.70:9081/";     // 内网
    public static String BASE_API_URL = "http://cp.yangguangzx.com/proxy/";   //外网环境

//    public static String BASE_SOURCE_URL = "https://static.xxsfjzpg.com/";  //资源路径, 如视频图片等
//    public static String BASE_SOURCE_URL = "http://192.168.1.70:92/";      // 内网
    public static String BASE_SOURCE_URL = "http://static.yangguangzx.com/"; //外网环境

//    public static String BASE_API_URL = "http://192.168.1.127:8081/";     // 内网

}
