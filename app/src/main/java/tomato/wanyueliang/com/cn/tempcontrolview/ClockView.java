package tomato.wanyueliang.com.cn.tempcontrolview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created           :Herve on 2017/2/7.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2017/2/7
 * @ projectName     :TempControlView
 * @ version
 */
public class ClockView extends View {

    // 控件宽
    private int width;
    // 控件高
    private int height;
    // 刻度盘半径
    private int dialRadius;
    // 时针长度
    private float hourHand;
    // 分针长度
    private float minuteHand;
    // 秒针的长度
    private float secondHand;
    // 刻度高
    private int scaleHeight = dp2px(10);
    // 刻度盘画笔
    private Paint dialPaint;
    // 时针画笔
    private Paint hourPaint;
    // 分针画笔
    private Paint minutePaint;
    // 秒针画笔
    private Paint secondPaint;
    // 中心点画笔
    private Paint centerPaint;

    /*时针的位置角度*/
    private float hourRotate;
    /*分针的位置角度*/
    private float minuteRotate;
    /*秒针的位置角度*/
    private float secondRotate;
    private String TAG = getClass().getSimpleName();

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dialPaint = new Paint();
        dialPaint.setAntiAlias(true);
        dialPaint.setStrokeWidth(dp2px(2));
        dialPaint.setStyle(Paint.Style.STROKE);

        hourPaint = new Paint();
        hourPaint.setAntiAlias(true);
        hourPaint.setStrokeWidth(dp2px(4));
        hourPaint.setColor(Color.RED);
        hourPaint.setStyle(Paint.Style.FILL);

        minutePaint = new Paint();
        minutePaint.setAntiAlias(true);
        minutePaint.setStrokeWidth(dp2px(3));
        minutePaint.setColor(Color.GREEN);
        minutePaint.setStyle(Paint.Style.FILL);

        secondPaint = new Paint();
        secondPaint.setAntiAlias(true);
        secondPaint.setStrokeWidth(dp2px(2));
        secondPaint.setColor(Color.YELLOW);
        secondPaint.setStyle(Paint.Style.FILL);

        centerPaint = new Paint();
        centerPaint.setColor(Color.BLUE);
        centerPaint.setAntiAlias(true);
        centerPaint.setStyle(Paint.Style.FILL);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 控件宽、高
        width = height = Math.min(h, w);
        // 刻度盘半径
        dialRadius = width / 2 - width / 10;

        float halfWidth = width / 2;
        // 圆弧半径
        hourHand = dialRadius - halfWidth * 2 / 5;
        minuteHand = dialRadius - halfWidth / 4;
        secondHand = dialRadius;

        start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        drawPointer(canvas);
        drawCenter(canvas);
    }

    /**
     * 绘制刻度盘
     *
     * @param canvas 画布
     */
    private void drawScale(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        // 逆时针旋转135-2度

        dialPaint.setColor(Color.parseColor("#3CB7EA"));
        for (int i = 0; i < 60; i++) {

            float stopY = 0;
            if (i % 5 == 0) {
                stopY = -dialRadius + 2 * scaleHeight;
            } else {
                stopY = -dialRadius + scaleHeight;
            }
            canvas.drawLine(0, -dialRadius, 0, stopY, dialPaint);
            canvas.rotate(6f);
        }

        canvas.restore();
    }

    private void drawCenter(Canvas canvas) {

        canvas.save();
        RectF rectF = new RectF(-10, -10, 10, 10);
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.drawArc(rectF, 0, 360, false, centerPaint);
        canvas.restore();

    }

    private void drawPointer(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);

        canvas.rotate(hourRotate);
        canvas.drawLine(0, -dp2px(6), 0, -hourHand, hourPaint);
        canvas.rotate(minuteRotate - hourRotate);
        canvas.drawLine(0, -dp2px(6), 0, -minuteHand, minutePaint);
        canvas.rotate(secondRotate - minuteRotate);
        canvas.drawLine(0, -dp2px(6), 0, -secondHand, secondPaint);
        canvas.restore();

    }

    private boolean isStart;
    private float lastCurrent;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (!isStart) {
//            isStart = true;
//            start();
//        } else {
//            isStart = false;
//            stop();
//        }

        return super.onTouchEvent(event);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            secondRotate += 6f;
            if (secondRotate >= 360) {
                secondRotate = 0;
                minuteRotate += 6f;
            }
            if (minuteRotate >= 360) {
                lastCurrent += minuteRotate / 12;
                minuteRotate = 0;
            }
            hourRotate = lastCurrent + minuteRotate / 12;
            if (hourRotate >= 360) {
                hourRotate = 0;
                lastCurrent = 0;
            }
            invalidate();
            postDelayed(runnable, 1000);
        }
    };

    private void start() {

        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        secondRotate = (float) second / 60 * 360;

        minuteRotate = (float) minute / 60 * 360;

        hourRotate = (float) hour / 12 * 360 + minuteRotate / 12;

        lastCurrent = hourRotate;

        Log.i(TAG, "start: hour=" + hour);
        Log.i(TAG, "start: minute" + minute);
        Log.i(TAG, "start: second" + second);

        Log.i(TAG, "start: secondRotate" + secondRotate);
        Log.i(TAG, "start: minuteRotate" + minuteRotate);
        Log.i(TAG, "start: hourRotate" + hourRotate);

        postDelayed(runnable, 0);
    }

    private void stop() {
        removeCallbacks(runnable);
        invalidate();

    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
