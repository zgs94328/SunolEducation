package com.yangguangyulu.sunoleducation.widget;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;

import java.lang.ref.WeakReference;

/**
 * 描    述:  <描述>
 * 修 改 人:  tangjian
 * 修改时间:  2016/6/20
 */
public class LoadingDialog {
    /**
     * 旋转动画的时间
     */
    private static final int ROTATION_ANIMATION_DURATION = 1200;
    /**
     * 动画插值
     */
    private static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    private Dialog dialog;
    private TextView textView;

    public static LoadingDialog getInstance(WeakReference<Activity> activity) {
        return new LoadingDialog(activity);
    }

    @SuppressWarnings("deprecation")
    public LoadingDialog(WeakReference<Activity> activity) {
        if (null != activity.get() && !activity.get().isFinishing()) {
            View view = View.inflate(activity.get(), R.layout.loading_dialog, null);
            ImageView imageView = view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.load_text);

            AnimationDrawable ad = (AnimationDrawable)
                    activity.get().getResources().getDrawable(R.drawable.loading_progress_round);
            imageView.setBackgroundDrawable(ad);
            ad.start();

            dialog = new Dialog(activity.get(), R.style.loading_dialog);// 加入样式
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            if (null != window) {
                window.setGravity(Gravity.CENTER);
                window.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
//        window.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    public void show() {
        if (null != dialog && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void show(String content) {
        if (null != dialog && !dialog.isShowing()) {
            textView.setText(content);
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void setCancelable(boolean canCancel) {
        if (null != dialog) {
            dialog.setCancelable(canCancel);
        }
    }
}
