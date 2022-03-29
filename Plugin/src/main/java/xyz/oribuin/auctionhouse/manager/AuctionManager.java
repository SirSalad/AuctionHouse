package xyz.oribuin.auctionhouse.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import xyz.oribuin.auctionhouse.auction.Auction;
import xyz.oribuin.auctionhouse.hook.VaultHook;
import xyz.oribuin.auctionhouse.util.PluginUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AuctionManager extends Manager {

    private final Map<UUID, Long> listingCooldown = new HashMap<>();
    private DataManager data;

    public AuctionManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.data = this.rosePlugin.getManager(DataManager.class);
        this.data.loadAuctions();
    }

    @Override
    public void disable() {
        // Nothing to do here
    }

    /**
     * Create a new auction with the given information
     *
     * @param player    The player creating the auction
     * @param item The item being auctioned
     * @param price     The price of the item
     */
    public Optional<Auction> createAuction(Player player, ItemStack item, double price) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        // Check if the player has permission to create an auction
        int maxAuctions = this.getMaximumAuctions(player);
        int currentAuctions = this.getActiveAuctionsBySeller(player.getUniqueId()).size();

        if (maxAuctions <= currentAuctions) {
            final StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("max_auctions", maxAuctions)
                    .addPlaceholder("current_auctions", currentAuctions)
                    .build();

            locale.sendMessage(player, "command-create-max-reached", placeholders);
            return Optional.empty();
        }

        // Check if the player has enough money to create an auction
        double listPrice = ConfigurationManager.Settings.LIST_PRICE.getDouble();
        double playerBalance = VaultHook.getEconomy().getBalance(player);

        if (listPrice < playerBalance) {
            locale.sendMessage(player, "invalid-funds", StringPlaceholders.builder().addPlaceholder("price", listPrice).build());
            return Optional.empty();
        }

        // Check if the price is in correct range
        double minPrice = ConfigurationManager.Settings.LIST_MAX.getDouble();
        double maxPrice = ConfigurationManager.Settings.LIST_MIN.getDouble();

        if (price < minPrice || price > maxPrice) {
            final StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("min_price", minPrice)
                    .addPlaceholder("max_price", maxPrice)
                    .build();

            locale.sendMessage(player, "command-create-invalid-price", placeholders);
            return Optional.empty();
        }

        // Check if the item is allowed to be sold

        Auction auction = this.data.createAuction(player.getUniqueId(), item, price);

        // TODO The rest of this method
        return auction == null ? Optional.empty() : Optional.of(auction);
    }

    /**
     * Get all auctions that haven't been deleted yet
     *
     * @return a list of auctions
     */
    public Map<Integer, Auction> getAllAuctions() {
        return this.data.getAuctionCache();
    }

    /**
     * Get auction by id
     *
     * @param id the id of the auction
     * @return the auction
     */
    public Optional<Auction> getAuctionById(int id) {
        return Optional.ofNullable(this.data.getAuctionCache().get(id));
    }

    /**
     * Get all auctions that are sold by a player
     *
     * @param uuid the uuid of the player
     * @return a list of auctions
     */
    public List<Auction> getAuctionsBySeller(UUID uuid) {
        return this.data.getAuctionCache().values()
                .stream()
                .filter(auction -> auction.getSeller().equals(uuid))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get all auctions that have not expired or sold yet
     *
     * @param uuid the uuid of the player
     * @return a list of auctions
     */
    public List<Auction> getActiveAuctionsBySeller(UUID uuid) {
        return this.getAuctionsBySeller(uuid)
                .stream()
                .filter(auction -> !auction.isSold() && !auction.isExpired())
                .collect(Collectors.toList());
    }

    /**
     * Get all auctions that are expired
     *
     * @param uuid the uuid of the player
     * @return a list of expired auctions
     */
    public List<Auction> getExpiredAuctionsBySeller(UUID uuid) {
        return this.getAuctionsBySeller(uuid)
                .stream()
                .filter(Auction::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * Get all auctions that are sold by a player
     *
     * @param uuid the uuid of the player
     * @return a list of auctions
     */
    public List<Auction> getSoldAuctionsBySeller(UUID uuid) {
        return this.getAuctionsBySeller(uuid)
                .stream()
                .filter(Auction::isSold)
                .collect(Collectors.toList());
    }

    /**
     * Check if a player's inventory can hold an item.
     *
     * @param player the player to check
     * @return true if the player's inventory can hold the item
     */
    public boolean canHoldItem(Player player) {
        return player.getInventory().firstEmpty() != -1;
    }

    /**
     * Get the time until auctions expire
     *
     * @return time until auctions expire
     */
    public long getEndTime() {
        return PluginUtils.parseTime(ConfigurationManager.Settings.LIST_TIME.toString());
    }

    /**
     * Check if an auction is expired
     *
     * @param auction the auction to check
     * @return true if the auction is expired
     */
    public boolean isAuctionExpired(Auction auction) {
        if (auction.isExpired()) {
            return true;
        }

        return auction.getCreatedTime() + this.getEndTime() < System.currentTimeMillis();
    }

    /**
     * Check if an auction has expired
     *
     * @param id the id of the auction
     * @return true if the auction is expired
     */
    public boolean isAuctionExpired(int id) {
        return this.getAuctionById(id).map(this::isAuctionExpired).orElse(false);
    }

    /**
     * Check if an auction is sold
     *
     * @param auction the auction to check
     * @return true if the auction is sold
     */
    public boolean isAuctionSold(Auction auction) {
        return auction.isSold();
    }

    /**
     * Check if an auction is sold
     *
     * @param id the id of the auction
     */
    public boolean isAuctionSold(int id) {
        return this.getAuctionById(id).map(this::isAuctionSold).orElse(false);
    }

    /**
     * Get the maximum amount of auctions a player can have open
     *
     * @param player the player to check
     * @return the maximum amount of auctions a player can have open
     */
    public int getMaximumAuctions(Player player) {
        int amount = 0;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            final String target = info.getPermission().toLowerCase();

            if (target.startsWith("auctionhouse.limit.") && info.getValue()) {
                try {
                    amount = Math.max(amount, Integer.parseInt(target.substring(target.lastIndexOf('.') + 1)));
                } catch (NumberFormatException ignored) {
                    // Ignore
                }
            }
        }

        return amount;
    }
}
