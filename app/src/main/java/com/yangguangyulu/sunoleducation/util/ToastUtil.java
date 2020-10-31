package com.yangguangyulu.sunoleducation.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yangguangyulu.sunoleducation.R;

/***
 * Created by tangjian on 16-5-17.
 */

@SuppressWarnings("all")
public class ToastUtil {
    private static ToastUtil util;

    public static ToastUtil getInstance() {
        if (util == null) {
            synchronized (ToastUtil.class) {
                if (null == util) {
                    util = new ToastUtil();
                }
            }
        }
        return util;
    }

    private Toast toast;

    /**
     * 显示Toast
     */
    public void show(Context context, CharSequence text) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_SHORT);
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            //CalledFromWrongThreadException  Only the original thread that created a view hierarchy can touch its views
            e.printStackTrace();
        }
    }

    /**
     * 显示Toast
     */
    public void showLongToast(Context context, CharSequence text) {
        showLongToast(context, text, 0);
    }

    /**
     * 显示Toast
     */
    public void showLongToast(Context context, CharSequence text, int yOffset) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                toast.setText(text);
                toast.setDuration(Toast.LENGTH_LONG);
            }
            toast.setGravity(Gravity.CENTER, 0, yOffset);
            toast.show();
        } catch (Exception e) {
            //CalledFromWrongThreadException  Only the original thread that created a view hierarchy can touch its views
            e.printStackTrace();
        }
    }

    /**
     * 显示Toast
     */
    public void show(Context context, int resId) {
        if (toast == null) {
            toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public void showCustomToast(Context context, String content, OnToastDismissListener listener) {
        showCustomToast(context, content, -1, listener);
    }

    public void showCustomToast(Context context, int resImgId, OnToastDismissListener listener) {
        showCustomToast(context, null, -resImgId, listener);
    }

    public void showCustomToast(Context context, String content, int resImgId) {
        showCustomToast(context, content, -resImgId, null);
    }

    public void showCustomToast(final Context context, String content, int resImgId, final OnToastDismissListener listener) {
        // 自定义土司显示位置
        // 创建土司
        Toast toast = new Toast(context);
        // 找到toast布局的位置
        View layout = View.inflate(context, R.layout.custom_toast_layout, null);
        // 设置toast文本，把设置好的布局传进来
        if (resImgId > 0) {
            ImageView imageView = layout.findViewById(R.id.top_img);
            try {
                imageView.setImageResource(resImgId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(content)) {
            TextView textView = layout.findViewById(R.id.toast_content);
            textView.setText(content);
        }

        //回调时间尽量接近 Toast.LENGTH_SHORT 的时间
        new CountDownTimer(2100, 1150) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        }.start();
        toast.setView(layout);
        // 设置土司显示在屏幕的位置
//        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 70);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public interface OnToastDismissListener {
        void onDismiss();
    }
}
