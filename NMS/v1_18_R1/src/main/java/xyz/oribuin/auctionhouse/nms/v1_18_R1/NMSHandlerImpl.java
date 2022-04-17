package xyz.oribuin.auctionhouse.nms.v1_18_R1;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.auctionhouse.nms.NMSHandler;

public class NMSHandlerImpl implements NMSHandler {
    @Override
    public boolean hasTag(ItemStack item, String key) {
        final net.minecraft.world.item.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = itemStack.getOrCreateTag();
        return tag.contains(key);
    }
}
