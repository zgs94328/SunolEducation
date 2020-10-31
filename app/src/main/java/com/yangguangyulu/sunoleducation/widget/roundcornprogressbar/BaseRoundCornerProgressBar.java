package com.yangguangyulu.sunoleducation.widget.roundcornprogressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yangguangyulu.sunoleducation.R;

/***
 * Created by Akexorcist on 5/16/15 AD.
 */
public abstract class BaseRoundCornerProgressBar extends LinearLayout {
    protected final static int DEFAULT_PROGRESS_BAR_HEIGHT = 30;
    protected final static int DEFAULT_BACKGROUND_WIDTH = 0;
    protected final static int DEFAULT_MAX_PROGRESS = 100;
    protected final static int DEFAULT_CURRENT_PROGRESS = 0;
    protected final static int DEFAULT_SECONDARY_PROGRESS = 0;
    protected final static int DEFAULT_PROGRESS_RADIUS = 10;
    protected final static int DEFAULT_PROGRESS_PADDING = 5;
    protected final static int DEFAULT_PROGRESS_COLOR = Color.parseColor("#ff7f7f7f");
    protected final static int DEFAULT_SECONDARY_PROGRESS_COLOR = Color.parseColor("#7f7f7f7f");
    protected final static int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#ff5f5f5f");

    protected LinearLayout layoutBackground;
    protected LinearLayout layoutProgress;
    protected LinearLayout layoutSecondaryProgress;

    protected float max;
    protected float progress;
    protected float secondaryProgress;

    private int backgroundWidth;
    private int backgroundHeight;
    private int radius;
    private int padding;
    private int[] progressColor;
    private int secondaryProgressColor;
    private int backgroundColor;

    private boolean isProgressBarCreated;
    private boolean isProgressSetBeforeDraw;
    private boolean isMaxProgressSetBeforeDraw;
    private boolean isBackgroundColorSetBeforeDraw;
    private boolean isProgressColorSetBeforeDraw;

    private boolean isScreenMeasure;
    private boolean isBackgroundLayoutSet;

    @SuppressLint("NewApi")
    public BaseRoundCornerProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            isProgressBarCreated = false;
            isProgressSetBeforeDraw = false;
            isMaxProgressSetBeforeDraw = false;
            isBackgroundColorSetBeforeDraw = false;
            isProgressColorSetBeforeDraw = false;
            isScreenMeasure = false;
            isBackgroundLayoutSet = false;

            backgroundColor = DEFAULT_BACKGROUND_COLOR;
            backgroundWidth = DEFAULT_BACKGROUND_WIDTH;
            backgroundHeight = (int) dp2px(DEFAULT_PROGRESS_BAR_HEIGHT);
            max = DEFAULT_MAX_PROGRESS;
            progress = DEFAULT_CURRENT_PROGRESS;
            secondaryProgress = DEFAULT_SECONDARY_PROGRESS;
            radius = DEFAULT_PROGRESS_RADIUS;
            padding = DEFAULT_PROGRESS_PADDING;

            progressColor = new int[]{
                    DEFAULT_PROGRESS_COLOR
            };

