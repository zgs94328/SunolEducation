package com.yangguangyulu.sunoleducation.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;

import androidx.appcompat.app.AlertDialog;

@SuppressWarnings("unused")
public class AlertDialogUtil {

    /**
     * 弹出有一个按钮确认的提示框
     *
     * @param content 提示内容
     */
    public static AlertDialog alert(Context context, String content) {
        return alert(context, content, null, null);
    }

    /**
     * 弹出有一个按钮确认的提示框
     *
     * @param content 提示内容
     * @param btnStr  按钮文字
     */
    public static AlertDialog alert(Context context, String content, String btnStr) {
        return alert(context, content, btnStr, null);
    }

    /**
     * 弹出有一个按钮确认的提示框
     *
     * @param content  提示内容
     * @param listener 点击按钮要做的处理
     */
    public static AlertDialog alert(Context context, String content, final AlertListener listener) {
        return alert(context, content, null, listener);
    }

    /**
     * 弹出有一个按钮确认的提示框
     *
     * @param content 提示内容
     * @param btnStr  确定按钮显示文字
     */
    public static AlertDialog alert(Context context, String content, String btnStr, final AlertListener listener) {
        View view = View.inflate(context, R.layout.alert, null);
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
        mAlertDialog.setView(view);
        mAlertDialog.show();
        Window window = mAlertDialog.getWindow();
        if (null != window) {
            window.setContentView(R.layout.alert);
            //AlertDialog自定义界面圆角有背景问题    有白色背景，加这句代码
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView contentView = window.findViewById(R.id.content);
            contentView.setText(Html.fromHtml(content));
            TextView btn_ok = window.findViewById(R.id.btn_ok);
            if (btnStr != null && !btnStr.isEmpty()) {
                btn_ok.setText(btnStr);
            }
            btn_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.doConfirm();
                    }
                    mAlertDialog.dismiss();
                }
            });
        }
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        return mAlertDialog;
    }


    /**
     * 有两个按钮的确认对话框（按钮文字，点击操作都是默认的）
     *
     * @param content 提示内容
     */
    public static AlertDialog confirm(Context context, String content) {
        return confirm(context, content, null, null, null);
    }

    /**
     * 有两个按钮的确认对话框（按钮使用默认文字）
     *
     * @param content  提示内容
     * @param listener 点击按钮的操作
     */
    public static AlertDialog confirm(Context context, String content, final ConfirmListener listener) {
        return confirm(context, content, null, null, listener);
    }

    public static AlertDialog confirm(Context context, String content, String btnOkStr, String btnCancelStr) {
        return confirm(context, content, btnOkStr, btnCancelStr, null);
    }

    /**
     * 有两个按钮的确认对话框
     *
     * @param content      提示内容
     * @param btnOkStr     确认按钮文字
     * @param btnCancelStr 取消按钮文字
     * @param listener     按钮被点击时的操作
     */
    public static AlertDialog confirm(Context context, String content, String btnOkStr, String btnCancelStr,
                                      final ConfirmListener listener) {
        View view = View.inflate(context, R.layout.confirm, null);
        final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
        mAlertDialog.setView(view);
        mAlertDialog.show();
        Window window = mAlertDialog.getWindow();
        if (null != window) {
            window.setContentView(R.layout.confirm);
            //AlertDialog自定义界面圆角有背景问题    有白色背景，加这句代码
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            window.setGravity(Gravity.BOTTOM);
//            window.setWindowAnimations(R.style.animationBottom);
            TextView contentView = window.findViewById(R.id.content);
            contentView.setText(Html.fromHtml(content));
            TextView btn_ok = window.findViewById(R.id.btn_ok);
            if (btnOkStr != null && !btnOkStr.isEmpty()) {
                btn_ok.setText(btnOkStr);
            }
            btn_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onOkClick();
                    }
                    mAlertDialog.dismiss();
                }
            });

            TextView btn_cancel = window.findViewById(R.id.btn_cancel);
            if (btnCancelStr != null && !btnCancelStr.isEmpty()) {
                btn_cancel.setText(btnCancelStr);
            } else {
                btn_cancel.setVisibility(View.GONE);
                window.findViewById(R.id.bottom_middle_line).setVisibility(View.GONE);
            }

            btn_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCancelClick();
                    }
                    mAlertDialog.dismiss();
                }
            });
        }
        mAlertDialog.setCancelable(true);
        mAlertDialog.setCanceledOnTouchOutside(true);
        return mAlertDialog;
    }

    public interface AlertListener {
        /**
         * 点击确认按钮时需要做的处理
         */
        void doConfirm();
    }

    public interface ConfirmListener {
        /**
         * 当确认被点击时调用
         */
        void onOkClick();

        /**
         * 当取消被点击时调用
         */
        void onCancelClick();
    }

    public class AlertDialogBuilder {

    }

    /****
     **
     * 这是兼容的 AlertDialog
     * 这里使用了 android.support.v7.app.AlertDialog.Builder
     * 可以直接在头部写 import android.support.v7.app.AlertDialog
     * 那么下面就可以写成 AlertDialog.Builder
     * <p>
     * dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.color_primary));
     * dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.color_primary));
     * dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.color_secondary_text));
     * 弹出跳转设置界面提示框
     */
    public static void showPermissionDialog(final Context context, String content) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("温馨提示");

        if (null == content) {
            content = "";
        }
        builder.setMessage(content);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));

                localIntent.setAction(Settings.ACTION_SETTINGS);
                context.startActivity(localIntent);
            }
        });

        builder.setCancelable(false);
        builder.show();
    }
}
