package net.zeathus.pmcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class TooltipImageView extends AppCompatImageView {

    private String tooltip;

    public TooltipImageView(Context context, AttributeSet set) {
        super(context, set);
        tooltip = "Test Tooltip";
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                displayTooltip();
            }
        });
    }

    protected void displayTooltip() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

}
