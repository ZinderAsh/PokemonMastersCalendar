package net.zeathus.pmcalendar;

public class Reward {

    private String item;
    private int qty;
    private boolean approx;

    public Reward(String item) {
        this.item = item;
        this.qty = 1;
        this.approx = false;
    }

    public Reward(String item, int qty) {
        this.item = item;
        this.qty = qty;
        this.approx = false;
    }

    public Reward(String item, int qty, boolean approx) {
        this.item = item;
        this.qty = qty;
        this.approx = approx;
    }

    public String getItem() {
        return item;
    }

    public int getQty() {
        return qty;
    }

    public int getIconId() {
        return IconLib.getItemIcon(item);
    }

    public boolean isApproximate() {
        return approx;
    }

}
