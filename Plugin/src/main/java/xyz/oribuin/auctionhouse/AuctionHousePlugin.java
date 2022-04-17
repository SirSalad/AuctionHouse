package xyz.oribuin.auctionhouse;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import xyz.oribuin.auctionhouse.hook.PAPI;
import xyz.oribuin.auctionhouse.hook.VaultHook;
import xyz.oribuin.auctionhouse.manager.AuctionManager;
import xyz.oribuin.auctionhouse.manager.CommandManager;
import xyz.oribuin.auctionhouse.manager.ConfigurationManager;
import xyz.oribuin.auctionhouse.manager.DataManager;
import xyz.oribuin.auctionhouse.manager.LocaleManager;

import java.util.List;

public class AuctionHousePlugin extends RosePlugin {

    private static AuctionHousePlugin instance;

    public AuctionHousePlugin() {
        super(-1, -1, ConfigurationManager.class, DataManager.class, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    protected void enable() {
        // Check if the server is on 1.17 or higher
        if (NMSUtil.getVersionNumber() < 17) {
            this.getLogger().severe("This plugin requires 1.17 or higher, Disabling plugin!");
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

        if (PAPI.isEnabled()) {
            new PAPI(this).register();
        }

    }

    @Override
    protected void disable() {
        // Do nothing
    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(AuctionManager.class);
    }

    public static AuctionHousePlugin getInstance() {
        return instance;
    }
}
