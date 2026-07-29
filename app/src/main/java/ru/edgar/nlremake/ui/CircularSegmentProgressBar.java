package ru.edgar.nlremake.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CircularSegmentProgressBar extends View {

    private static final int SEGMENT_COUNT = 12;
    private static final int UPDATE_DELAY = 100;
    private int currentSegment = 0;

    private Paint paint;
    private Runnable updater;

    public CircularSegmentProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        // Анимация
        updater = new Runnable() {
            @Override
            public void run() {
                currentSegment = (currentSegment + 1) % SEGMENT_COUNT;
                invalidate();
                postDelayed(this, UPDATE_DELAY);
            }
        };
        post(updater);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        float centerX = w / 2f;
        float centerY = h / 2f;

        float innerRadius = Math.min(w, h) / 4.5f;
        float outerRadius = Math.min(w, h) / 2.8f;
        float anglePerSegment = 360f / SEGMENT_COUNT;

        canvas.translate(centerX, centerY);

        for (int i = 0; i < SEGMENT_COUNT; i++) {
            float startAngle = i * anglePerSegment;

            float alphaFactor = (float) (SEGMENT_COUNT - ((currentSegment - i + SEGMENT_COUNT) % SEGMENT_COUNT)) / SEGMENT_COUNT;
            alphaFactor = Math.max(0.05f, alphaFactor);  // минимальная видимость

            int alpha = (int) (alphaFactor * 255);
            paint.setColor(Color.argb(alpha, 255, 255, 255));

            drawTrapezoidSegment(canvas, paint, innerRadius, outerRadius, startAngle, anglePerSegment * 0.8f);
        }
    }

    private void drawTrapezoidSegment(Canvas canvas, Paint paint, float innerR, float outerR, float startAngle, float sweepAngle) {
        Path path = new Path();

        double rad1 = Math.toRadians(startAngle);
        double rad2 = Math.toRadians(startAngle + sweepAngle);

        float x1 = (float) (innerR * Math.cos(rad1));
        float y1 = (float) (innerR * Math.sin(rad1));

        float x2 = (float) (outerR * Math.cos(rad1));
        float y2 = (float) (outerR * Math.sin(rad1));

        float x3 = (float) (outerR * Math.cos(rad2));
        float y3 = (float) (outerR * Math.sin(rad2));

        float x4 = (float) (innerR * Math.cos(rad2));
        float y4 = (float) (innerR * Math.sin(rad2));

        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.close();

        canvas.drawPath(path, paint);
    }
}
