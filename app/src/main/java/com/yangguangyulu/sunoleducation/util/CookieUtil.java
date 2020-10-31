/*
 * 文 件 名:  CookieUtil.java
 * 版    权:  深圳市迪蒙网络科技有限公司
 * 描    述:  <描述>
 * 修 改 人:  jiaohongyun
 * 修改时间:  2014年12月23日
 */
package com.yangguangyulu.sunoleducation.util;

import android.content.Context;

import com.yangguangyulu.sunoleducation.operator.AppManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 管理Cookie
 */
@SuppressWarnings("all")
public class CookieUtil {

    public static String getCookie(Context context) {
        return null == context ? "" : (String) SpUtils
                .get(context, SpUtils.ACCESS_COOKIE, "");
    }

    public static void setCookie(List<String> cookieList, String requestUrl) {
        String cookie = encodeCookie(cookieList);
        Context context = AppManager.getAppManager().currentActivity();
        if (null != context) {
            SpUtils.put(context, SpUtils.ACCESS_COOKIE, cookie);
        }
    }

    public static void setCookie(String accessToken, Context context) {
        if (null != context) {
            SpUtils.put(context, SpUtils.ACCESS_COOKIE, accessToken);
        }
    }

    //整合cookie为唯一字符串
    private static String encodeCookie(List<String> cookies) {
        if (cookies.size() >= 4) {
            return cookies.get(2) + ";" + cookies.get(3);
        }
        StringBuilder sb = new StringBuilder();
        Set<String> set = new HashSet<>();
        for (String cookie : cookies) {
            String[] arr = cookie.split(";");
            for (String s : arr) {
                if (set.contains(s)) continue;
                set.add(s);
            }
        }

        Iterator<String> ite = set.iterator();
        while (ite.hasNext()) {
            String cookie = ite.next();
            sb.append(cookie).append(";");
        }

        int last = sb.lastIndexOf(";");
        if (sb.length() - 1 == last) {
            sb.deleteCharAt(last);
        }

        return sb.toString();
    }
}
