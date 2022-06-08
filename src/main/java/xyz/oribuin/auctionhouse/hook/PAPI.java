package xyz.oribuin.auctionhouse.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.auctionhouse.AuctionHousePlugin;
import xyz.oribuin.auctionhouse.manager.AuctionManager;

public class PAPI extends PlaceholderExpansion {

    private final AuctionHousePlugin plugin;
    private final AuctionManager auctionManager;
    private static boolean enabled = false;

    public PAPI(AuctionHousePlugin plugin) {
        this.plugin = plugin;
        this.auctionManager = this.plugin.getManager(AuctionManager.class);
        if (this.plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            enabled = true;
        }
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        // TODO: Add more placeholders
        return params;
    }

    /**
     * Apply PAPI placeholders to a string
     *
     * @param player The player to apply the placeholders to
     * @param text   The text to apply the placeholders to
     * @return The text with the placeholders applied
     */
    public static String apply(OfflinePlayer player, String text) {
        if (enabled) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    public static boolean isEnabled() {
        return enabled;
    }

}
