package net.zeathus.pmcalendar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class EventList {

    private ArrayList<Event> events;
    private ArrayList<Event> weekly;
    private final Context context;
    private boolean widget;

    public EventList(Context context, boolean widget) {
        this.context = context;
        this.widget = widget;
        addWeeklyEvents();
        if (widget) {
            getEventsFromWeb();
        } else {
            this.events = new ArrayList<>();
        }
    }

    public int length() {
        return events.size();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public int getNextEventID() {
        int nextID = 0;
        for (Event e : events) {
            if (e.getID() >= nextID) {
                nextID = e.getID() + 1;
            }
        }
        return nextID;
    }

    public ArrayList<Event> getEventsAtDate(Date date) {
        ArrayList<Event> output = new ArrayList<Event>();
        for (Event event : events) {
            if (event.isAtDate(date)) {
                output.add(event);
            }
        }
        for (Event event : weekly) {
            if (event.isAtDate(date)) {
                output.add(event);
            }
        }
        return output;
    }

    public ArrayList<Event> getUpcomingAndOngoingEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getStartDate().compare(date) >= 0) {
                output.add(event);
            } else if (event.getStartDate().compare(date) <= 0 && event.getEndDate().compare(date) >= 0) {
                output.add(event);
            }
        }
        Date iDate = date.clone();
        for (int i = 0; i < 31; i++) {
            for (Event event : weekly) {
                if (event.getTotalGems() > 0) {
                    if (event.isAtDate(iDate)) {
                        Event newEvent = new Event(-1, event.getCategory(), event.getSubcategory(), event.getName(), iDate.clone(), iDate.clone(), event.getRewards());
                        output.add(newEvent);
                    }
                }
            }
            iDate.nextDate();
        }
        return output;
    }

    public ArrayList<Event> getUpcomingEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getStartDate().compare(date) >= 0) {
                output.add(event);
            }
        }
        Date iDate = date.clone();
        for (int i = 0; i <= 7; i++) {
            for (Event event : weekly) {
                if (event.isAtDate(iDate)) {
                    Date eDate = iDate.clone();
                    eDate.setHour(event.getStartDate().getHour());
                    Event newEvent = new EventWeekly(event.getCategory(), event.getSubcategory(), event.getName(), eDate, -1, event.getRewards());
                    if (newEvent.getStartDate().compare(date) >= 0) {
                        output.add(newEvent);
                    }
                }
            }
            iDate.nextDate();
        }
        return output;
    }

    public ArrayList<Event> getOngoingEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getStartDate().compare(date) <= 0 && event.getEndDate().compare(date) >= 0) {
                output.add(event);
            }
        }
        Date iDate = date.clone();
        iDate.prevDate();
        for (int i = 0; i <= 2; i++) {
            for (Event event : weekly) {
                if (event.isAtDate(iDate)) {
                    Date eDate = iDate.clone();
                    eDate.setHour(event.getStartDate().getHour());
                    Event newEvent = new EventWeekly(event.getCategory(), event.getSubcategory(), event.getName(), eDate, -1, event.getRewards());
                    if (newEvent.getStartDate().compare(date) <= 0 && newEvent.getEndDate().compare(date) >= 0) {
                        output.add(newEvent);
                    }
                }
            }
            iDate.nextDate();
        }
        return output;
    }

    public ArrayList<Event> getRecentEvents(Date date) {
        ArrayList<Event> output = new ArrayList<>();
        for (Event event : events) {
            if (event.getEndDate().compare(date) <= 0) {
                output.add(event);
            }
        }
        return output;
    }

    public String[] getEventCategories() {

        ArrayList<String> categories = new ArrayList<>();

        categories.add("All");

        for (Event e : events) {
            if (!categories.contains(e.getCategory())) {
                categories.add(e.getCategory());
            }
        }

        for (Event e : weekly) {
            if (!categories.contains(e.getCategory())) {
                categories.add(e.getCategory());
            }
        }

        categories.add("Rewards Gems");

        String[] ret = new String[categories.size()];

        return categories.toArray(ret);

    }

    public void getEventsFromWeb() {
        this.events = new ArrayList<>();
        new RetrieveTextTask().execute("");
    }

    public void addWeeklyEvents() {
        this.weekly = new ArrayList<>();

        /*** OTHER ***/
        //Reward[] rewardsDaily = {new Reward("Gems", 30)};
        //weekly.add(new EventWeekly("Daily", "Missions", "Daily Missions", new Date(0, 1, 9, 2019), 1, rewardsDaily));


        /*** SUPERCOURSES ***/
        if (!widget) {
            Reward[] rewardsSuperStrike = {new Reward("Buff Blend"), new Reward("Skill Capsule"), new Reward("Gym Leader Notes")};
            Reward[] rewardsSuperSupport = {new Reward("Aid Ade"), new Reward("Skill Capsule"), new Reward("Gym Leader Notes")};
            Reward[] rewardsSuperTech = {new Reward("Tech Tonic"), new Reward("Skill Capsule"), new Reward("Gym Leader Notes")};
            Reward[] rewardsSuperLevel = {new Reward("Level-Up Manual")};
            Reward[] rewardsSuperCoin = {new Reward("Coins"), new Reward("Pearl")};
            // Strike
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Strike Supercourse: Will", new Date(8, 2, 9, 2019), 7, rewardsSuperStrike));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Strike Supercourse: Gardenia", new Date(0, 4, 9, 2019), 7, rewardsSuperStrike));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Strike Supercourse: Nanu", new Date(16, 5, 9, 2019), 7, rewardsSuperStrike));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Strike Supercourse: Drake", new Date(8, 7, 9, 2019), 7, rewardsSuperStrike));
            // Support
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Support Supercourse: Brendan", new Date(8, 1, 9, 2019), 7, rewardsSuperSupport));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Support Supercourse: Lorelei", new Date(0, 3, 9, 2019), 7, rewardsSuperSupport));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Support Supercourse: Kahili", new Date(16, 4, 9, 2019), 7, rewardsSuperSupport));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Support Supercourse: Brendan", new Date(8, 6, 9, 2019), 7, rewardsSuperSupport));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Support Supercourse: Mina", new Date(8, 7, 9, 2019), 7, rewardsSuperSupport));
            // Tech
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Tech Supercourse: Phoebe", new Date(8, 1, 9, 2019), 7, rewardsSuperTech));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Tech Supercourse: Shauntal", new Date(16, 2, 9, 2019), 7, rewardsSuperTech));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Tech Supercourse: Karen", new Date(8, 4, 9, 2019), 7, rewardsSuperTech));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Tech Supercourse: Phoebe", new Date(0, 6, 9, 2019), 7, rewardsSuperTech));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Tech Supercourse: Tate", new Date(8, 7, 9, 2019), 7, rewardsSuperTech));
            // Level-Up
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Level-Up Supercourse: Grant", new Date(16, 1, 9, 2019), 7, rewardsSuperLevel));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Level-Up Supercourse: Flint", new Date(8, 3, 9, 2019), 7, rewardsSuperLevel));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Level-Up Supercourse: Clair", new Date(0, 5, 9, 2019), 7, rewardsSuperLevel));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Level-Up Supercourse: Grant", new Date(16, 6, 9, 2019), 7, rewardsSuperLevel));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Level-Up Supercourse: Marshal", new Date(8, 7, 9, 2019), 7, rewardsSuperLevel));
            // Coin
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Coin Supercourse: Roxie", new Date(0, 2, 9, 2019), 7, rewardsSuperCoin));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Coin Supercourse: Thorton", new Date(16, 3, 9, 2019), 7, rewardsSuperCoin));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Coin Supercourse: Sophocles", new Date(8, 5, 9, 2019), 7, rewardsSuperCoin));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Coin Supercourse: Roxie", new Date(0, 7, 9, 2019), 7, rewardsSuperCoin));
            weekly.add(new EventWeekly("Supercourse", "Supercourse", "Coin Supercourse: Liza", new Date(8, 7, 9, 2019), 7, rewardsSuperCoin));
        }

    }

    private class RetrieveTextTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL textUrl = new URL("http://www.zeathus.net/data/pmcalendar/events.asp?short=1");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(textUrl.openStream(), StandardCharsets.UTF_8));
                String stringBuffer;
                String stringText = "";
                while ((stringBuffer = bufferedReader.readLine()) != null) {
                    stringText += stringBuffer;
                }
                bufferedReader.close();
                return stringText;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String string) {
            if (string != null) {
                String[] lines = string.split("<br>");

                for (String line : lines) {
                    String[] fields = line.split(";");
                    int id = Integer.parseInt(fields[0]);
                    String category = fields[1];
                    String subcategory = fields[2];
                    String name = fields[3];
                    Date startDate = new Date(fields[4]);
                    Date endDate = new Date(fields[5]);
                    boolean datamined = Boolean.parseBoolean(fields[6]);
                    Reward[] rewards = new Reward[3];
                    if (fields.length > 7 && fields[7].length() > 1) {
                        rewards[0] = new Reward(fields[7],
                                fields.length > 8 ? Integer.parseInt(fields[8]) : 1,
                                fields.length > 9 && Boolean.parseBoolean(fields[9]));
                    }
                    if (fields.length > 10 && fields[10].length() > 1) {
                        rewards[1] = new Reward(fields[10],
                                fields.length > 11 ? Integer.parseInt(fields[11]) : 1,
                                fields.length > 12 && Boolean.parseBoolean(fields[12]));
                    }
                    if (fields.length > 13 && fields[13].length() > 1) {
                        rewards[2] = new Reward(fields[13],
                                fields.length > 14 ? Integer.parseInt(fields[14]) : 1,
                                fields.length > 15 && Boolean.parseBoolean(fields[15]));
                    }
                    events.add(new Event(id, category, subcategory, name, startDate, endDate, rewards, datamined));
                }

                Intent intent = new Intent(context, FEHCalendarWidgetProvider.class);
                intent.setAction(FEHCalendarWidgetProvider.UPDATE_BROADCAST);
                context.sendBroadcast(intent);
            }
        }

    }

}
