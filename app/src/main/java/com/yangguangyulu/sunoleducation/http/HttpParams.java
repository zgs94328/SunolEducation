package com.yangguangyulu.sunoleducation.http;

import com.yangguangyulu.sunoleducation.operator.AppManager;
import com.yangguangyulu.sunoleducation.util.PhonePackageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2018/1/12.
 * Description:
 * Modified:
 */

@SuppressWarnings("unused")
public class HttpParams extends BaseHttpParams {

    public HttpParams() {
        initBaseParams();
    }

    public HttpParams(boolean useBaseParams) {
        this.useBaseParams = useBaseParams;
        initBaseParams();
    }

    public HttpParams(Map<String, Object> params, boolean useBaseParams) {
        this.useBaseParams = useBaseParams;
        initBaseParams();
//        putRequestParams(params);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            putParam(entry.getKey(), entry.getValue());
        }
    }

    private void initBaseParams() {
        requestParams = new JSONObject();

        if (!useBaseParams) {
            return;
        }
        try {
            requestParams.put("devId", PhonePackageUtils.getPhoneIMEI(AppManager.getAppManager().currentActivity()));
            requestParams.put("devType", "2");  // 1、ios，2、android，3、windows 4、H5
            requestParams.put("appVersion", "v" + PhonePackageUtils.getAPKVersion(AppManager.getAppManager().currentActivity()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
