package net.zeathus.pmcalendar;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class EventWeekly extends Event {

    private int interval;

    public EventWeekly(String category, String subcategory, String name, Date startDate, int interval) {
        super(-1, category, subcategory, name, startDate);
        this.interval = interval;
    }

    public EventWeekly(String category, String subcategory, String name, Date startDate, int interval, Reward[] rewards) {
        super(-1, category, subcategory, name, startDate, startDate, rewards);
        if (category.equals("Supercourse")) {
            Date endDate = startDate.clone();
            endDate.setHour(endDate.getHour() + 16);
            if (endDate.getHour() >= 24) {
                endDate.setHour(endDate.getHour() - 24);
                endDate.nextDate();
            }
            setEndDate(endDate);
        } else {
            Date endDate = startDate.clone();
            endDate.nextDate();
            setEndDate(endDate);
        }
        this.interval = interval;
    }

    public boolean isAtDate(Date date) {
        if (getStartDate().compareDays(date) % interval == 0) {
            return true;
        }
        return false;
    }
}
