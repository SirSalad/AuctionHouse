package xyz.oribuin.auctionhouse.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import xyz.oribuin.auctionhouse.auction.Auction;

public class AuctionSoldEvent extends PlayerEvent {

    private static final HandlerList list = new HandlerList();
    private final Auction auction;

    public AuctionSoldEvent(Player player, Auction auction) {
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

}