            secondaryProgressColor = DEFAULT_SECONDARY_PROGRESS_COLOR;
            backgroundColor = DEFAULT_BACKGROUND_COLOR;

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(initProgressBarLayout(), this);
            setup(context, attrs);
        } else {
            setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            setGravity(Gravity.CENTER);
            int padding = (int) dp2px(10);
            setPadding(padding, padding, padding, padding);
            TextView tv = new TextView(context);
            tv.setText(getClass().getSimpleName());
            addView(tv);
        }
    }

    protected void setup(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerProgress);

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, metrics);
        radius = (int) typedArray.getDimension(R.styleable.RoundCornerProgress_rcBackgroundRadius, DEFAULT_PROGRESS_RADIUS);

        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, metrics);
        padding = (int) typedArray.getDimension(R.styleable.RoundCornerProgress_rcBackgroundPadding, DEFAULT_PROGRESS_PADDING);

        layoutBackground = findViewById(R.id.round_corner_progress_background);
        layoutBackground.setPadding(padding, padding, padding, padding);
        if (!isBackgroundColorSetBeforeDraw) {
            setBackgroundLayoutColor(typedArray.getColor(R.styleable.RoundCornerProgress_rcBackgroundColor, DEFAULT_BACKGROUND_COLOR));
        }

        layoutProgress = findViewById(R.id.round_corner_progress_progress);
        layoutSecondaryProgress = findViewById(R.id.round_corner_progress_secondary_progress);
        if (!isProgressColorSetBeforeDraw) {
            int[] progressColorTemp = new int[1];
            progressColorTemp[0] = typedArray.getColor(R.styleable.RoundCornerProgress_rcProgressColor, DEFAULT_PROGRESS_COLOR);
            setProgressColor(progressColorTemp);
            setSecondProgressColor(typedArray.getColor(R.styleable.RoundCornerProgress_rcSecondaryProgressColor, DEFAULT_SECONDARY_PROGRESS_COLOR));
        }

        if (!isMaxProgressSetBeforeDraw) {
            max = typedArray.getFloat(R.styleable.RoundCornerProgress_rcMax, DEFAULT_MAX_PROGRESS);
        }

        if (!isProgressSetBeforeDraw) {
            progress = typedArray.getFloat(R.styleable.RoundCornerProgress_rcProgress, DEFAULT_CURRENT_PROGRESS);
            secondaryProgress = typedArray.getFloat(R.styleable.RoundCornerProgress_rcSecondaryProgress, DEFAULT_SECONDARY_PROGRESS);
        }

        setup(typedArray, metrics);
        typedArray.recycle();
    }

    protected abstract void setBackgroundLayoutSize(LinearLayout layoutBackground);

    protected abstract void setup(TypedArray typedArray, DisplayMetrics metrics);

    protected abstract int initProgressBarLayout();

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void setProgressColor(ViewGroup layout, int... color) {
        GradientDrawable gradient = null;
        if (color.length > 1) {
            gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, color);
            gradient.setColors(color);
        } else if (color.length == 1) {
            gradient = new GradientDrawable();
            gradient.setColor(color[0]);
        } else {
            gradient = new GradientDrawable();
        }
        gradient.setShape(GradientDrawable.RECTANGLE);
        setGradientRadius(gradient);
        layout.setBackground(gradient);
    }

    protected abstract void setGradientRadius(GradientDrawable gradient);

    public void setProgressColor(int... color) {
        progressColor = color;
        setProgressColor(layoutProgress, color);
        if (!isProgressBarCreated) {
            isProgressColorSetBeforeDraw = true;
        }
    }

    /***
     * 设置背景进度颜色
     */
    public void setSecondProgressColor(int secondaryColor) {
        secondaryProgressColor = secondaryColor;
        setProgressColor(layoutSecondaryProgress, secondaryColor);
        if (!isProgressBarCreated) {
            isProgressColorSetBeforeDraw = true;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void setBackgroundLayoutColor(int color) {
        backgroundColor = color;
        GradientDrawable gradient = new GradientDrawable();
        gradient.setShape(GradientDrawable.RECTANGLE);
        gradient.setColor(backgroundColor);
        gradient.setCornerRadius(radius);
        layoutBackground.setBackground(gradient);

        if (!isProgressBarCreated) {
            isBackgroundColorSetBeforeDraw = true;
        }
    }

    protected void setProgress() {
        setProgress(progress);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setBackgroundLayoutSize(layoutBackground);
        onLayoutMeasured();
        isScreenMeasure = true;
        isProgressBarCreated = true;
        setProgress();
        setSecondaryProgress();
    }

    protected abstract void onLayoutMeasured();

    public void setProgress(float progress) {
        progress = (progress > max) ? max : progress;
        progress = (progress < 0) ? 0 : progress;
        this.progress = progress;

        if (isScreenMeasure) {
            if (!isBackgroundLayoutSet) {
                ViewGroup.LayoutParams backgroundParams = layoutBackground.getLayoutParams();
//                backgroundParams.width = backgroundWidth;
                backgroundParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                backgroundParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutBackground.setLayoutParams(backgroundParams);
                isBackgroundLayoutSet = true;
            }
            float ratio = (progress > 0) ? max / progress : 0;
            int progressWidth = (int) setLayoutProgressWidth(ratio);
            ViewGroup.LayoutParams progressParams = layoutProgress.getLayoutParams();
            progressParams.width = progressWidth;
            progressParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutProgress.setLayoutParams(progressParams);
        }

        if (!isProgressBarCreated) {
            isProgressSetBeforeDraw = true;
        }
    }

    protected abstract float setLayoutProgressWidth(float ratio);

    public void setSecondaryProgress(float secondaryProgress) {
        secondaryProgress = (secondaryProgress > max) ? max : secondaryProgress;
        secondaryProgress = (secondaryProgress < 0) ? 0 : secondaryProgress;
        this.secondaryProgress = secondaryProgress;
        if (isScreenMeasure) {
            if (!isBackgroundLayoutSet) {
                ViewGroup.LayoutParams backgroundParams = layoutBackground.getLayoutParams();
                backgroundParams.width = backgroundWidth;
                backgroundParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutBackground.setLayoutParams(backgroundParams);
                isBackgroundLayoutSet = true;
            }

            float ratio = (secondaryProgress > 0) ? max / secondaryProgress : 0;
            int progressWidth = (int) setSecondaryLayoutProgressWidth(ratio);
            ViewGroup.LayoutParams progressParams = layoutSecondaryProgress.getLayoutParams();
            progressParams.width = progressWidth;
            progressParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutSecondaryProgress.setLayoutParams(progressParams);
        }

        if (!isProgressBarCreated) {
            isProgressSetBeforeDraw = true;
        }
    }

    protected abstract float setSecondaryLayoutProgressWidth(float ratio);

    protected void setSecondaryProgress() {
        setSecondaryProgress(secondaryProgress);
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        if (!isProgressBarCreated) {
            isMaxProgressSetBeforeDraw = true;
        }
        this.max = max;
        setProgress(progress);
    }

    public float getProgress() {
        return progress;
    }

    public int getBackgroundWidth() {
        return backgroundWidth;
    }

    /***
     * isGetMeasuredWidth 用这个字段的原因是，发现getMeasuredWidth()的值会有三个
     * 一个是0，一个是N，一个是M;  N 和 M是不相同的，且两者之间会切换两次，最后好像是得到了小的那个。
     * 问题：当设置progress = 100的时候，进度条显示又是正常的；为什么呢？
     */
    public void setBackgroundWidth(int backgroundWidth, boolean isGetMeasuredWidth) {
        if (isGetMeasuredWidth) {
            if (backgroundWidth > this.backgroundWidth) {
                this.backgroundWidth = backgroundWidth;
            }
        } else {
            this.backgroundWidth = backgroundWidth;
        }
    }

    public int getBackgroundHeight() {
        return backgroundHeight;
    }

    public void setBackgroundHeight(int backgroundHeight) {
        this.backgroundHeight = backgroundHeight;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public boolean isProgressBarCreated() {
        return isProgressBarCreated;
    }

    @SuppressLint("NewApi")
    protected float dp2px(float dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateProgressLayout();
    }

    @SuppressLint("NewApi")
    private void invalidateProgressLayout() {
        if (this.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT
                || this.getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            ViewTreeObserver vto = layoutBackground.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layoutBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setProgress();
                    setSecondaryProgress();
                }
            });
            ViewGroup.LayoutParams backgroudParam = layoutBackground.getLayoutParams();
            backgroudParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutBackground.setLayoutParams(backgroudParam);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.backgroundWidth = this.backgroundWidth;
        ss.backgroundHeight = this.backgroundHeight;
        ss.radius = this.radius;
        ss.padding = this.padding;
        ss.progressColor = this.progressColor[0];
        ss.secondaryProgressColor = this.secondaryProgressColor;
        ss.backgroundColor = this.backgroundColor;

        ss.max = this.max;
        ss.progress = this.progress;
        ss.secondaryProgress = this.secondaryProgress;

        ss.isProgressBarCreated = this.isProgressBarCreated;
        ss.isProgressSetBeforeDraw = this.isProgressSetBeforeDraw;
        ss.isMaxProgressSetBeforeDraw = this.isMaxProgressSetBeforeDraw;
        ss.isBackgroundColorSetBeforeDraw = this.isBackgroundColorSetBeforeDraw;
        ss.isProgressColorSetBeforeDraw = this.isProgressColorSetBeforeDraw;
        ss.isScreenMeasure = this.isScreenMeasure;
        ss.isBackgroundLayourSet = this.isBackgroundLayoutSet;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.backgroundWidth = ss.backgroundWidth;
        this.backgroundHeight = ss.backgroundHeight;
        this.radius = ss.radius;
        this.padding = ss.padding;
        this.progressColor[0] = ss.progressColor;
        this.secondaryProgressColor = ss.secondaryProgressColor;
        this.backgroundColor = ss.backgroundColor;

        setProgressColor(progressColor);
        setSecondProgressColor(secondaryProgressColor);

        this.max = ss.max;
        this.progress = ss.progress;
        this.secondaryProgress = ss.secondaryProgress;

        this.isProgressBarCreated = ss.isProgressBarCreated;
        this.isProgressSetBeforeDraw = ss.isProgressSetBeforeDraw;
        this.isMaxProgressSetBeforeDraw = ss.isMaxProgressSetBeforeDraw;
        this.isBackgroundColorSetBeforeDraw = ss.isBackgroundColorSetBeforeDraw;
        this.isProgressColorSetBeforeDraw = ss.isProgressColorSetBeforeDraw;
        this.isScreenMeasure = ss.isScreenMeasure;
        this.isBackgroundLayoutSet = ss.isBackgroundLayourSet;
    }

    private static class SavedState extends BaseSavedState {
        float max;
        float progress;
        float secondaryProgress;

        int backgroundWidth;
        int backgroundHeight;
        int radius;
        int padding;
        int progressColor;
        int secondaryProgressColor;
        int backgroundColor;

        int layoutBackgroundColor;
        int layoutProgress;
        int layoutSecondaryProgress;

        boolean isProgressBarCreated;
        boolean isProgressSetBeforeDraw;
        boolean isMaxProgressSetBeforeDraw;
        boolean isBackgroundColorSetBeforeDraw;
        boolean isProgressColorSetBeforeDraw;

        boolean isScreenMeasure;
        boolean isBackgroundLayourSet;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.max = in.readFloat();
            this.progress = in.readFloat();
            this.secondaryProgress = in.readFloat();

            this.backgroundWidth = in.readInt();
            this.backgroundHeight = in.readInt();
            this.radius = in.readInt();
            this.padding = in.readInt();
            this.progressColor = in.readInt();
            this.secondaryProgressColor = in.readInt();
            this.backgroundColor = in.readInt();

            this.layoutBackgroundColor = in.readInt();
            this.layoutProgress = in.readInt();
            this.layoutSecondaryProgress = in.readInt();

            this.isProgressBarCreated = in.readByte() != 0;
            this.isProgressSetBeforeDraw = in.readByte() != 0;
            this.isMaxProgressSetBeforeDraw = in.readByte() != 0;
            this.isBackgroundColorSetBeforeDraw = in.readByte() != 0;
            this.isProgressColorSetBeforeDraw = in.readByte() != 0;
            this.isScreenMeasure = in.readByte() != 0;
            this.isBackgroundLayourSet = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(this.max);
            out.writeFloat(this.progress);
            out.writeFloat(this.secondaryProgress);

            out.writeInt(this.backgroundWidth);
            out.writeInt(this.backgroundHeight);
            out.writeInt(this.radius);
            out.writeInt(this.padding);
            out.writeInt(this.progressColor);
            out.writeInt(this.secondaryProgressColor);
            out.writeInt(this.backgroundColor);

            out.writeByte((byte) (this.isProgressBarCreated ? 1 : 0));
            out.writeByte((byte) (this.isProgressSetBeforeDraw ? 1 : 0));
            out.writeByte((byte) (this.isMaxProgressSetBeforeDraw ? 1 : 0));
            out.writeByte((byte) (this.isBackgroundColorSetBeforeDraw ? 1 : 0));
            out.writeByte((byte) (this.isProgressColorSetBeforeDraw ? 1 : 0));
            out.writeByte((byte) (this.isScreenMeasure ? 1 : 0));
            out.writeByte((byte) (this.isBackgroundLayourSet ? 1 : 0));
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
