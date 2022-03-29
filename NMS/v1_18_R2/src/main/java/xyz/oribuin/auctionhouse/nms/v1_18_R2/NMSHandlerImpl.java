package xyz.oribuin.auctionhouse.nms.v1_18_R2;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.auctionhouse.nms.NMSHandler;

public class NMSHandlerImpl implements NMSHandler {


    @Override
    public ItemStack setString(ItemStack item, String key, String value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putString(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setInt(ItemStack item, String key, int value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putInt(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setLong(ItemStack item, String key, long value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putLong(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setDouble(ItemStack item, String key, double value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putDouble(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public ItemStack setBoolean(ItemStack item, String key, boolean value) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        tag.putBoolean(key, value);
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public boolean hasTag(ItemStack item, String key) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        return tag.contains(key);
    }
}
