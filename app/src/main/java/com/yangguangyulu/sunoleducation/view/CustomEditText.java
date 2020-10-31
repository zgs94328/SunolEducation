package com.yangguangyulu.sunoleducation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/5/28.
 * Description:
 * Modified:
 */

public class CustomEditText extends AppCompatEditText {
    private OnKeyDownListener onKeyDownListener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnKeyDownListener(OnKeyDownListener onKeyDownListener) {
        this.onKeyDownListener = onKeyDownListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
            if (null != onKeyDownListener) {
                onKeyDownListener.onKeyDown();
            }
            super.onKeyPreIme(keyCode, event);
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public interface OnKeyDownListener {
        void onKeyDown();
    }
}
