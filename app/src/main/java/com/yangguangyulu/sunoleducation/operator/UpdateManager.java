package com.yangguangyulu.sunoleducation.operator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.model.UpdateInfo;
import com.yangguangyulu.sunoleducation.util.AlertDialogUtil;
import com.yangguangyulu.sunoleducation.util.DMCache;
import com.yangguangyulu.sunoleducation.util.PhonePackageUtils;
import com.yangguangyulu.sunoleducation.util.SpUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by yinqm on 2019/06/13
 * Description:
 * Modified:
 */

@SuppressWarnings("unchecked")
public class UpdateManager {
    private static UpdateManager instance;
    private WeakReference<Context> mContext;
    private UpdateInfo updateInfo;

    private UpdateManager(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    //由于登录界面和首页都做了版本更新的判断，所以不能用单例了。
    public static UpdateManager getInstance(Context context) {
        instance = new UpdateManager(context);
        return instance;
    }

    /**
     * 该字段是为了防止用户下载完apk后，不安装，并且立马结束应用。在下次启动时会用到
     */
    private boolean needToInstallNewVersionApk = false;
    /**
     * 是否强制更新
     */
    private boolean isForceToUpdate;
    private boolean isDowningApk;  //是否已经开始下载apk

    public void checkUpdate(UpdateInfo info) {
        this.updateInfo = info;
        if (!hasDownNewVersionApk()) {
            try {
                if (null != updateInfo) {
                    checkUpdateInfo(updateInfo);
                    //保存版本更新信息
                    DMCache dmCache = DMCache.get(mContext.get());
                    dmCache.put(Constants.CONSTANT_STRING.PH_UPDATE_INFO, updateInfo);
                }
            } catch (Exception e) {

            }
        } else {
            //读取缓存中的版本更新信息
            DMCache dmCache = DMCache.get(mContext.get());
            updateInfo = (UpdateInfo) dmCache.getAsObject(Constants.CONSTANT_STRING.PH_UPDATE_INFO);
            if (null != updateInfo && isNeedUpdate()) {
                needToInstallNewVersionApk = true;
                installNewVersionApk();
            } else {
                SpUtils.put(mContext.get(), Constants.HAS_DOWN_NEW_VERSION, false);
                checkUpdate(updateInfo);
            }
        }
    }

    /***
     * 当下载提示已出现，但是用户下载了却没有选择安装（或者用户没有放开文件读写权限，导致下载失败后），检测是否有新版本apk需要安装
     */
    public void hasNewVersionApkToInstall() {
        try {
            if (null != updateInfo) {
                //如果不是必须更新则返回
                if (!updateInfo.getMandatoryUpdate()) {
                    return;
                }
                //判断是否已下载了最新apk. 这里的原因可能就是已经下载了apk，但是用户却没有安装
                if (hasDownNewVersionApk()) {
                    //比较版本，判断是否需要更新
                    if (!isNeedUpdate()) {
                        return;
                    }
                    installNewVersionApk();
                } else {
                    //到这里，可能的原因就是用户没有放开文件读写权限，导致下载失败
                    checkUpdateInfo(updateInfo);
                }
            } else if (needToInstallNewVersionApk) {
                installNewVersionApk();
            }
        } catch (Exception e) {

        }
    }

    private boolean hasShowUpdateDialog = false;

    /**
     * 提示安装本地已下载好的新版本apk
     */
    private void installNewVersionApk() {
        isForceToUpdate = (boolean) SpUtils
                .get(mContext.get(), Constants.IS_FORCE_TO_UPDATE, false);

        if (!hasShowUpdateDialog) {
            hasShowUpdateDialog = true;
            String description = "您有新的客户端版本，无需下载，请及时安装!";
            AlertDialog alertDialog = AlertDialogUtil.alert(
                    mContext.get(), description, () -> {
                        String apkPath = Constants.Config.DEFAULT_SAVE_FILE_PATH + File.separator + Constants.Config.APK_NAME;
                        PhonePackageUtils.installApk(mContext.get(), apkPath);
                    }
            );

            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    hasShowUpdateDialog = false;
                }
            });

            alertDialog.setCancelable(!isForceToUpdate);
            alertDialog.setCanceledOnTouchOutside(!isForceToUpdate);
        }
    }

    private void checkUpdateInfo(final UpdateInfo updateInfo) {
        //判断是否需要更新
        if (!isNeedUpdate()) {
            SpUtils.put(mContext.get(), Constants.HAS_NEW_VERSION_TO_DOWN, false);
            return;
        }

        if (isDowningApk) {  //若是已经开始下载了，则不用再弹出了。
            isDowningApk = false;
            return;
        }

        SpUtils.put(mContext.get(), Constants.HAS_NEW_VERSION_TO_DOWN, false);
        startDownService();
    }

    /**
     * 开始下载apk
     */
    private void startDownService() {
        isDowningApk = true;
        SpUtils.put(mContext.get(),
                Constants.HAS_NEW_VERSION_TO_DOWN, false);

        Intent updateService = new Intent(mContext.get(), UpdateService.class);
        updateService.putExtra("downLoadUrl", updateInfo.getAndroidUrl());
        updateService.putExtra("isForceToUpdate", isForceToUpdate);
        updateService.putExtra("newVersionNumber", updateInfo.getVersionNumber());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.get().startForegroundService(updateService);
        } else {
            mContext.get().startService(updateService);
        }

        UpdateServiceConnection conn = new UpdateServiceConnection();
        mContext.get().bindService(updateService, conn, Context.BIND_AUTO_CREATE);
    }

    /***
     * 判断是否需要更新
     *  1、如果字符串相等返回值0
     *  2、如果第一个字符和参数的第一个字符不等,结束比较,返回他们之间的差值（ascii码值）
     *      （负值前字符串的值小于后字符串，正值前字符串大于后字符串）
     *  3、如果第一个字符和参数的第一个字符相等,则以第二个字符和参数的第二个字符做比较,
     *      以此类推,直至比较的字符或被比较的字符有一方全比较完,这时就比较字符串的长度.
     * 比较当前apk版本 和 版本更新信息的versionName
     */
    private boolean isNeedUpdate() {

        String currentVersionName = PhonePackageUtils.getAPKVersion(mContext.get());
        String updateVersionName = updateInfo.getVersionNumber();
        boolean needUpdate = updateVersionName.compareTo(currentVersionName) > 0;
        Log.e("checkUpdateInfo", "*****server version = " + updateVersionName
                + "*****current version = " + currentVersionName + "****needUpdate " + "= " + needUpdate);
        return needUpdate;
    }

    /**
     * 是否已下载了最新的apk包，并判断该包是否已被删除
     */
    private boolean hasDownNewVersionApk() {
        boolean hasDownNewVersion = (boolean) SpUtils
                .get(mContext.get(), Constants.HAS_DOWN_NEW_VERSION, false);

        if (hasDownNewVersion) {
            //防止用户将已下载的apk删除后引起异常
            String saveFileName = Constants.Config.DEFAULT_SAVE_FILE_PATH + File.separator + Constants.Config.APK_NAME;
            File file = new File(saveFileName);
            if (!file.exists()) {
                hasDownNewVersion = false;
                SpUtils.put(mContext.get(), Constants.HAS_DOWN_NEW_VERSION, false);
            }
        }
        return hasDownNewVersion;
    }

}
