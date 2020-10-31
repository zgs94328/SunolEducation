package com.yangguangyulu.sunoleducation.ui.adapter;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

/**
 * Copyright: 瑶咪科技
 * Created by TangJian on 2018/8/11.
 * Description:
 * Modified:
 */
@SuppressWarnings("unused")
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private FragmentActivity mContext;
    private List<Fragment> listFragments;
    private List<String> mTitleList = new ArrayList<>(); //页卡标题集合

    public SimpleFragmentPagerAdapter(FragmentActivity context, List<Fragment> al, List<String> titleList) {
        super(context.getSupportFragmentManager());
        listFragments = al;
        mTitleList = titleList;
    }

    public SimpleFragmentPagerAdapter(FragmentActivity context, List<Fragment> fragments) {
        super(context.getSupportFragmentManager());
        mContext = context;
        listFragments = fragments;
    }

    public SimpleFragmentPagerAdapter(FragmentActivity context) {
        super(context.getSupportFragmentManager());
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = listFragments.get(position);

        if (!fragment.isAdded()) { // 如果fragment还没有added
            FragmentManager fragmentManager = mContext.getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(fragment, fragment.getClass().getSimpleName());
            ft.commit();
            /***
             * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
             * 会在进程的主线程中，用异步的方式来执行。
             * 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
             * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
             */
            fragmentManager.executePendingTransactions();
        }

        if (null != fragment.getView() && null == fragment.getView().getParent()) {
            container.addView(fragment.getView()); // 为viewpager增加布局
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return listFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (null != mTitleList && mTitleList.size() > position) ? mTitleList.get(position) : ""; //页卡标题
    }
}
