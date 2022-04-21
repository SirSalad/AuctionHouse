package xyz.oribuin.auctionhouse.auction;

// stonks
public class OfflineProfits {

    private double profit;
    private int sold;

    public OfflineProfits(double profit, int sold) {
        this.profit = profit;
        this.sold = sold;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

}
