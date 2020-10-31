package com.yangguangyulu.sunoleducation.http.retrofit;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.yangguangyulu.sunoleducation.base.Constants;
import com.yangguangyulu.sunoleducation.http.HttpApi;
import com.yangguangyulu.sunoleducation.http.retrofit.converter.StringConverterFactory;
import com.yangguangyulu.sunoleducation.http.retrofit.cookie.CookieManger;
import com.yangguangyulu.sunoleducation.operator.AppManager;
import com.yangguangyulu.sunoleducation.util.CookieUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by TANGJIAN on 2017/2/3.
 * Description:
 * Modified by TANGJIAN on 2017/2/3.
 */

@SuppressWarnings("unused")
public class HttpManager {
    private static final int DEFAULT_TIMEOUT = 10;

    private static volatile HttpManager httpManager = null;
    private static Retrofit apiRetrofit;
    private static String currentRequestUrl = ""; //预留字段，可能会用到
    private OkHttpClient okHttpClient;

    private HttpManager() {
        initRetrofit();
    }

    public static HttpManager getInstance() {
        Context context = AppManager.getAppManager().currentActivity();
        if (httpManager == null) {
            synchronized (HttpManager.class) {
                if (httpManager == null) {
                    httpManager = new HttpManager();
                }
            }
        }
        return httpManager;
    }

    public OkHttpClient getOkHttpClient() {
        if (null == okHttpClient) {
            okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(mHeaderInterceptor)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//                    .cookieJar(new CookieManger(AppManager.getAppManager().currentActivity()))
                    .build();
        }
        return okHttpClient;
    }

    private void initRetrofit() {
        apiRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_API_URL)
                .client(getOkHttpClient())
                .addConverterFactory(StringConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public HttpApi createApiService() {
        return apiRetrofit.create(HttpApi.class);
    }

    public <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())//事件在IO线程执行
                .unsubscribeOn(Schedulers.io())//解绑在IO线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(s);
    }

    public <T> void toSubscribe(Context context, Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())//事件在IO线程执行
                .unsubscribeOn(Schedulers.io())//解绑在IO线程
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .subscribe(s);
        SubscriberManager.getInstance().addSubscription(context, s);
    }

    private Interceptor mHeaderInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Activity activity = AppManager.getAppManager().currentActivity();
            if (null != activity) {
                String accessToken = CookieUtil.getCookie(activity);
                if (!TextUtils.isEmpty(accessToken)) {
                    request = request.newBuilder()
//                            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                            .addHeader("Content-Type", "application/json;charset=UTF-8")
//                            .addHeader("Accept-Encoding", "gzip, deflate")
//                            .addHeader("Connection", "keep-alive")
//                            .addHeader("Accept", "*/*")
                            .addHeader("accessToken", accessToken)
                            .addHeader("Cookie", accessToken)
                            .build();
                }
            }

            return chain.proceed(request);
        }
    };

    public static <T> Subscriber getSubscriber(final RequestResponse<T> requestResponse) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (null != requestResponse) {
                    requestResponse.onFailure(e);
                }
            }

            @Override
            public void onNext(T result) {
                if (null != requestResponse) {
                    requestResponse.onRequestResult(currentRequestUrl, result);
                }
            }
        };
    }
}