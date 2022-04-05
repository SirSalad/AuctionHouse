package xyz.oribuin.auctionhouse.auction;

import java.util.Comparator;

public enum AuctionSort {
    NONE("None ", null),
    PRICE_ASCENDING("Price ↑", Comparator.comparingDouble(Auction::getPrice)),
    PRICE_DESCENDING("Price ↓", Comparator.comparingDouble(Auction::getPrice).reversed()),
    TIME_ASCENDING("Time Remaining ↑", Comparator.comparingLong(Auction::getCreatedTime).reversed()),
    TIME_DESCENDING("Time Descending ↓", Comparator.comparingLong(Auction::getCreatedTime));

    private final String displayName;
    private final Comparator<Auction> comparator;

    AuctionSort(String displayName, Comparator<Auction> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Comparator<Auction> getComparator() {
        return comparator;
    }

}
