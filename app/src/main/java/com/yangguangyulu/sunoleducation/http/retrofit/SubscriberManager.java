package com.yangguangyulu.sunoleducation.http.retrofit;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by TANGJIAN on 2017/2/3.
 * Description:
 * Modified by TANGJIAN on 2017/2/3.
 */
public class SubscriberManager {
    private final Map<Object, CompositeSubscription> SubscriptionMap = new HashMap<>();
    private static SubscriberManager instance = new SubscriberManager();

    private SubscriberManager() {
    }

    /**
     * 单一实例
     */
    public static SubscriberManager getInstance() {
        return instance;
    }

    /**
     * 把发布者加入生命周期控制，当activity销毁的时候，会自动停止它的发布。防止内存溢出异常
     */
    public void addSubscription(Object object, Subscription s) {
        CompositeSubscription subscriptionList = SubscriptionMap.get(object);
        if (subscriptionList == null) {
            subscriptionList = new CompositeSubscription();
            SubscriptionMap.put(object, subscriptionList);
        }
        subscriptionList.add(s);
    }

    /**
     * 清空发布者list，列表中发布者会全部停止
     */
    public void removeSubscription(Object object) {
        CompositeSubscription subscriptionList = SubscriptionMap.get(object);
        if (subscriptionList != null) {
            subscriptionList.unsubscribe();
            subscriptionList.clear();
        }
        SubscriptionMap.remove(object);
    }

    /**
     * 完全退出应用程序
     */
    public void AppExit() {
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
