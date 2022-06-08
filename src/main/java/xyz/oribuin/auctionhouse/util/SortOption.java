package xyz.oribuin.auctionhouse.util;

import xyz.oribuin.auctionhouse.auction.AuctionSort;

import java.util.Arrays;
import java.util.Iterator;

public class SortOption {

    private AuctionSort sort;
    private Iterator<AuctionSort> iterator;

    public SortOption() {
        this.iterator = Arrays.stream(AuctionSort.values()).iterator();
        this.sort = iterator.next();
    }

    public SortOption(AuctionSort sort) {
        Iterator<AuctionSort> iterator = Arrays.stream(AuctionSort.values()).iterator();
        this.sort = sort;

        // set the iterator to the current sort
        while (iterator.hasNext()) {
            AuctionSort next = iterator.next();
            if (next == sort) {
                this.iterator = iterator;
                break;
            }
        }
    }

    public AuctionSort getSort() {
        return sort;
    }

    public void setSort(AuctionSort sort) {
        this.sort = sort;
    }

    public Iterator<AuctionSort> getIterator() {
        return iterator;
    }

    public void setIterator(Iterator<AuctionSort> iterator) {
        this.iterator = iterator;
    }

}

