package com.yangguangyulu.sunoleducation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import com.shuyu.gsyvideoplayer.utils.CommonUtil;

public class MySurfaceView extends SurfaceView {

    private int widthSize;
    private int height; // 圆的半径

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MySurfaceView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);//父类的宽度，
        height = MeasureSpec.getSize(heightMeasureSpec);//父类的高度，
        int screenWidth = CommonUtil.getScreenWidth(getContext());//屏幕的宽度
        int screenHeight = CommonUtil.getScreenHeight(getContext());//屏幕的高度
        height = screenHeight / 2 + 20;//适配HUAWEIP20的高度
        //height=1000;
        //可以理解为红色的背景盖住了大部分的区域，我们只能看到圆框里面的，如果还是按照原来的比例绘制surfaceview
        //需要把手机拿的很远才可以显示出整张脸，故而我用了一个比较取巧的办法就是，按比例缩小，试验了很多数后，感觉0.55
        //是最合适的比例
        double screenWidth1 = 0.7 * screenWidth;//即屏幕的宽度*0.55，绘制的surfaceview的宽度
        double screenHeight1 = 0.7 * screenHeight;//即屏幕的高度*0.55，绘制的surfaceView的高度
        Log.e("onMeasure", "widthSize=" + widthSize);
        Log.e("onMeasure", "draw: widthMeasureSpec = " + screenWidth + "  heightMeasureSpec = " + screenHeight);
        //绘制的输入参数必须是整数型，做浮点型运算后为float型数据，故需要做取整操作
        setMeasuredDimension((int) screenWidth1, (int) screenHeight1);
        //setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    //绘制一个圆形的框，并设置圆框的坐标和半径大小
    public void draw(Canvas canvas) {
        Log.e("onDraw", "draw: test");

//        Paint paint = new Paint();
//        paint.setAntiAlias(true);//消除锯齿   
//        paint.setStyle(Paint.Style.STROKE);//绘制空心圆或 空心矩形   
//
//        int center = getWidth() / 2;
//        int ringWidth = DensityUtil.dip2px(getContext(), 10);//圆环宽度
//第一种方法绘制圆环
//绘制内圆
//        paint.setARGB(255, 138, 43, 226);
//        paint.setStrokeWidth(2);
//        canvas.drawCircle(widthSize / 2, height / 2, widthSize / 2 - ringWidth, paint);
//绘制圆环
//        paint.setARGB(255, 138, 43, 226);
//        paint.setStrokeWidth(ringWidth);
//        canvas.drawCircle(center, center, widthSize / 2 + 1 + ringWidth / 2, paint);
//绘制外圆
//        paint.setARGB(255, 138, 43, 226);
//        paint.setStrokeWidth(2);
//        canvas.drawCircle(center, center, widthSize / 2, paint);


        Path path = new Path();
        //path.addCircle(widthSize / 2, height / 2, height / 2, Path.Direction.CCW);
        path.addCircle(widthSize / 2, height / 2, widthSize / 2, Path.Direction.CCW);
        if(Build.VERSION.SDK_INT >= 26){
            canvas.clipPath(path);
        }else {
            canvas.clipPath(path, Region.Op.REPLACE);
        }
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("onDraw", "onDraw");
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        int screenWidth = CommonUtil.getScreenWidth(getContext());
        int screenHeight = CommonUtil.getScreenHeight(getContext());
        Log.d("screenWidth", Integer.toString(screenWidth));
        Log.d("screenHeight", Integer.toString(screenHeight));
        w = screenWidth;
        h = screenHeight;
        super.onSizeChanged(w, h, oldw, oldh);

    }
}
