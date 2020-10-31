package com.yangguangyulu.sunoleducation.base;

import android.app.Activity;
import android.os.Bundle;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.presenter.BasePresenter;
import com.yangguangyulu.sunoleducation.util.Strings;
import com.yangguangyulu.sunoleducation.util.ToastUtil;
import com.yangguangyulu.sunoleducation.widget.LoadingDialog;

import java.lang.ref.WeakReference;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/26.
 * Description:
 * Modified:
 */
public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity implements IBaseView {

    protected P mPresenter;

    public abstract P initPresenter();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
        if (null != mPresenter) {
            mPresenter.attachView(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLoading();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showToast(String content) {
        if (!Strings.isEmptyOrNull(content)) {
            ToastUtil.getInstance().show(this, content);
        }
    }

    public void startLoading() {
        startLoading(getString(R.string.loading), true);
    }

    @Override
    public void startLoading(boolean canCancel) {
        startLoading(getString(R.string.loading), canCancel);
    }

    @Override
    public void startLoading(String text, boolean canCancel) {
        if (!isShowing() && !isFinishing()) {
            loadingDialog = LoadingDialog.getInstance(new WeakReference<Activity>(this));
            loadingDialog.show(text);
            loadingDialog.setCancelable(false);  //暂时先都设置为不可取消
        }
    }

    @Override
    public boolean isShowing() {
        if (loadingDialog == null) {
            return false;
        } else if (loadingDialog.isShowing()) {
            return true;
        } else {
            loadingDialog = null;
            return false;
        }
    }

    @Override
    public void stopLoading() {
        if (isShowing() && !isFinishing() && null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public Activity getContext() {
        return this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLoading();
    }
}
