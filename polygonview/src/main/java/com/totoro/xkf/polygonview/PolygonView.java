package com.totoro.xkf.polygonview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PolygonView extends View {
    private Paint eagePaint;
    private Paint areaPaint;
    private Paint textPaint;

    private int width;
    private int height;
    private float maxRadius;
    private int eageCount;
    private int loopCount;
    private float angle;
    private List<Float> pointValue;
    private List<String> pointName;
    private List<Float> maxPointXList;
    private List<Float> maxPointYList;

    public PolygonView(Context context) {
        super(context);
    }

    public PolygonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public PolygonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void initPaint() {
        eagePaint = new Paint();
        areaPaint = new Paint();
        textPaint = new Paint();

        eagePaint.setStyle(Paint.Style.STROKE);
        eagePaint.setAntiAlias(true);

        areaPaint.setStyle(Paint.Style.FILL);
        areaPaint.setAntiAlias(true);

        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(35);
        textPaint.setAntiAlias(true);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Polygon);
        initPaint();
        setTextColor(typedArray.getColor(R.styleable.Polygon_textColor, Color.BLACK));
        setLoopCount(typedArray.getInteger(R.styleable.Polygon_loopCount, 0));
        setEageCount(typedArray.getInteger(R.styleable.Polygon_eageCount, 0));
        setAreaColor(typedArray.getColor(R.styleable.Polygon_areaColor, Color.BLUE));
        setEageColor(typedArray.getColor(R.styleable.Polygon_eageColor, Color.GRAY));
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        maxRadius = (float) ((width / 2) * 0.8);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!canDraw()) {
            return;
        }
        canvas.translate(width / 2, height / 2);
        computeMaxPoint();
        drawPolygon(canvas);
        drawLine(canvas);
        drawArea(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (pointName == null) {
            return;
        }

        for (int i = 0; i < pointName.size(); i++) {
            float currentAngle = i * angle;
            if (currentAngle == 180) {
                float currentX = maxPointXList.get(i) * 1.1f;
                float currentY = maxPointYList.get(i) * 1.1f;
                canvas.drawText(pointName.get(i), currentX - (textPaint.getTextSize() / 4)
                        * (pointName.get(i).length()), currentY, textPaint);
            } else {
                canvas.save();
                float currentX = maxPointXList.get(0) * 1.1f;
                float currentY = maxPointYList.get(0) * 1.1f;
                canvas.rotate(currentAngle);
                canvas.drawText(pointName.get(i), currentX - (textPaint.getTextSize() / 4)
                        * (pointName.get(i).length()), currentY, textPaint);
                canvas.restore();
            }
        }
    }

    private void drawArea(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < eageCount; i++) {
            float rate = pointValue.get(i);
            float currentX = maxPointXList.get(i) * rate;
            float currentY = maxPointYList.get(i) * rate;
            if (i == 0) {
                path.moveTo(currentX, currentY);
            } else {
                path.lineTo(currentX, currentY);
            }
        }
        path.close();
        canvas.drawPath(path, areaPaint);
    }

    private void drawLine(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < eageCount; i++) {
            path.reset();
            path.lineTo(maxPointXList.get(i), maxPointYList.get(i));
            canvas.drawPath(path, eagePaint);
        }
    }

    public static final String TAG = "Totoro";

    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < loopCount; i++) {
            path.reset();
            float rate = computeRate(i + 1, loopCount);
            for (int j = 0; j < eageCount; j++) {
                float currentX = maxPointXList.get(j) * rate;
                float currentY = maxPointYList.get(j) * rate;
                if (j == 0) {
                    path.moveTo(currentX, currentY);
                } else {
                    path.lineTo(currentX, currentY);
                }
            }
            path.close();
            canvas.drawPath(path, eagePaint);
        }
    }

    private float computeRate(float value, float max) {
        return value / max;
    }


    public void computeMaxPoint() {
        maxPointXList = new ArrayList<>();
        maxPointYList = new ArrayList<>();
        for (int i = 0; i < eageCount; i++) {

            float currentAngle = i * angle - 90;

            float currentX = (float) (maxRadius * Math.cos((currentAngle / 180) * Math.PI));
            float currentY = (float) (maxRadius * Math.sin((currentAngle / 180) * Math.PI));
            maxPointXList.add(currentX);
            maxPointYList.add(currentY);
        }
    }

    public void draw() {
        if (canDraw()) {
            final Float[] trueValues = pointValue.toArray(new Float[pointValue.size()]);
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = animation.getAnimatedFraction();
                    for (int i = 0; i < pointValue.size(); i++) {
                        pointValue.set(i, trueValues[i] * rate);
                    }
                    invalidate();
                }
            });
            valueAnimator.start();
        }
    }

    private boolean canDraw() {
        if (loopCount <= 0 || eageCount <= 2 || pointValue == null
                || pointValue == null || pointValue.size() < eageCount) {
            return false;
        }
        return true;
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
    }


    public void setAreaColor(int color) {
        areaPaint.setColor(color);
        areaPaint.setAlpha(150);
    }

    public void setEageColor(int color) {
        eagePaint.setColor(color);
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public void setEageCount(int eageCount) {
        this.eageCount = eageCount;
        angle = 360 / eageCount;
    }

    public void setPointValue(List<Float> pointValue) {
        this.pointValue = pointValue;
    }

    public void setPointName(List<String> pointName) {
        this.pointName = pointName;
    }

    public int getEageCount() {
        return eageCount;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public List<Float> getPointValue() {
        return pointValue;
    }

    public List<String> getPointName() {
        return pointName;
    }
}
