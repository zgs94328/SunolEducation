package com.yangguangyulu.sunoleducation.presenter.interfaces;

import com.yangguangyulu.sunoleducation.base.IBasePresenter;
import com.yangguangyulu.sunoleducation.ui.interfaces.ILoginView;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/19.
 * Description:
 * Modified:
 */

public interface ILoginPresenter extends IBasePresenter<ILoginView> {
    void login(String idCardNumber, String password, String verifyCode);

    void openLogin(String idCardNumber);
    void getUserFaceUrl();

}
