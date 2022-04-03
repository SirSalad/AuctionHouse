package xyz.oribuin.auctionhouse.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.auctionhouse.manager.AuctionManager;
import xyz.oribuin.auctionhouse.manager.LocaleManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellCommand extends RoseCommand {

    final List<UUID> confirmList = new ArrayList<>();

    public SellCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Double price) {
        AuctionManager auctionManager = this.rosePlugin.getManager(AuctionManager.class);
        LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
        Player player = (Player) context.getSender();

        if (price == null) {
            locale.sendMessage(player, "command-sell-invalid-price");
            return;
        }

        // Mkae the player confirm they want to sell their item
        if (!this.confirmList.contains(player.getUniqueId())) {
            locale.sendMessage(player, "command-sell-confirm", StringPlaceholders.single("price", String.format("%.2f", price)));
            this.confirmList.add(player.getUniqueId());
            return;
        }

        this.confirmList.remove(player.getUniqueId());
        ItemStack item = player.getInventory().getItemInMainHand().clone();
        auctionManager.createAuction(player, item, price);
    }

    @Override
    protected String getDefaultName() {
        return "sell";
    }

    @Override
    public String getDescriptionKey() {
        return "command-sell-description";
    }

    @Override
    public String getRequiredPermission() {
        return "auctionhouse.sell";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}

