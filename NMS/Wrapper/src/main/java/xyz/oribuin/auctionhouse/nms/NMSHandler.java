package xyz.oribuin.auctionhouse.nms;

import org.bukkit.inventory.ItemStack;

public interface NMSHandler {

    boolean hasTag(ItemStack item, String key);

    ItemStack setString(ItemStack item, String key, String value);

    ItemStack setInt(ItemStack item, String key, int value);

    ItemStack setLong(ItemStack item, String key, long value);

    ItemStack setDouble(ItemStack item, String key, double value);

    ItemStack setBoolean(ItemStack item, String key, boolean value);

}