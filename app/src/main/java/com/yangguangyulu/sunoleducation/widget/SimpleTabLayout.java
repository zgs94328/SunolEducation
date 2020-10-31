package com.yangguangyulu.sunoleducation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;
import com.yangguangyulu.sunoleducation.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinqm on 2018/3/30.
 */

@SuppressWarnings("unused")
public class SimpleTabLayout extends LinearLayout {

    private static final int COLOR_INDICATOR_COLOR = 0xffffbb56;

    private String[] mTitles;
    private int mTabCount = 1;
    private int mIndicatorColor = COLOR_INDICATOR_COLOR;
    private float mTranslationX;
    private Paint mPaint = new Paint();
    private int mBottomLineWidth;
    private int totalWidth;

    private List<TextView> textViews = new ArrayList<>(3);
    private List<ImageView> imageViews = new ArrayList<>(3);

    public SimpleTabLayout(Context context) {
        this(context, null);
    }

    public SimpleTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(mIndicatorColor);
        mPaint.setStrokeWidth(9.0F);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        totalWidth = w;
        mBottomLineWidth = w / mTabCount;
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
        mTabCount = titles.length;
        generateTitleView(0);
    }

    public void setTitles(String[] titles, int currentItem) {
        mTitles = titles;
        mTabCount = titles.length;
        generateTitleView(currentItem);
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        this.mPaint.setColor(mIndicatorColor);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (textViews.size() > 0) {
//            mBottomLineWidth = textViews.get(0).getMeasuredWidth();  //如此可设置mBottomLineWidth与文本等宽
            mBottomLineWidth = DensityUtil.dip2px(getContext(), 25);
            canvas.save();
            canvas.translate(mTranslationX, getHeight() - 2);
//            float startX = (totalWidth / textViews.size() - mBottomLineWidth) / 2;
//            canvas.drawLine(startX, 0, startX + mBottomLineWidth, 0, mPaint);
            canvas.restore();
        }
    }

    public void scroll(int position, float offset) {
        // 0-1:position=0; 1-0:postion=0;
        mTranslationX = getWidth() / mTabCount * (position + offset);
        invalidate();
    }

    public void onPageSelected(int position) {
        for (int i = 0; i < textViews.size(); i++) {
            if (i == position) {
                textViews.get(position).setSelected(true);
                textViews.get(position).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
            } else {
                textViews.get(i).setSelected(false);
                textViews.get(i).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//正常
            }
        }
    }

    private void generateTitleView(int initItem) {
        if (getChildCount() > 0)
            this.removeAllViews();
        int count = mTitles.length;
        setWeightSum(count);
        for (int i = 0; i < count; i++) {
            View itemIndicator = LayoutInflater.from(getContext()).inflate(R.layout.pager_indicator_item_layout, null);
            TextView tv = itemIndicator.findViewById(R.id.indicator_name_tv);
            ImageView imageView = itemIndicator.findViewById(R.id.has_unread_msg_img);
            tv.setGravity(Gravity.CENTER);
//            tv.setSelected(i == initItem);
            tv.setText(mTitles[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            itemIndicator.setTag(i);
            itemIndicator.setOnClickListener((v) -> {
                if (null != mOnItemTabClickListener) {
                    mOnItemTabClickListener.onItemTabClick((Integer) v.getTag());
                }
            });
            textViews.add(tv);
            imageViews.add(imageView);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
//            lp.weight = 1;
            int width = calculateTextWidth(mTitles[1], tv) + tv.getPaddingLeft() + tv.getPaddingEnd() + DensityUtil.dip2px(getContext(), 2);
            LayoutParams lp = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.leftMargin = DensityUtil.dip2px(getContext(), 12);
            itemIndicator.setLayoutParams(lp);
            addView(itemIndicator);
        }
    }

    /***
     * 计算文字宽度
     */
    public int calculateTextWidth(String text, TextView tv) {
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(tv.getTextSize());
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    private OnItemTabClickListener mOnItemTabClickListener;

    public void setOnItemTabClickListener(OnItemTabClickListener onItemTabClickListener) {
        mOnItemTabClickListener = onItemTabClickListener;
    }

    public interface OnItemTabClickListener {
        void onItemTabClick(int position);
    }
}
