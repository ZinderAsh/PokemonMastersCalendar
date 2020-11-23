package net.zeathus.pmcalendar;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.widget.AppCompatTextView;

public class CountdownView extends AppCompatTextView {

    private Date date;
    private Timer timer;
    private int mode;

    public CountdownView(Context context, AttributeSet set) {
        super(context, set);
        timer = new Timer();
        mode = 0;
        date = new Date(1, 1, 2019);
        timer.scheduleAtFixedRate(new UpdateViewTask(this), 0, 1000);
    }

    public void setDate(Date date) {
        this.date = date;
        updateText();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private void updateText() {
        long time = date.compare(Date.getCurrentDate());
        boolean hasHappened = (time <= 0);

        int days = (time >= 0) ? (int) Math.floor((time * 1.0F) / 24.0F) : (int) Math.ceil((time * 1.0F) / 24.0F);
        int hours = (int) (time - (days * 24));
        int minutes = Calendar.getInstance(TimeZone.getTimeZone("GMT-6")).get(Calendar.MINUTE);
        if (!hasHappened) {
            hours--;
            minutes = 60 - minutes;
        }

        String clock = (days != 0) ?
                String.format(Locale.US, "%dD & %dh", Math.abs(days), Math.abs(hours)) :
                (hours != 0) ?
                String.format(Locale.US, "%dh & %dm", Math.abs(hours), minutes) :
                        String.format(Locale.US, "%dm", minutes);

        if (mode == 1) {
            if (hasHappened) {
                setText(String.format("Started %s ago", clock));
            } else {
                setText(String.format("Starts in %s", clock));
            }
        } else if (mode == 0 || mode == 2) {
            if (hasHappened) {
                setText(String.format("Ended %s ago", clock));
            } else {
                setText(String.format("Ends in %s", clock));
            }
        }
    }

    private class UpdateViewTask extends TimerTask {

        private CountdownView view;

        public UpdateViewTask(CountdownView view) {
            this.view = view;
        }

        @Override
        public void run() {
            view.post(new Runnable() {
                @Override
                public void run() {
                    updateText();
                }
            });
        }

    }

}
