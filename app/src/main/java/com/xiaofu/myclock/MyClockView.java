package com.xiaofu.myclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by XiaoFu on 2017-05-10 15:36.
 * 注释：时钟控件
 */

public class MyClockView extends View {

    private Context mContext;
    private int width;//view宽度
    private int height;//view的高度
    private int radius;//外层圆的半径
    private int hcount = 0;//当前小时数
    private int mcount = 0;//当前分钟数
    private int scount = 0;//当前秒钟数
    private int colorHour, colorMinute, colorSecond, colorCalibration;

    private static final int ANGLE_HOUR = 30;//每个时钟刻度之间间隔的角度
    private static final int ANGLE_MINUTE = 6;//每个分钟刻度之间间隔的角度

    //View默认最小宽度
    private static final int DEFAULT_MIN_WIDTH = 200;
    //指针反向超过圆点的长度
    private static final float DEFAULT_POINT_BACK_LENGTH = 20f;

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public MyClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MyClockView(Context context) {
        super(context);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyClockView, defStyleAttr, 0);
        colorHour = typedArray.getColor(R.styleable.MyClockView_clock_hour_color, Color.RED);
        colorMinute = typedArray.getColor(R.styleable.MyClockView_clock_minute_color, Color.GREEN);
        colorSecond = typedArray.getColor(R.styleable.MyClockView_clock_second_color, Color.BLUE);
        colorCalibration = typedArray.getColor(R.styleable.MyClockView_clock_calibration_color, Color.BLACK);
        typedArray.recycle();
    }

    private void getDatas() {
        SimpleDateFormat format = new SimpleDateFormat("HH,mm,ss");
        String time = format.format(new Date());
        try {
            String s[] = time.split(",");
            hcount = Integer.parseInt(s[0]);
            mcount = Integer.parseInt(s[1]);
            scount = Integer.parseInt(s[2]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = DEFAULT_MIN_WIDTH;
        int desiredHeight = DEFAULT_MIN_WIDTH;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    protected void onDraw(Canvas canvas) {

        getDatas();

        width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        width = height = Math.min(width, height);
        radius = width / 2 - 3;

        canvas.translate(getPaddingLeft(), getPaddingTop());

        Paint paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(3);
        paintCircle.setColor(colorCalibration);
        paintCircle.setAntiAlias(true);
        //画出外层的圆盘
        canvas.drawCircle(width / 2, height / 2, radius, paintCircle);


        // 绘制上下午
        paintCircle.setTextSize(24);
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setStrokeWidth(0);
        canvas.drawText(hcount < 12 ? "AM" : "PM", width / 2 - paintCircle.measureText("PM") / 2, height / 2 + 50, paintCircle);

        Paint paintDegree = new Paint();
        paintDegree.setColor(colorCalibration);
        paintDegree.setStyle(Paint.Style.STROKE);
        paintDegree.setStrokeWidth(3);
        paintDegree.setAntiAlias(true);
        //画出12个小时的刻度线
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(width / 2, height / 2 - radius, width / 2, height / 2 - radius + 30, paintDegree);
            canvas.rotate(ANGLE_HOUR, width / 2, height / 2);
        }
        //画出60个分钟的刻度线
        for (int x = 0; x < 60; x++) {
            paintDegree.setStrokeWidth(2);
            if (x % 5 != 0) {
                canvas.drawLine(width / 2, height / 2 - radius, width / 2, height / 2 - radius + 20, paintDegree);
            }
            canvas.rotate(ANGLE_MINUTE, width / 2, height / 2);
        }


        canvas.save();//先保存下，因为下面要用到坐标的平移


        int hourRadius = radius * 5 / 12;//时针长度
        int minuteRaidus = radius * 8 / 12;//分针长度
        int secondRaidus = radius * 10 / 12;//秒针长度

        Paint paintHour = new Paint();
        paintHour.setStyle(Paint.Style.STROKE);
        paintHour.setAntiAlias(true);
        paintHour.setStrokeWidth(7);
        paintHour.setColor(colorHour);

        //将坐标系的平移至原点为（wdith/2,height/2）的地方
        canvas.translate(width / 2, height / 2);
        canvas.save();

        int offset = 30 * mcount / 60;
        offset -= offset % ANGLE_MINUTE;//时针相对分针数，有一个偏移量
        int rotateH = 180 + ANGLE_HOUR * hcount + offset;
        canvas.rotate(rotateH);
        canvas.drawLine(0, -DEFAULT_POINT_BACK_LENGTH, 0, hourRadius, paintHour);//画时针

        canvas.restore();

        Paint paintMinute = new Paint();
        paintMinute.setStrokeWidth(5);
        paintMinute.setColor(colorMinute);
        paintMinute.setStyle(Paint.Style.STROKE);
        paintMinute.setAntiAlias(true);
        int rotateM = 180 + ANGLE_MINUTE * mcount;
        canvas.save();
        canvas.rotate(rotateM);
        canvas.drawLine(0, -DEFAULT_POINT_BACK_LENGTH, 0, minuteRaidus, paintMinute);//画分针

        canvas.restore();

        Paint paintSecond = new Paint();
        paintSecond.setStrokeWidth(3);
        paintSecond.setColor(colorSecond);
        paintSecond.setStyle(Paint.Style.STROKE);
        paintSecond.setAntiAlias(true);
        int rotateS = 180 + ANGLE_MINUTE * scount;
        canvas.save();
        canvas.rotate(rotateS);
        canvas.drawLine(0, -DEFAULT_POINT_BACK_LENGTH, 0, secondRaidus, paintSecond);//画秒针

        canvas.restore();

        //画圆心
        Paint paintCenter = new Paint();
        paintCenter.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, 2, paintCenter);

        postInvalidateDelayed(1000);//延迟一秒执行
    }

}
