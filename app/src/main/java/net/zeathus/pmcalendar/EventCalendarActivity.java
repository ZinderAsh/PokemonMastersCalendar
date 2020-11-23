package net.zeathus.pmcalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

public class EventCalendarActivity extends AppCompatActivity {

    private boolean loaded = false;
    private int mode = -1; // 0 = Ongoing, 1 = Upcoming, 2 = Recent
    private ArrayList<String> filter = null;
    private EventList eventlist;
    private boolean editting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_calendar);
        loadSettings();
        if (BuildConfig.DEBUG) {
            setDebugView();
        }
        new RetrieveTextTask().execute("");
    }

    public void reloadAll() {
        this.recreate();
    }

    protected void onRestart() {
        super.onRestart();
        loadSettings();
        updateDisplay();
    }

    public void setDebugView() {
        ImageButton adminMenu = findViewById(R.id.adminButton);
        adminMenu.setVisibility(View.VISIBLE);

        ListView listView = findViewById(R.id.upcoming_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {
                if (editting) {
                    Event event = ((UpcomingListAdapter) adapter.getAdapter()).getEventAtPosition(position);

                    if (event.getID() < 0) {
                        Toast.makeText(getApplicationContext(), "Cannot edit hard-coded event.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), EditEventActivity.class);
                        intent.putExtra("event", event);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void loadSettings() {
        SharedPreferences mPrefs = getSharedPreferences("calendar",0);
        Set<String> savedFilter = mPrefs.getStringSet("filter", null);
        if (savedFilter != null && savedFilter.size() > 0) {
            filter = new ArrayList<>();
            filter.addAll(savedFilter);
        }
        updateFilterButton();
        //if (!mPrefs.getBoolean("seen_info", false)) {
            //infoDialog();
        //}
    }

    public void infoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome to FEH Calendar!");
        builder.setMessage("This calendar's dates are in Pacific Standard Time. " +
                "If these dates do not match your timezone, click the settings button " +
                "at the bottom right of the app to shift events one day forward or backward.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                SharedPreferences mPrefs = getSharedPreferences("calendar", 0);
                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putBoolean("seen_info", true).apply();
            }
        });
        builder.show();
    }

    public void updateFilterButton() {
        ImageButton buttonFilter = findViewById(R.id.button_filter);
        if (filter == null) {
            buttonFilter.setImageResource(R.drawable.button_filter);
        } else {
            buttonFilter.setImageResource(R.drawable.button_filter_on);
        }
    }

    /*
    public void openOptions(View view) {
        if (!loaded) {
            return;
        }
        String[] options = {"+1", "0", "-1", "Cancel"};

        String currentOption = "Currently: ";
        if (dateOffset == 0) {
            currentOption += "0";
        } else if (dateOffset == 1) {
            currentOption += "+1";
        } else if (dateOffset == -1) {
            currentOption += "-1";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a date offset.\n" +
            currentOption);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i <= 2) {
                    int newOffset = 0;
                    if (i == 0) {
                        newOffset = 1;
                    } else if (i == 1) {
                        newOffset = 0;
                    } else if (i == 2) {
                        newOffset = -1;
                    }
                    SharedPreferences mPrefs = getSharedPreferences("calendar", 0);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putInt("date_offset", newOffset).commit();
                    loadSettings();
                    reloadAll();
                    //Toast.makeText(getApplicationContext(), "Restart the app to see changes.", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }
    */

    public void selectFilter(View view) {
        if (!loaded || mode >= 3) {
            return;
        }

        Dialog dialog;

        final String[] items = eventlist.getEventCategories();
        final boolean[] checkedItems = new boolean[items.length];
        final ArrayList<String> itemsSelected = new ArrayList<>();

        if (filter != null) {
            for (int i = 0; i < items.length; i++) {
                if (filter.contains(items[i])) {
                    checkedItems[i] = true;
                    itemsSelected.add(items[i]);
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Choose what events to display");

        builder.setMultiChoiceItems(items, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                        if (items[selectedItemId].equals("All")) {
                            ListView dialogList = ((AlertDialog) dialog).getListView();
                            if (isSelected) {
                                itemsSelected.clear();
                                for (int i = 1; i < items.length; i++) {
                                    dialogList.setItemChecked(i, true);
                                    itemsSelected.add(items[i]);
                                }
                            } else {
                                itemsSelected.clear();
                                for (int i = 1; i < items.length; i++) {
                                    dialogList.setItemChecked(i, false);
                                }
                            }
                        } else {
                            if (isSelected) {
                                itemsSelected.add(items[selectedItemId]);
                            } else if (itemsSelected.contains(items[selectedItemId])) {
                                itemsSelected.remove(items[selectedItemId]);
                            }
                        }
                    }
                }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ImageView filterView = findViewById(R.id.button_filter);
                        SharedPreferences mPrefs = getSharedPreferences("calendar", 0);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        if (itemsSelected.size() >= items.length - 1 || itemsSelected.size() <= 0) {
                            filter = null;
                            filterView.setImageResource(R.drawable.button_filter);
                            mEditor.putStringSet("filter", null);
                        } else {
                            filter = itemsSelected;
                            filterView.setImageResource(R.drawable.button_filter_on);
                            Set<String> saveFilter = new ArraySet<>();
                            saveFilter.addAll(filter);
                            mEditor.putStringSet("filter", saveFilter);
                        }
                        mEditor.apply();
                        updateDisplay();
                    }
        });

        dialog = builder.create();

        dialog.show();
    }

    public void updateDisplay() {
        if ( mode == 0 ) {
            addListItems(eventlist.getOngoingEvents(Date.getCurrentDate()));
        } else if ( mode == 1 ) {
            addListItems(eventlist.getUpcomingEvents(Date.getCurrentDate()));
        } else if ( mode == 2 ) {
            addListItems(eventlist.getRecentEvents(Date.getCurrentDate()));
        }
    }

    public void selectDisplay(View view) {
        if (!loaded) {
            return;
        }

        String[] options = {"Ongoing", "Upcoming", "Recent"/*, "Gem Calendar"*/};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose what to display");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Toggle filter button
                ImageButton buttonFilter = findViewById(R.id.button_filter);
                if (i >= 3) {
                    buttonFilter.setImageResource(R.drawable.button_filter_off);
                } else {
                    updateFilterButton();
                }
                switch (i) {
                    case 0: {
                        displayOngoing();
                        break;
                    }
                    case 1: {
                        displayUpcoming();
                        break;
                    }
                    case 2: {
                        displayRecent();
                        break;
                    }
                }
            }
        });
        builder.show();
    }
