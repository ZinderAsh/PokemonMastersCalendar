package net.zeathus.pmcalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class EditEventActivity extends AppCompatActivity {

    private Event event;
    private boolean adding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = getIntent().getParcelableExtra("event");
        adding = getIntent().getBooleanExtra("adding", false);
        setContentView(R.layout.activity_edit_event);
        loadEventInfo();
    }

    public void loadEventInfo() {
        TextView id = findViewById(R.id.event_id);
        TextView category = findViewById(R.id.event_category);
        TextView subcategory = findViewById(R.id.event_subcategory);
        TextView name = findViewById(R.id.event_name);
        TextView longName = findViewById(R.id.event_long_name);
        TextView startDate = findViewById(R.id.event_start_date);
        TextView endDate = findViewById(R.id.event_end_date);
        CheckBox dataMined = findViewById(R.id.event_datamined);
        TextView reward1Item = findViewById(R.id.event_reward_item_1);
        TextView reward1Qty = findViewById(R.id.event_reward_qty_1);
        CheckBox reward1Approx = findViewById(R.id.event_reward_apr_1);
        TextView reward2Item = findViewById(R.id.event_reward_item_2);
        TextView reward2Qty = findViewById(R.id.event_reward_qty_2);
        CheckBox reward2Approx = findViewById(R.id.event_reward_apr_2);
        TextView reward3Item = findViewById(R.id.event_reward_item_3);
        TextView reward3Qty = findViewById(R.id.event_reward_qty_3);
        CheckBox reward3Approx = findViewById(R.id.event_reward_apr_3);

        id.setText(Integer.toString(event.getID()));
        category.setText(event.getCategory());
        subcategory.setText(event.getSubcategory());
        name.setText(event.getName());
        longName.setText("");
        startDate.setText(event.getStartDate().toString());
        endDate.setText(event.getEndDate().toString());
        dataMined.setChecked(event.isDatamined());
        Reward[] rewards = event.getRewards();
        if (rewards.length > 0 && rewards[0] != null) {
            reward1Item.setText(rewards[0].getItem());
            reward1Qty.setText(Integer.toString(rewards[0].getQty()));
            reward1Approx.setChecked(rewards[0].isApproximate());
        }
        if (rewards.length > 1 && rewards[1] != null) {
            reward2Item.setText(rewards[1].getItem());
            reward2Qty.setText(Integer.toString(rewards[1].getQty()));
            reward2Approx.setChecked(rewards[1].isApproximate());
        }
        if (rewards.length > 2 && rewards[2] != null) {
            reward3Item.setText(rewards[2].getItem());
            reward3Qty.setText(Integer.toString(rewards[2].getQty()));
            reward3Approx.setChecked(rewards[2].isApproximate());
        }
    }

    public void deleteEvent() {
        TextView idView = findViewById(R.id.event_id);
        String id = idView.getText().toString();
        String urlString = "http://www.zeathus.net/data/pmcalendar/deleteevent.asp?pwdskcneoshkdjwj=SWtP863Gj3JES5QhhE5a&";
        urlString += String.format(Locale.US, "ID=%s", id);

        new UpdateDatabaseTask().execute(urlString);
    }

    public void save() {
        TextView idView = findViewById(R.id.event_id);
        TextView categoryView = findViewById(R.id.event_category);
        TextView subcategoryView = findViewById(R.id.event_subcategory);
        TextView nameView = findViewById(R.id.event_name);
        TextView longNameView = findViewById(R.id.event_long_name);
        TextView startDateView = findViewById(R.id.event_start_date);
        TextView endDateView = findViewById(R.id.event_end_date);
        CheckBox dataMinedView = findViewById(R.id.event_datamined);
        TextView reward1ItemView = findViewById(R.id.event_reward_item_1);
        TextView reward1QtyView = findViewById(R.id.event_reward_qty_1);
        CheckBox reward1ApproxView = findViewById(R.id.event_reward_apr_1);
        TextView reward2ItemView = findViewById(R.id.event_reward_item_2);
        TextView reward2QtyView = findViewById(R.id.event_reward_qty_2);
        CheckBox reward2ApproxView = findViewById(R.id.event_reward_apr_2);
        TextView reward3ItemView = findViewById(R.id.event_reward_item_3);
        TextView reward3QtyView = findViewById(R.id.event_reward_qty_3);
        CheckBox reward3ApproxView = findViewById(R.id.event_reward_apr_3);

        String id = idView.getText().toString();
        String category = categoryView.getText().toString();
        String subcategory = subcategoryView.getText().toString();
        String name = nameView.getText().toString();
        String longName = longNameView.getText().toString();
        String startDate = startDateView.getText().toString();
        String endDate = endDateView.getText().toString();
        boolean datamined = dataMinedView.isChecked();
        String reward1Item = reward1ItemView.getText().toString();
        String reward1Qty = reward1QtyView.getText().toString();
        boolean reward1Approx = reward1ApproxView.isChecked();
        String reward2Item = reward2ItemView.getText().toString();
        String reward2Qty = reward2QtyView.getText().toString();
        boolean reward2Approx = reward2ApproxView.isChecked();
        String reward3Item = reward3ItemView.getText().toString();
        String reward3Qty = reward3QtyView.getText().toString();
        boolean reward3Approx = reward3ApproxView.isChecked();

        if (reward1Item.length() <= 0) {
            reward1Item = "NULL";
            reward1Qty = "NULL";
        } else if (reward1Qty.length() <= 0) {
            reward1Qty = "1";
        }
        if (reward2Item.length() <= 0) {
            reward2Item = "NULL";
            reward2Qty = "NULL";
        } else if (reward2Qty.length() <= 0) {
            reward2Qty = "1";
        }
        if (reward3Item.length() <= 0) {
            reward3Item = "NULL";
            reward3Qty = "NULL";
        } else if (reward3Qty.length() <= 0) {
            reward3Qty = "1";
        }

        if (name.equals(longName)) {
            longName = "NULL";
        }

        category = category.replace("&", "%26").replace("'","''");
        subcategory = subcategory.replace("&", "%26").replace("'","''");
        name = name.replace("&", "%26").replace("'","''");
        longName = longName.replace("&", "%26").replace("'","''");
        reward1Item = reward1Item.replace("&", "%26").replace("'","''");
        reward2Item = reward2Item.replace("&", "%26").replace("'","''");
        reward3Item = reward3Item.replace("&", "%26").replace("'","''");

        String[] required = {id, category, subcategory, name, startDate, endDate};
        for (String r : required) {
            if (r.length() <= 0) {
                Toast.makeText(getApplicationContext(), "Missing required field", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String[] sdate = startDate.split("-");
        if (sdate.length < 3 || sdate[0].length() != 4 || sdate[1].length() > 2 || sdate[2].length() > 2) {
            Toast.makeText(getApplicationContext(), "Invalid start date", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] edate = endDate.split("-");
        if (edate.length < 3 || edate[0].length() != 4 || edate[1].length() > 2 || edate[2].length() > 2) {
            Toast.makeText(getApplicationContext(), "Invalid end date", Toast.LENGTH_SHORT).show();
            return;
        }

        String urlString = !adding ?
                "http://www.zeathus.net/data/pmcalendar/updateevent.asp?pwdskcneoshkdjwj=SWtP863Gj3JES5QhhE5a&" :
                "http://www.zeathus.net/data/pmcalendar/addevent.asp?pwdskcneoshkdjwj=SWtP863Gj3JES5QhhE5a&";
        urlString += String.format(Locale.US,
                "ID=%s&Category=%s&Subcategory=%s&Name=%s&LongName=%s&StartDate=%s&EndDate=%s&Datamine=%b&",
                id,
                category,
                subcategory,
                name,
                longName,
                startDate,
                endDate,
                datamined);
        urlString += String.format(Locale.US,
                "RewardItem1=%s&RewardQty1=%s&RewardApprox1=%b&RewardItem2=%s&RewardQty2=%s&RewardApprox2=%b&RewardItem3=%s&RewardQty3=%s&RewardApprox3=%b",
                reward1Item,
                reward1Qty,
                reward1Approx,
                reward2Item,
                reward2Qty,
                reward2Approx,
                reward3Item,
                reward3Qty,
                reward3Approx);

        new UpdateDatabaseTask().execute(urlString);
    }

    public void delete(View v) {
        String[] options = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this Event?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    deleteEvent();
                    finish();
                }
            }
        });
        builder.show();
    }

    public void cancel(View v) {
        finish();
    }

    public void confirm(View v) {
        String[] options = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apply changes?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    save();
                    finish();
                }
            }
        });
        builder.show();
    }

    private class UpdateDatabaseTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

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

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "Successfully saved changes", Toast.LENGTH_SHORT).show();
        }
    }
}
