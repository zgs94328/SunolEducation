package com.yangguangyulu.sunoleducation.presenter.impl;

import android.util.Log;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.http.HttpParams;
import com.yangguangyulu.sunoleducation.http.retrofit.HttpManager;
import com.yangguangyulu.sunoleducation.http.retrofit.RequestResponse;
import com.yangguangyulu.sunoleducation.model.CorrectPersonInfoBean;
import com.yangguangyulu.sunoleducation.operator.UserManager;
import com.yangguangyulu.sunoleducation.presenter.BasePresenter;
import com.yangguangyulu.sunoleducation.presenter.interfaces.ILoginPresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.ILoginView;
import com.yangguangyulu.sunoleducation.util.CookieUtil;
import com.yangguangyulu.sunoleducation.util.Jsons;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/19.
 * Description:
 * Modified:
 */
@SuppressWarnings(value = {"unchecked", "unused"})
public class LoginPresenter extends BasePresenter<ILoginView> implements ILoginPresenter {

    @Override
    public void openLogin(String idCardNumber) {
        Log.d("yinqm", idCardNumber);
        mView.startLoading();
        Observable<String> loginResult = HttpManager.getInstance().createApiService().openLogin(idCardNumber);
        HttpManager.getInstance().toSubscribe(mView.getContext(),
                loginResult, HttpManager.getSubscriber(new RequestResponse<String>() {
                    @Override
                    public void onSuccess(String code, String message, JSONObject result) {
                        mView.stopLoading();
                        if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                            CorrectPersonInfoBean bean = Jsons.fromJson(result.toString(), CorrectPersonInfoBean.class);
                            mView.loginSuccess(bean);
                        } else if (Constants.HTTP_STATUS.ERR.equals(code)) {
                            mView.loginFailed(message);
                        } else {
                            onCodeError(code, message);
                            mView.showToast(message);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        mView.stopLoading();
                        mView.loginFailed(e.toString());
                    }
                }));
    }

    @Override
    public void getUserFaceUrl() {
        Observable<String> loginResult = HttpManager.getInstance().createApiService().getUserFaceUrl();
        HttpManager.getInstance().toSubscribe(mView.getContext(),
                loginResult, HttpManager.getSubscriber(new RequestResponse<String>() {
                    @Override
                    public void onSuccess(String code, String message, JSONObject result) {

                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);

                    }
                }));
    }

    @Override
    public void login(String idCardNumber, String password, String verifyCode) {
        mView.startLoading();
        HttpParams httpParams = new HttpParams();
        httpParams.putParam("accountName", idCardNumber);
        httpParams.putParam("pwd", password);
        httpParams.putParam("validateCode", verifyCode);
        httpParams.putParam("devType", "android");
        Observable<String> loginResult = HttpManager.getInstance().createApiService().login(httpParams.getRequestBody());

        HttpManager.getInstance().toSubscribe(mView.getContext(),
                loginResult, HttpManager.getSubscriber(new RequestResponse<String>() {
                    @Override
                    public void onSuccess(String code, String message, JSONObject result) {
                        mView.stopLoading();
                        if (Constants.HTTP_STATUS.SUCCESS.equals(code)) {
                            CorrectPersonInfoBean bean = Jsons.fromJson(result.toString(), CorrectPersonInfoBean.class);

                            mView.loginSuccess(bean);
                        } else {
                            onCodeError(code, message);
                            mView.showToast(message);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        mView.stopLoading();
                    }
                }));
    }

    /**
     * 下载人脸特征码
     *
     * @param url
     */
    public void downloadFaceFeature(String url) {

        final String SAVE_FEATURE_DIR = "register" + File.separator + "features";
        final String SAVE_FEATURE_NAME = "registerface";

        Call<ResponseBody> call = HttpManager.getInstance().createApiService().downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    boolean dirExists = true;

                    //特征存储的文件夹
                    File featureDir = new File(mView.getContext().getFilesDir().getAbsolutePath() + File.separator + SAVE_FEATURE_DIR);
                    if (!featureDir.exists()) {
                        dirExists = featureDir.mkdirs();
                    }
                    if (dirExists) {
                        InputStream is = response.body().byteStream();
                        File featureFile = new File(mView.getContext().getFilesDir().getAbsolutePath() + File.separator + SAVE_FEATURE_DIR + File.separator + SAVE_FEATURE_NAME);
                        FileOutputStream fos = new FileOutputStream(featureFile);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            fos.flush();
                        }
                        fos.close();
                        bis.close();
                        is.close();
                        mView.downloadSuccess();
                    } else {
                        UserManager.removeAll(mView.getContext());
                        CookieUtil.setCookie("", mView.getContext());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    UserManager.removeAll(mView.getContext());
                    CookieUtil.setCookie("", mView.getContext());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                UserManager.removeAll(mView.getContext());
                CookieUtil.setCookie("", mView.getContext());
            }
        });
    }
}
