package xyz.oribuin.auctionhouse;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import xyz.oribuin.auctionhouse.hook.VaultHook;

import java.util.List;

public class AuctionHousePlugin extends RosePlugin {

    private static AuctionHousePlugin instance;

    public AuctionHousePlugin() {
        super(0, 0, null, null, null, null);
    }

    @Override
    protected void enable() {
        instance = this;

        // Check if the server is on 1.16 or higher
        if (NMSUtil.getVersionNumber() < 16) {
            this.getLogger().severe("This plugin requires 1.16 or higher, Disabling plugin!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check if vault is installed
        if (!this.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.getLogger().severe("Vault is not installed or not enabled, Disabling plugin!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register plugin listeners.

        // Register plugin hooks
        VaultHook.hook();

    }

    @Override
    protected void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return null;
    }

    public static AuctionHousePlugin getInstance() {
        return instance;
    }
}
