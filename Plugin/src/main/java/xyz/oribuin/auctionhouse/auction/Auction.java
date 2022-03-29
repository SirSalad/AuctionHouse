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
    private UUID buyer;
    private long createdTime;
    private long expiredTime;
    private boolean expired;
    private boolean sold;
    private long soldTime;
    private double soldPrice;

    public Auction(int id, UUID seller, ItemStack item, double price) {
        this.id = id;
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.soldPrice = price;
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
}
