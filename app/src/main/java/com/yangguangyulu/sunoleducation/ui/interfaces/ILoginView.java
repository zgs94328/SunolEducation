package com.yangguangyulu.sunoleducation.ui.interfaces;


import com.yangguangyulu.sunoleducation.base.IBaseView;
import com.yangguangyulu.sunoleducation.model.CorrectPersonInfoBean;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/19.
 * Description:
 * Modified:
 */

public interface ILoginView extends IBaseView {

    void loginSuccess(CorrectPersonInfoBean correctPersonInfoBean);

    void loginFailed(String message);

    void downloadSuccess();
}
