package com.yangguangyulu.sunoleducation.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.http.retrofit.SubscriberManager;
import com.yangguangyulu.sunoleducation.operator.AppManager;
import com.yangguangyulu.sunoleducation.util.PhonePackageUtils;
import com.yangguangyulu.sunoleducation.widget.LoadingDialog;
import com.yangguangyulu.sunoleducation.widget.StatusBar.StatusBarUtil;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.Unbinder;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/12.
 * Description:
 * Modified:
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static boolean isForeground = false;

    /**
     * 保存上一次点击时间
     */
    //1.在数据量小的时候一般认为1000以下，当你的key为int的时候，使用SparseArray确实是一个很不错的选择，
    // 内存大概能节省30%，相比用HashMap，因为它key值不需要装箱，所以时间性能平均来看也优于HashMap
    //2.ArrayMap相对于SparseArray，特点就是key值类型不受限，任何情况下都可以取代HashMap, 但是通过研究和测试发现，
    // ArrayMap的内存节省并不明显，也就在10%左右，但是时间性能却是最差的，当然了，1000以内的数据量也无所谓了，另外
    // 它只有在API>=19才可以使用。
    private SparseArray<Long> lastClickTimes;

    /**
     * 加载进度 （如需修改样式，改show方法就可以了）
     */
    protected LoadingDialog loadingDialog;

    protected Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
        lastClickTimes = new SparseArray<>();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setStatusBar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setStatusBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setBtnBackClick();
    }

    /***
     * 打开另外一个Activity
     */
    public void startActivity(Class clazz) {
        startActivity(clazz, -1);
    }

    /***
     * 打开另外一个Activity
     */
    public void startActivity(Class clazz, int requestCode) {
        startActivityForResult(new Intent(this, clazz), requestCode);
    }

    protected void setBtnBackClick() {
        View view = findViewById(R.id.btn_back);
        if (null != view) {
            view.setOnClickListener(v -> finish());
        }
    }

    /**
     * 设置页面标题，必须在onStart之后调用
     */
    @Override
    public void setTitle(CharSequence title) {
        TextView textView = findViewById(R.id.txt_Title);
        if (null != textView) {
            textView.setText(title);
        }
    }

    /**
     * 设置页面标题，必须在onStart之后调用
     */
    @Override
    public void setTitle(int titleId) {
        TextView textView = findViewById(R.id.txt_Title);
        if (null != textView && titleId > 0) {
            textView.setText(titleId);
        }
    }

    /**
     * 检查是否可执行点击操作 防重复点击
     *
     * @return 返回true则可执行
     */
    public boolean checkClick(int id) {
        Long lastTime = lastClickTimes.get(id);
        Long thisTime = System.currentTimeMillis();
        lastClickTimes.put(id, thisTime);
        return !(lastTime != null && thisTime - lastTime < 800);
    }

    @Override
    protected void onDestroy() {
        SubscriberManager.getInstance().removeSubscription(this);
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().removeActivity(this);
        lastClickTimes = null;

        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    /**
     * 设置状态栏
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.main_theme_color), 0);
            StatusBarUtil.setStatusTextColor(true, this);
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && getCurrentFocus() != null
                && getCurrentFocus().getWindowToken() != null) {

            View focusedView = getCurrentFocus();
            if (isShouldHideKeyboard(focusedView, event)) {
                PhonePackageUtils.hideKeyboard(this, focusedView.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * 该方法最初只能处理当前界面只有一个EditText控件的情况
     * 所以自己增加了一个方法来处理 isClickOnEditText(MotionEvent event)
     */
    private boolean isShouldHideKeyboard(View focusedView, MotionEvent event) {
        if (isClickOnEditText(event)) {
            return false;
        }

        if (focusedView instanceof EditText) {
            int[] location = {0, 0};
            focusedView.getLocationOnScreen(location);
            int left = location[0], top = location[1],
                    bottom = top + focusedView.getHeight(), right = left + focusedView.getWidth();
            if (event.getRawX() > left
                    && event.getRawX() < right
                    && event.getRawY() > top
                    && event.getRawY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                focusedView.clearFocus();
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上
        return false;
    }

    /***
     * 检查是否是点击了当前Activity中的EditText
     */
    public boolean isClickOnEditText(MotionEvent event) {
        return false;
    }

    /*
     * 检查是否是点击了当前Activity中的EditText
     */
//    public boolean checkClickArea(MotionEvent event, View... views) {
//        int[] location = {0, 0};
//
//        if (null != views && views.length > 0) {
//            for (View view : views) {
//                view.getLocationOnScreen(location);
//                int left = location[0], top = location[1],
//                        bottom = top + view.getHeight(), right = left + view.getWidth();
//                if (event.getRawX() > left && event.getRawX() < right
//                        && event.getRawY() > top && event.getRawY() < bottom) {
//                    // 点击EditText的事件，忽略它。
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
