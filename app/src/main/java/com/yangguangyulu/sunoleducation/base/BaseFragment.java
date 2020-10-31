package com.yangguangyulu.sunoleducation.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.operator.AppManager;
import com.yangguangyulu.sunoleducation.presenter.BasePresenter;
import com.yangguangyulu.sunoleducation.util.Strings;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.Unbinder;

/**
 * 版    权:  人人普惠
 * 描    述:  <描述>
 * Created by tangjian on 2017/2/25.
 */
@SuppressWarnings("unused")
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IBaseView {

    protected P mPresenter;

    public abstract P initPresenter();

    protected View mView;
    protected Unbinder unbinder;

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = initPresenter();
        if (null != mPresenter) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        stopLoading();
        if (null != mPresenter) {
            mPresenter.detachView();
        }

    }

    /**
     * 设置页面Title
     */
    public void setTitle(CharSequence title) {
        TextView titleTv = mView.findViewById(R.id.txt_Title);
        if (null != titleTv) {
            titleTv.setText(title);
        }
    }

    /**
     * 设置页面Title
     */
    public void setTitle(int titleId) {
        TextView titleTv = mView.findViewById(R.id.txt_Title);
        if (null != titleTv) {
            titleTv.setText(titleId);
        }
    }

    /**
     * 检查是否可执行点击操作 防重复点击
     *
     * @return 返回true则可执行
     */
    public boolean checkClick(int id) {
        BaseActivity activity = (BaseActivity) getActivity();
        return null != activity && activity.checkClick(id);
    }

    protected void initView() {
    }

    protected void initData() {
    }

    protected void initListener() {
    }

    /***
     * 结束界面
     */
    public void finishActivity() {
        stopLoading();
        if (null != getActivity()) {
            getActivity().finish();
        }
    }

    public void startActivity(Class clazz) {
        if (null != getActivity()) {
            ((BaseMvpActivity) getActivity()).startActivity(clazz);
        }
    }

    @Override
    public void showToast(String content) {
        if (null != getActivity()) {
            ((BaseMvpActivity) getActivity()).showToast(content);
        }
    }

    @Override
    public void startLoading() {
        if (null != getActivity()) {
            ((BaseMvpActivity) getActivity()).startLoading();
        }
    }

    @Override
    public void startLoading(boolean canCancel) {
        if (null != getActivity()) {
            ((BaseMvpActivity) getActivity()).startLoading(canCancel);
        }
    }

    @Override
    public void startLoading(String text, boolean canCancel) {
        if (!Strings.isEmptyOrNull(text) && null != getActivity()) {
            ((BaseMvpActivity) getActivity()).startLoading(text, canCancel);
        }
    }

    @Override
    public boolean isShowing() {
        BaseMvpActivity activity = (BaseMvpActivity) getActivity();
        return null != activity && activity.isShowing();
    }

    @Override
    public void stopLoading() {
        if (null != getActivity()) {
            ((BaseMvpActivity) getActivity()).stopLoading();
        }
    }

    @Override
    public Activity getContext() {
        BaseMvpActivity activity = (BaseMvpActivity) getActivity();
        if (null != activity) {
            return activity.getContext();
        } else {
            return AppManager.getAppManager().currentActivity();
        }
    }

    /***
     * 检查是否是点击了当前Activity中的EditText
     * 在控制软键盘的显示与隐藏的时候需要用到该方法
     */
    public boolean isClickOnEditText(MotionEvent event) {
        return false;
    }
}
