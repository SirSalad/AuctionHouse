package xyz.oribuin.auctionhouse.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import xyz.oribuin.auctionhouse.auction.Auction;

public class AuctionCreateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList list = new HandlerList();
    private final Auction auction;
    private boolean cancelled = false;

    public AuctionCreateEvent(Player player, Auction auction) {
        super(player);
        this.auction = auction;
    }

    public static HandlerList getHandlerList() {
        return list;
    }

    public Auction getAuction() {
        return auction;
    }

    @Override
    public HandlerList getHandlers() {
        return list;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
