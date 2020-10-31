package com.yangguangyulu.sunoleducation.util.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.yangguangyulu.sunoleducation.http.retrofit.HttpManager;
import com.yangguangyulu.sunoleducation.util.DensityUtil;

import java.io.InputStream;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;



/**
 * Copyright: 人人普惠
 * Created by TangJian on 2017/5/22.
 * Description:
 * Modified By:
 */

@SuppressWarnings("unused")
public class GlideUtils {

    /**
     * @param context         上下文
     * @param imgPath         图片路径
     * @param errorResourceId 出错是图片ID
     * @param imageView       控件
     */
    public static void loadImage(Context context, String imgPath, int errorResourceId, ImageView imageView) {
        try {
            // 网络获取图片去
            //1.ALL:缓存原图(SOURCE)和处理图(RESULT)
            //2.NONE:什么都不缓存
            //3.SOURCE:只缓存原图(SOURCE)
            //4.RESULT:只缓存处理图(RESULT) —默认值
            Glide.with(context)
                    .load(imgPath)
                    .crossFade()
                    .centerCrop()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(errorResourceId)
                    .error(errorResourceId)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从网络加载
     */
    public static void loadRoundImage(final Context context, String imageUrl, int loadingResourceId,
                                      final int errorResourceId, final ImageView imageView, int dp) {
        //如果想要圆角，又要centerCorp，则需要如此使用：bitmapTransform(new CenterCrop(mContext), transformation)
        //并且不能调用centerCorp()方法，以及xml里面ImageView不能设置scaleType属性
        final GlideRoundTransform roundTransform = new GlideRoundTransform(context, DensityUtil.dip2px(context, dp));
        Glide.with(context)
                .load(imageUrl)
                .placeholder(loadingResourceId)
                .error(errorResourceId)
                .bitmapTransform(new CenterCrop(context), roundTransform)
//                .centerCrop()
                .crossFade()   //平滑的加载
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Glide.with(context)
                                .load(errorResourceId)
                                .crossFade()
                                .bitmapTransform(new CenterCrop(context), roundTransform)
                                .into(imageView);

                        //奇怪，当Url错误时，这里返回false的话，列表里面复用的Item里面的errorResourceId图片就加载不出来
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

    public static void loadRoundImage(Context context, String imageUrl, int errorResourceId, ImageView imageView, int dp) {
        //如果想要圆角，又要centerCorp，则需要如此使用：bitmapTransform(new CenterCrop(mContext), transformation)
        //并且不能调用centerCorp()方法，以及xml里面ImageView不能设置scaleType属性
        final GlideRoundTransform roundTransform = new GlideRoundTransform(context, DensityUtil.dip2px(context, dp));
        Glide.with(context)
                .load(imageUrl)
                .placeholder(errorResourceId)
                .error(errorResourceId)
                .bitmapTransform(new CenterCrop(context), roundTransform)
                .crossFade()   //平滑的加载
                .into(imageView);
    }

    /**
     * 从本地加载
     */
    public static void loadRoundImage(Context context, int resourceId, ImageView imageView, int dp) {
        //如果想要圆角，又要centerCorp，则需要如此使用：bitmapTransform(new CenterCrop(mContext), transformation)
        //并且不能调用centerCorp()方法，以及xml里面ImageView不能设置scaleType属性
        final GlideRoundTransform roundTransform = new GlideRoundTransform(context, DensityUtil.dip2px(context, dp));
        Glide.with(context)
                .load(resourceId)
                .bitmapTransform(new CenterCrop(context), roundTransform)
//                .centerCrop()
                .crossFade()   //平滑的加载
                .into(imageView);
    }

    public static void loadImage(Context context, String imgPath,
                                 int loadingResourceId, int errorResourceId, ImageView imageView) {
        // 网络获取图片去
        //1.ALL:缓存原图(SOURCE)和处理图(RESULT)
        //2.NONE:什么都不缓存
        //3.SOURCE:只缓存原图(SOURCE)
        //4.RESULT:只缓存处理图(RESULT) —默认值
        Glide.with(context)
                .load(imgPath)
                .crossFade()
                .centerCrop()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(loadingResourceId)
                .error(errorResourceId)
                .into(imageView);
    }

    public static void loadImage(Context context, String imgPath, ImageView imageView) {
        loadImage(context, imgPath, imageView, null);
    }

    /**
     * 加载带cookie的图形验证码
     */
    public static void loadVerifyImage(Context context, String imgUrl, ImageView imageView) {
        //使用okHttp作为图片请求
        Glide.get(context).register(GlideUrl.class, InputStream.class
                , new OkHttpUrlLoader.Factory(HttpManager.getInstance().getOkHttpClient()));
        Glide.with(context)
                .load(imgUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    @SuppressWarnings("unchecked")
    public static void loadImage(Context context, String imgPath, ImageView imageView, RequestListener listener) {
        // 网络获取图片去
        //1.ALL:缓存原图(SOURCE)和处理图(RESULT)
        //2.NONE:什么都不缓存
        //3.SOURCE:只缓存原图(SOURCE)
        //4.RESULT:只缓存处理图(RESULT) —默认值
        Glide.with(context)
                .load(imgPath)
                .crossFade()
                .centerCrop()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(listener)
                .into(imageView);
    }

    public static void loadImage(Context context, String imgPath, Drawable errorDrawable, ImageView imageView) {
        Glide.with(context)
                .load(imgPath)
                .crossFade()
                .centerCrop()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(errorDrawable)
                .into(imageView);
    }

    //加载圆形图片
    public static void loadCircleImage(final Context context, String imageUrl, final ImageView imageView) {
        Glide.with(context).load(imageUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    /***
     * 加载本地图片
     */
    public static void loanLocalImage(Context context, int resourceId, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(resourceId)
                    .crossFade()
                    .centerCrop()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 加载本地GIF图片
     */
    public static void loanLocalImageAsGif(Context context, int resourceId, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(resourceId)
                    .asGif()
                    .crossFade()
                    .centerCrop()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
