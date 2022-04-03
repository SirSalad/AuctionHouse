package xyz.oribuin.auctionhouse.auction;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Represents an auction
 *
 * @author Oribuin
 */
public class Auction {

    private int id;
    private final UUID seller;
    private final ItemStack item;
    private final double price;
    private double soldPrice;
    private UUID buyer;
    private long createdTime;
    private long expiredTime;
    private long soldTime;
    private boolean expired;
    private boolean sold;

    public Auction(int id, UUID seller, ItemStack item, double price) {
        this.id = id;
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.soldPrice = price;
        this.buyer = null;
        this.createdTime = System.currentTimeMillis();
        this.expiredTime = 0L;
        this.soldTime = 0L;
        this.expired = false;
        this.sold = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getSeller() {
        return seller;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public UUID getBuyer() {
        return buyer;
    }

    public void setBuyer(UUID buyer) {
        this.buyer = buyer;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public long getSoldTime() {
        return soldTime;
    }

    public void setSoldTime(long soldTime) {
        this.soldTime = soldTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public double getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(double soldPrice) {
        this.soldPrice = soldPrice;
    }
}
