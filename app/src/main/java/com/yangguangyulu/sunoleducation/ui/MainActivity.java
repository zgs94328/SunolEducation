package com.yangguangyulu.sunoleducation.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.base.BaseMvpActivity;
import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.model.CorrectPersonInfoBean;
import com.yangguangyulu.sunoleducation.model.UpdateInfo;
import com.yangguangyulu.sunoleducation.operator.AppManager;
import com.yangguangyulu.sunoleducation.operator.UpdateService;
import com.yangguangyulu.sunoleducation.operator.UpdateServiceConnection;
import com.yangguangyulu.sunoleducation.operator.UserManager;
import com.yangguangyulu.sunoleducation.presenter.impl.LoginPresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.ILoginView;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtils;
import com.yangguangyulu.sunoleducation.util.CookieUtil;
import com.yangguangyulu.sunoleducation.util.FileUtils;
import com.yangguangyulu.sunoleducation.util.PhonePackageUtils;
import com.yangguangyulu.sunoleducation.util.SpUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseMvpActivity<LoginPresenter> implements ILoginView, EasyPermissions.PermissionCallbacks {

    String[] perms = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private String idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = getIntent().getData();
        if (uri != null) {
            //获取指定参数值
            idNumber = uri.getQueryParameter("params");
        } else {
            showToast("身份信息获取失败，请重新尝试");
            AppManager.getAppManager().AppExit();
        }
        registerBroadCast();
        mPresenter.getUserFaceUrl();
    }

    @Override
    public LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    private void login() {
        mPresenter.openLogin(idNumber);
    }

    UpdateServiceConnection conn;

    @Override
    public void loginSuccess(CorrectPersonInfoBean bean) {
        UserManager.putUserId(this, bean.getCorrectPersonInfo().getId());
        UserManager.putUserName(this, bean.getCorrectPersonInfo().getName());
        UserManager.putCurrectStatus(this, bean.getCorrectPersonInfo().getCurrectStatus());
        UserManager.putArcFaceUrl(this, bean.getCorrectPersonInfo().getArcFaceUrl());

        CookieUtil.setCookie(bean.getAccessToken(), this);

        if (bean.getAppVersions() != null && compareVersion(bean.getAppVersions()) == -1) {
            AlertDialogUtils.showDialog(this, "您有一个新版本已准备好开始更新", "取消", "确定", new AlertDialogUtils.OnAlertClickListener() {
                @Override
                public void onCancel() {
                    onBackPressed();
                }

                @Override
                public void onConfirm() {
                    startDownService(bean.getAppVersions());
                }
            });
        } else {
            if (bean.getCorrectPersonInfo().getArcFaceUrl() != null && !bean.getCorrectPersonInfo().getArcFaceUrl().isEmpty()) {
                mPresenter.downloadFaceFeature(bean.getCorrectPersonInfo().getArcFaceUrl());
            } else {
                activeEngine();
            }
        }

    }

    /**
     * 版本号比较
     * 0代表相等，1代表version1大于version2，-1代表version1小于version2
     *
     * @return
     */
    private int compareVersion(UpdateInfo updateInfo) {
        String version1 = PhonePackageUtils.getAPKVersion(this);
        String version2 = updateInfo.getVersionNumber();

        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        // 循环判断每位的大小
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public void clearCookies() {
        SharedPreferences cookiePrefs = this.getSharedPreferences("Cookies_Prefs", 0);
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
    }

    @Override
    public void loginFailed(String message) {
        AlertDialogUtil.alert(this, message + "", "确定", () -> {
                    clearCookies();
                    AppManager.getAppManager().AppExit();
                }
        );

    }

    @Override
    public void downloadSuccess() {
        activeEngine();
    }



    public void startDownService(UpdateInfo info) {
        Intent updateService = new Intent(this, UpdateService.class);
        updateService.putExtra("downLoadUrl", info.getAndroidUrl());
        updateService.putExtra("isForceToUpdate", true);
        updateService.putExtra("newVersionNumber", info.getVersionNumber());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(updateService);
        } else {
            this.startService(updateService);
        }
        conn = new UpdateServiceConnection();
        bindService(updateService, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 运行时权限
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(MainActivity.this, "为保证程序正常运行，请提供运行时权限。", 10000, perms);
        } else {
            login();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 10000) {
            login();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 10000) {
            PhonePackageUtils.gotoSecuritySetting(this);
        }
    }

    private void registerBroadCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BroadCastReceiver.DOWNLOAD_APK_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    /**
     * 激活引擎
     */
    public void activeEngine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                FaceEngine faceEngine = new FaceEngine();
                int activeCode = faceEngine.active(MainActivity.this,
                        Constants.faceConstants.FACE_APP_ID,
                        Constants.faceConstants.FACE_SDK_KEY);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast("识别引擎激活成功");
                            SpUtils.put(getContext(), "hasRegFaceEngine", true);
                            startActivity(new Intent(MainActivity.this, EducationActivity.class));
                            finish();
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast("识别引擎已激活");
                            SpUtils.put(getContext(), "hasRegFaceEngine", true);
                            startActivity(new Intent(MainActivity.this, EducationActivity.class));
                            finish();
                        } else if (activeCode == ErrorInfo.MERR_ASF_UNIQUE_IDENTIFIER_MISMATCH) {
                            SpUtils.put(getContext(), "hasRegFaceEngine", false);
                            FileUtils.cleanApplicationData(MainActivity.this, null);
                            startActivity(new Intent(MainActivity.this, EducationActivity.class));
                            finish();
                        } else {
                            SpUtils.put(getContext(), "hasRegFaceEngine", false);
                            startActivity(new Intent(MainActivity.this, EducationActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        SpUtils.put(getContext(), "hasRegFaceEngine", false);
                        startActivity(new Intent(MainActivity.this, EducationActivity.class));
                        finish();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    /**
     * 8.0以上系统设置安装未知来源权限
     */
    public void setInstallPermission() {
        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先判断是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                //弹框提示用户手动打开
                AlertDialogUtil.alert(this, "需要打开允许来自此来源，请去设置中开启此权限", new MyAlertListener());
                return;
            } else {
                String apkPath = Constants.Config.DEFAULT_SAVE_FILE_PATH + File.separator + Constants.Config.APK_NAME;
                PhonePackageUtils.installApk(this, apkPath);
                AppManager.getAppManager().AppExit();
            }
        } else {
            String apkPath = Constants.Config.DEFAULT_SAVE_FILE_PATH + File.separator + Constants.Config.APK_NAME;
            PhonePackageUtils.installApk(this, apkPath);
            AppManager.getAppManager().AppExit();
        }
    }

    class MyAlertListener implements AlertDialogUtil.AlertListener {
        @Override
        public void doConfirm() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //此方法需要API>=26才能使用
                toInstallPermissionSettingIntent();
            }
        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Constants.BroadCastReceiver.DOWNLOAD_APK_COMPLETE:
                        setInstallPermission();
                        break;
                }
            }
        }
    };

    final int INSTALL_PERMISS_CODE = 1000;

    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toInstallPermissionSettingIntent() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, INSTALL_PERMISS_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INSTALL_PERMISS_CODE) {
            String apkPath = Constants.Config.DEFAULT_SAVE_FILE_PATH + File.separator + Constants.Config.APK_NAME;
            PhonePackageUtils.installApk(this, apkPath);
            AppManager.getAppManager().AppExit();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
