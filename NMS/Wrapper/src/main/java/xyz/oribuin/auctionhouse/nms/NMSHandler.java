package xyz.oribuin.auctionhouse.nms;

import org.bukkit.inventory.ItemStack;

public interface NMSHandler {

    boolean hasTag(ItemStack item, String key);

}
