package com.yangguangyulu.sunoleducation.util;

import android.content.Context;
import android.content.DialogInterface;

import com.yangguangyulu.sunoleducation.R;

import androidx.appcompat.app.AlertDialog;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/3/17.
 * Description: 使用Material Design的弹框方式
 * Modified By:
 */

@SuppressWarnings("all")
public class AlertDialogUtils {

    public static AlertDialog showDialog(Context context, String content, OnAlertClickListener listener) {
        return showDialog(context, null, content, null, null, listener);
    }

    public static AlertDialog showDialog(Context context, String content, String negativeStr, String positiveStr) {
        return showDialog(context, null, content, negativeStr, positiveStr, null);
    }

    public static AlertDialog showDialog(Context context, String content, String positiveStr, OnAlertClickListener listener) {
        return showDialog(context, null, content, null, positiveStr, listener);
    }

    public static AlertDialog showDialog(Context context, String content, String negativeStr, String positiveStr, OnAlertClickListener listener) {
        return showDialog(context, null, content, negativeStr, positiveStr, listener);
    }

    /**
     * 这是兼容的 AlertDialog
     * 这里使用了 android.support.v7.app.AlertDialog.Builder
     * 可以直接在头部写 import android.support.v7.app.AlertDialog
     * 那么下面就可以写成 AlertDialog.Builder
     * <p>
     * dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.color_primary));
     * dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.color_primary));
     * dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.color_secondary_text));
     */
    public static AlertDialog showDialog(Context context, String title,
                                         String content, String negativeStr, String positiveStr, final OnAlertClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (Strings.isEmptyOrNull(title)) {
            title = "温馨提示";
        }
        builder.setTitle(title);

        if (null == content) {
            content = "";
        }
        builder.setMessage(content);

        if (!Strings.isEmptyOrNull(negativeStr)) {
            builder.setNegativeButton(negativeStr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != listener) {
                        listener.onCancel();
                    }
                }
            });
        }

        if (!Strings.isEmptyOrNull(positiveStr)) {
            builder.setPositiveButton(positiveStr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != listener) {
                        listener.onConfirm();
                    }
                }
            });
        }

        builder.setCancelable(false);
        return builder.show();
    }

    public interface OnAlertClickListener {
        void onCancel();

        void onConfirm();
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

    /**
     * WiFi提示
     */
    public static void showWifiDialog(Context context, final OnConfirmListener listener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(context.getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (null != listener) {
                    listener.onConfirm();
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /****
     * 弹出跳转设置界面提示框
     */
    public static AlertDialog showPermissionDialog(Context context, String content, final OnAlertClickListener listener) {
        return showDialog(context, content, "取消", "去设置", listener);
    }
}
