package com.yangguangyulu.sunoleducation.base;

import android.app.Activity;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/12.
 * Description:
 * Modified:
 */
@SuppressWarnings("unused")
public interface IBaseView {
    /**
     * 弹提示
     */
    void showToast(String content);

    /**
     * 显示加载中
     */
    void startLoading();

    void startLoading(boolean canCancel);

    void startLoading(String text, boolean canCancel);

    /**
     * 是否正在显示加载框
     */
    boolean isShowing();

    /**
     * 停止加载
     */
    void stopLoading();

    /***
     * 获取当前activity
     */
    Activity getContext();
}
