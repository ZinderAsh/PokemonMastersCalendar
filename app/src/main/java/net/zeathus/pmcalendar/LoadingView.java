package net.zeathus.pmcalendar;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class LoadingView extends AppCompatTextView {

    private final String PROPERTY_FLOAT = "property_float";

    private Paint paint;
    private PropertyValuesHolder propertyFloat = PropertyValuesHolder.ofFloat(PROPERTY_FLOAT, 0.0F, (float) Math.PI);
    private float degree = 0.0F;

    public LoadingView(Context context, AttributeSet set) {
        super(context, set);
        paint = new Paint();
        ValueAnimator animator = new ValueAnimator();
        animator.setValues(propertyFloat);
        animator.setDuration(5000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (float) animation.getAnimatedValue(PROPERTY_FLOAT);
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        float cx = getWidth() / 2.0F;
        float cy = getHeight() / 2.0F;
        float radius = getWidth() / 6.0F;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(12);
        paint.setColor(Color.argb(255,200,200,250));
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setColor(Color.argb(255,100,100,250));
        if (degree * 2 > Math.PI) {
            float angle = (float) Math.sin(degree - Math.PI);
            canvas.drawArc(
                    cx - radius,
                    cy - radius,
                    cx + radius,
                    cy + radius,
                    -90 + 360.0F * angle, 360.0F * angle,
                    false,
                    paint);
        } else {
            float angle = (float) Math.sin(degree);
            canvas.drawArc(
                    cx - radius,
                    cy - radius,
                    cx + radius,
                    cy + radius,
                    -90 + 360.0F * angle, 360.0F * angle,
                    false,
                    paint);
        }

        super.onDraw(canvas);
        /*
        int textColor = getTextColors().getDefaultColor();
        setTextColor(Color.parseColor("#33778C"));
        getPaint().setStrokeWidth(4);
        getPaint().setStyle(Paint.Style.STROKE);
        super.onDraw(canvas);
        setTextColor(textColor);
        getPaint().setStrokeWidth(0);
        getPaint().setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
        */
    }
}
