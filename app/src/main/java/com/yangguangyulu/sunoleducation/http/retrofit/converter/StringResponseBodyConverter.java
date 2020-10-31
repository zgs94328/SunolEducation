package com.yangguangyulu.sunoleducation.http.retrofit.converter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/4/1.
 * Description:
 * Modified By:
 */

public class StringResponseBodyConverter implements Converter<ResponseBody, String> {
    @Override
    public String convert(ResponseBody value) throws IOException {
        try {
            return value.string();
        } finally {
            value.close();
        }
    }
}
