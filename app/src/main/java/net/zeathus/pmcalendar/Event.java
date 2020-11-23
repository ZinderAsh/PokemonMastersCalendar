package net.zeathus.pmcalendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zeathus on 2018-05-20.
 */
public class Event implements Parcelable {

    private int id;
    private String category;
    private String subcategory;
    private String name;
    private Date startDate;
    private Date endDate;
    private Reward[] rewards;
    private boolean datamined;

    public Event(int id, String category, String subcategory, String name) {
        this.id = id;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.startDate = null;
        this.endDate = null;
        this.rewards = new Reward[3];
        this.datamined = false;
    }

    public Event(int id, String category, String subcategory, String name, Date date) {
        this.id = id;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.startDate = date;
        this.endDate = date;
        this.rewards = new Reward[3];
        this.datamined = false;
    }

    public Event(int id, String category, String subcategory, String name, Date startDate, Date endDate) {
        this.id = id;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rewards = new Reward[3];
        this.datamined = false;
    }

    public Event(int id, String category, String subcategory, String name, Date startDate, Date endDate, Reward[] rewards) {
        this.id = id;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rewards = rewards;
        this.datamined = false;
    }

    public Event(int id, String category, String subcategory, String name, Date startDate, Date endDate, Reward[] rewards, boolean datamined) {
        this.id = id;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rewards = rewards;
        this.datamined = datamined;
    }

    public int getID() {
        return id;
    }

    public Reward[] getRewards() { return rewards; }

    public int getTotalGems() {

        int total = 0;

        for (Reward r : rewards) {
            if (r != null) {
                if (r.getItem().equals("Gem") || r.getItem().equals("Gems")) {
                    total += r.getQty();
                }
            }
        }

        return total;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName(boolean widget) {
        if (!name.equals("NULL")) {
            if (!widget) {
                return name;
            } else {
                return getShortCategory() + " " + name;
            }
        } else {
            return category;
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date date) { this.endDate = date; }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
    public String getSubcategory() {
        return subcategory;
    }

    public boolean isDatamined() {return datamined;}
    public void setDatamined() {datamined = true;}

    public String getShortCategory() {

        switch (category) {
            case "Missions": {
                return "";
            }
            case "Story Event": {
                return "Story Event\n";
            }
            case "Spotlight Scout": {
                return "Spotlight Scout\n";
            }
        }

        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAtDate(Date date) {
        if (this.startDate.compareDays(date) == 0) {
            return true;
        }
        return false;
    }

    public Reward getPrimaryReward() {
        if (rewards[0] == null) {
            return new Reward("None");
        } else {
            return rewards[0];
        }
    }

    public void update(Date date) {}

    // 99.9% of the time you can just ignore this
    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(category);
        out.writeString(subcategory);
        out.writeString(name);
        out.writeString(startDate.toString());
        out.writeString(endDate.toString());
        out.writeInt(datamined ? 1 : 0);

        int rewardCount = 0;
        for (int i = 0; i < rewards.length; i++) {
            if (rewards[i] != null) {
                rewardCount++;
            }
        }

        out.writeInt(rewardCount);

        for (int i = 0; i < rewards.length; i++) {
            if (rewards[i] != null) {
                out.writeString(rewards[i].getItem());
                out.writeInt(rewards[i].getQty());
                out.writeInt(rewards[i].isApproximate() ? 1 : 0);
            }
        }
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    private Event(Parcel in) {
        this.id = in.readInt();
        this.category = in.readString();
        this.subcategory = in.readString();
        this.name = in.readString();
        this.startDate = new Date(in.readString());
        this.endDate = new Date(in.readString());
        this.datamined = (in.readInt() == 1);

        int rewardCount = in.readInt();

        this.rewards = new Reward[3];
        for (int i = 0; i < rewardCount; i++) {
            Reward r = new Reward(
                    in.readString(),
                    in.readInt(),
                    (in.readInt() == 1));
            rewards[i] = r;
        }
    }
}
