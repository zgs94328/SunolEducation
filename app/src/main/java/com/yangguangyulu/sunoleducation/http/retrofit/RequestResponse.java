package com.yangguangyulu.sunoleducation.http.retrofit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.JsonParseException;
import com.shuyu.gsyvideoplayer.utils.NetworkUtils;
import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.operator.AppManager;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.JsonUtils;
import com.yangguangyulu.sunoleducation.util.SpUtils;
import com.yangguangyulu.sunoleducation.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.HttpException;

/**
 * Created by TANGJIAN on 2017/1/28.
 * Description:
 * Modified by TANGJIAN on 2017/1/28.
 */
@SuppressWarnings("all")
public abstract class RequestResponse<T> {
    private Activity activity;
    private String requestUrl;

    public RequestResponse() {
    }

    void onRequestResult(String requestUrl, T result) {
        try {
            JSONObject jsonObject = new JSONObject(result.toString());
            String code = JsonUtils.getString(jsonObject, "code");
            String message = JsonUtils.getString(jsonObject, "msg");
            JSONObject resultJson = JsonUtils.getJsonObject(jsonObject, "resBizMap");
            onSuccess(code, message, resultJson);
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(e);
        }
    }

    public void onCodeError(String code, String message) {
        activity = AppManager.getAppManager().currentActivity();
        //如果Token错误，或者超时，则跳转到登录界面  //未通过身份认证，请重新登录
        if ("CommErrCode.Unauthorized".equals(code)) {
            //退出
            if (null != activity && !activity.isFinishing()) {
                Intent intent = new Intent(Constants.BroadCastReceiver.TOKEN_ERROR);
                intent.putExtra("message", "登录已过期，请重新登录");
                AppManager.getAppManager().AppExit();
                return;
            }
        } else if (code.startsWith("CommErrCode.")) {
            if (null != activity && !activity.isFinishing()) {
                boolean isDialogShowing = (boolean) SpUtils.get(activity, "isDialogShowing", false);
                if (!isDialogShowing) {
                    SpUtils.put(activity, "isDialogShowing", true);
                    AlertDialog alertDialog = AlertDialogUtil.alert(activity, message);
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            SpUtils.put(activity, "isDialogShowing", false);
                        }
                    });
                }
                onFailure(null);
            }
        }
    }

    public void onFailure(Throwable t) {
        if (null != activity && !activity.isFinishing()) {
            ToastUtil.getInstance().show(activity, "actitity == null");
            if (null != t) {
                if (!NetworkUtils.isAvailable(activity)) {
                    ToastUtil.getInstance().show(activity, "无网络连接");
                } else if (t instanceof HttpException) {
                    ToastUtil.getInstance().show(activity, "HttpException");
                } else if (t instanceof SocketTimeoutException) {
                    ToastUtil.getInstance().show(activity, "网络请求超时，请稍后再试");
                } else if (t instanceof ConnectException) {
                    ToastUtil.getInstance().show(activity, "连接服务器失败");
                } else if (t instanceof IOException) {
                    ToastUtil.getInstance().show(activity, "IOEXCEPTION");
                }
            }
        }
    }

    public void onNetworkError() {
    }

    public abstract void onSuccess(String code, String message, JSONObject result);
}