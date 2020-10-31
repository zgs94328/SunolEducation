package com.yangguangyulu.sunoleducation.presenter;

import com.yangguangyulu.sunoleducation.base.IBasePresenter;
import com.yangguangyulu.sunoleducation.base.IBaseView;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/12.
 * Description:
 * Modified:
 */
@SuppressWarnings("all")
public abstract class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {
    protected V mView;

    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }
}
