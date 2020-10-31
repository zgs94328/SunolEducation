package com.yangguangyulu.sunoleducation.base;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/12.
 * Description:
 * Modified:
 */

@SuppressWarnings("unused")
public interface IBasePresenter<V> {
    void attachView(V view);

    void detachView();
}
