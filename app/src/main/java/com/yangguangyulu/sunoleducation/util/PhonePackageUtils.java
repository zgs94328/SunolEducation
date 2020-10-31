package com.yangguangyulu.sunoleducation.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.util.List;

import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/3/2.
 * Description:
 * Modified: by TangJian on 2017/3/2.
 */

@SuppressWarnings("unused")
public class PhonePackageUtils {
    public static final String DEFAULT_APK_VERSION = "0.0.0";

    public static String getAPKVersion(Context context) {
        String versionName = DEFAULT_APK_VERSION;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getPackageName(Context context) {
        String packageName = DEFAULT_APK_VERSION;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            packageName = info.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageName;
    }

    /**
     * 判断是否安装了支付宝
     *
     * @param applicationName 应用名称 Uri  如支付宝：alipays://
     * @return true 为已经安装
     */
    public static boolean hasApplication(Context context, String applicationName) {
        PackageManager manager = context.getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse(applicationName));
        List<ResolveInfo> list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    /***
     * 关闭软键盘
     */
    public static void closeKeyboard(Activity context, EditText... editTexts) {
        View view = context.getWindow().getDecorView();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && inputMethodManager != null) {
            if (null != editTexts) {
                for (EditText editText : editTexts) {
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            } else {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    public static void hideKeyboard(Activity context, IBinder token) {
        if (token != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mInputMethodManager != null) {
                mInputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 弹出键盘
     */
    public static void showKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, 0);
        }
    }

    /**
     * 判断键盘是否显示
     * 经测试，这个inputMethodManager.isActive(editText)方法，只要editText有焦点，它就返回true... 但是并不是EditText获得焦点，键盘就会弹出
     * 所以想要判断软键盘是否在显示，总的通过动态计算布局来解决。
     */
    public static boolean isSoftShowing(Activity context) {
        //获取当前屏幕内容的高度
        int screenHeight = context.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom - getSoftButtonsBarHeight(context) != 0;
    }

    /**
     * 底部虚拟按键栏的高度
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        context.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            uri = FileProvider.getUriForFile(context, "com.ygyl.fileProvider", apkFile);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(apkPath));
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        context.startActivity(intent);
    }

    public static void startNewApp(Context context, String packageName, String className) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    public static void startNewApp(Context context, String packageName) {
        // 通过包名获取要跳转的app，创建intent对象
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        // 这里如果intent为空，就说名没有安装要跳转的应用嘛
        if (intent != null) {
            // 这里跟Activity传递参数一样的嘛，不要担心怎么传递参数，还有接收参数也是跟Activity和Activity传参数一样
//            intent.putExtra("name", "Liu xiang");
//            intent.putExtra("birthday", "1983-7-13");
            context.startActivity(intent);
        }
    }

    /**
     * 根据包名判断是否已安装APP
     */
    public static boolean hasInstalledApk(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                if (packageInfos.get(i).packageName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取手机IMEI码
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String getPhoneIMEI(Activity aty) {
        TelephonyManager tm = (TelephonyManager) aty.getSystemService(Activity.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /***
     * 跳转到当前应用的详情界面，然后进入该应用的权限管理界面
     */
    public static void gotoSecuritySetting(Context activity) {
        if (null != activity) {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            localIntent.setAction(Settings.ACTION_SETTINGS);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            activity.startActivity(localIntent);
        }
    }
}