/*
    public void displayOrbs() {
        if (!loaded || mode == 3) {
            return;
        }
        ImageButton btn_display = findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_orbs);
        mode = 3;

        Date date = Date.getCurrentDate();
        ArrayList<Integer> orbCountArray = new ArrayList<>();
        ArrayList<String> dateArray = new ArrayList<>();
        ArrayList<String> eventsArray = new ArrayList<>();

        ArrayList<Event> events = eventlist.getUpcomingAndOngoingEvents(date);

        for (int i = 0; i < 40; i++) {
            int total = 0;
            String eventString = "";
            for (Event event : events) {
                int dayOrbs = event.getGemsAtDate(date);
                if (dayOrbs > 0) {
                    total += dayOrbs;
                    if (eventString.length() > 0) {
                        eventString += "\n";
                    }
                    eventString += event.getDisplayName(true);
                    eventString += ": ";
                    eventString += Integer.toString(dayOrbs);
                }
            }
            if (total > 0) {
                orbCountArray.add(total);
                dateArray.add(getDateString(date));
                eventsArray.add(eventString);
            }
            date.nextDate();
        }

        OrbListAdapter adapter = new OrbListAdapter(this, orbCountArray, dateArray, eventsArray);

        ListView list = (ListView) findViewById(R.id.upcoming_list);
        list.setAdapter(adapter);
    }
*/

    public void displayOngoing() {
        if (!loaded || mode == 0) {
            return;
        }
        ImageButton btn_display = (ImageButton) findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_ongoing);
        mode = 0;
        addListItems(eventlist.getOngoingEvents(Date.getCurrentDate()));
    }

    public void displayUpcoming() {
        if (!loaded || mode == 1) {
            return;
        }
        ImageButton btn_display = (ImageButton) findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_upcoming);
        mode = 1;
        addListItems(eventlist.getUpcomingEvents(Date.getCurrentDate()));
    }

    public void displayRecent() {
        if (!loaded || mode == 2) {
            return;
        }
        ImageButton btn_display = (ImageButton) findViewById(R.id.button_display);
        btn_display.setImageResource(R.drawable.button_recent);
        mode = 2;
        addListItems(eventlist.getRecentEvents(Date.getCurrentDate()));
    }

    public void addListItems(ArrayList<Event> events) {

        if (mode == 0) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getEndDate().compare(rhs.getEndDate());
                }
            });
        } else if (mode == 1) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return lhs.getStartDate().compare(rhs.getStartDate());
                }
            });
        } else if (mode == 2) {
            Collections.sort(events, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {
                    return rhs.getEndDate().compare(lhs.getEndDate());
                }
            });
        }

        if (filter != null) {
            for (int i = events.size() - 1; i >= 0; i--) {
                boolean keep = false;
                for (String s : filter) {
                    if (s.equals("Rewards Gems")) {
                        if (events.get(i).getTotalGems() > 0) {
                            keep = true;
                            break;
                        }
                    } else if (events.get(i).getCategory().equals(s)) {
                        keep = true;
                        break;
                    }
                }
                if (!keep) {
                    events.remove(events.get(i));
                }
            }
        }

        ListView list = findViewById(R.id.upcoming_list);
        TextView loading = findViewById(R.id.loading);

        ArrayList<Event> eventArray = new ArrayList<>();
        ArrayList<String> categoryArray = new ArrayList<>();
        ArrayList<String> nameArray = new ArrayList<>();
        ArrayList<Date> dateArray = new ArrayList<>();
        ArrayList<Reward[]> rewardsArray = new ArrayList<>();
        ArrayList<Boolean> dataminedArray = new ArrayList<>();

        for (Event event : events) {
            eventArray.add(event);
            String eventName = event.getDisplayName(false);
            categoryArray.add(event.getSubcategory());
            nameArray.add(eventName);
            dataminedArray.add(event.isDatamined());

            if (mode == 0) {
                dateArray.add(event.getEndDate());
            } else if (mode == 1) {
                dateArray.add(event.getStartDate());
            } else if (mode == 2) {
                dateArray.add(event.getEndDate());
            }

            rewardsArray.add(event.getRewards());
        }

        UpcomingListAdapter adapter = new UpcomingListAdapter(this, eventArray, categoryArray, nameArray, dateArray, rewardsArray, dataminedArray, mode);

        list.setAdapter(adapter);

        loading.setVisibility(View.GONE);
    }

    public void refresh(View v) {
        reloadAll();
    }

    public void showAdminMenu(View v) {
        String[] options = {editting ? "View Events" : "Edit Events", "Add Event"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ADMIN MENU:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    editting = !editting;
                    Toast.makeText(getApplicationContext(), editting ? "EDITTING EVENTS" : "Viewing Events", Toast.LENGTH_SHORT).show();
                } else if (i == 1) {
                    int nextID = eventlist.getNextEventID();
                    Event event = new Event(nextID, "", "", "", new Date("2019-01-01"));
                    Intent intent = new Intent(getApplicationContext(), EditEventActivity.class);
                    intent.putExtra("event", event);
                    intent.putExtra("adding", true);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }

    private class RetrieveTextTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL textUrl = new URL("http://www.zeathus.net/data/pmcalendar/events.asp");
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

                eventlist = new EventList(getApplicationContext(), false);

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
                    eventlist.addEvent(new Event(id, category, subcategory, name, startDate, endDate, rewards, datamined));
                }

                loaded = true;
                displayOngoing();

            }
        }
    }
}
