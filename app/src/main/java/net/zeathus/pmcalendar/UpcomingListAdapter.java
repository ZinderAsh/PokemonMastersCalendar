package net.zeathus.pmcalendar;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Zeathus on 2018-05-20.
 */

public class UpcomingListAdapter extends ArrayAdapter {

    private final Activity context;
    private ArrayList<Event> eventArray;
    private ArrayList<String> categoryArray;
    private ArrayList<String> nameArray;
    private ArrayList<Date> dateArray;
    private ArrayList<Reward[]> rewardsArray;
    private ArrayList<Boolean> dataminedArray;
    private int mode;

    public UpcomingListAdapter(Activity context, ArrayList<Event> eventArray, ArrayList<String> categoryArray, ArrayList<String> nameArray,
                             ArrayList<Date> dateArray, ArrayList<Reward[]> rewardsArray, ArrayList<Boolean> dataminedArray, int mode) {
        super(context, R.layout.upcoming_item, nameArray);

        this.context = context;
        this.eventArray = eventArray;
        this.categoryArray = categoryArray;
        this.nameArray = nameArray;
        this.dateArray = dateArray;
        this.rewardsArray = rewardsArray;
        this.dataminedArray = dataminedArray;
        this.mode = mode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.upcoming_item, container, false);
        }

        convertView.setTag(eventArray.get(position));
        TextView itemName = convertView.findViewById(R.id.name);
        TextView itemDatamined = convertView.findViewById(R.id.datamined);
        ImageView itemIconBG = convertView.findViewById(R.id.iconbg);
        ImageView itemIconBG2 = convertView.findViewById(R.id.iconbg2);
        ImageView itemIconBG3 = convertView.findViewById(R.id.iconbg3);
        ImageView itemIcon = convertView.findViewById(R.id.icon);
        ImageView itemIcon2 = convertView.findViewById(R.id.icon2);
        ImageView itemIcon3 = convertView.findViewById(R.id.icon3);
        TextView itemCount = convertView.findViewById(R.id.count);
        TextView itemCount2 = convertView.findViewById(R.id.count2);
        TextView itemCount3 = convertView.findViewById(R.id.count3);
        CountdownView itemDate = convertView.findViewById(R.id.date);

        itemName.setText(nameArray.get(position));
        itemDatamined.setVisibility(dataminedArray.get(position) ? View.VISIBLE : View.INVISIBLE);

        ImageView[] itemIconBGs = {itemIconBG, itemIconBG2, itemIconBG3};
        ImageView[] itemIcons = {itemIcon, itemIcon2, itemIcon3};
        TextView[] itemCounts = {itemCount, itemCount2, itemCount3};

        for (int i = 0; i < 3; i++) {
            itemIconBGs[i].setVisibility(View.INVISIBLE);
            itemIcons[i].setVisibility(View.INVISIBLE);
            itemCounts[i].setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < rewardsArray.get(position).length; i++) {
            if (rewardsArray.get(position)[i] != null) {
                itemIconBGs[i].setVisibility(View.VISIBLE);
                itemIcons[i].setVisibility(View.VISIBLE);
                itemIcons[i].setImageResource(rewardsArray.get(position)[i].getIconId());
                if (rewardsArray.get(position)[i].getQty() > 1) {
                    itemCounts[i].setVisibility(View.VISIBLE);
                    if (rewardsArray.get(position)[i].isApproximate()) {
                        itemCounts[i].setText("~" + rewardsArray.get(position)[i].getQty() + " ");
                    } else {
                        itemCounts[i].setText("×" + rewardsArray.get(position)[i].getQty() + " ");
                    }
                }
            }
        }

        itemDate.setMode(mode);
        itemDate.setDate(dateArray.get(position));

        TextView itemCategory = convertView.findViewById(R.id.category);
        itemCategory.setText(categoryArray.get(position));

        return convertView;
    }

    public Event getEventAtPosition(int position) {
        if (position >= 0 && position < eventArray.size()) {
            return eventArray.get(position);
        }
        return null;
    }
}