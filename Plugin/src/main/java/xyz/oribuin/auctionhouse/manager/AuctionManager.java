package xyz.oribuin.auctionhouse.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import xyz.oribuin.auctionhouse.auction.Auction;
import xyz.oribuin.auctionhouse.hook.VaultHook;
import xyz.oribuin.auctionhouse.manager.ConfigurationManager.Settings;
import xyz.oribuin.auctionhouse.nms.NMSAdapter;
import xyz.oribuin.auctionhouse.util.PluginUtils;

import java.time.Duration;
import java.time.Instant;
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
//        this.data.loadAuctions();
    }

    @Override
    public void disable() {
        // Nothing to do here
    }

    /**
     * Create a new auction with the given information
     *
     * @param player The player creating the auction
     * @param item   The item being auctioned
     * @param price  The price of the item
     */
    public void createAuction(Player player, ItemStack item, double price) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        // Check if the player has permission to create an auction
        int maxAuctions = this.getMaximumAuctions(player);
        int currentAuctions = this.getActiveAuctionsBySeller(player.getUniqueId()).size();

        if (currentAuctions >= maxAuctions) {
            final StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("max", maxAuctions)
                    .addPlaceholder("current", currentAuctions)
                    .build();

            locale.sendMessage(player, "command-sell-max-reached", placeholders);
            return;
        }

        // Check if the player has enough money to create an auction
        double listPrice = Settings.LIST_PRICE.getDouble();
        double playerBalance = VaultHook.getEconomy().getBalance(player);

        if (listPrice > playerBalance) {
            locale.sendMessage(player, "invalid-funds", StringPlaceholders.builder().addPlaceholder("price", listPrice).build());
            return;
        }

        // Check if the price is in correct range
        double minPrice = Settings.LIST_MIN.getDouble();
        double maxPrice = Settings.LIST_MAX.getDouble();

        if (price < minPrice || price > maxPrice) {
            final StringPlaceholders placeholders = StringPlaceholders.builder()
                    .addPlaceholder("min", minPrice)
                    .addPlaceholder("max", maxPrice)
                    .addPlaceholder("price", price)
                    .build();

            locale.sendMessage(player, "command-sell-invalid-price", placeholders);
            return;
        }

        if (item.getType().isAir()) {
            locale.sendMessage(player, "command-sell-disabled-item");
            return;
        }

        // Check if the item's material is allowed to be listed
        if (Settings.DISABLED_MATERIALS.getStringList().contains(item.getType().name())) {
            locale.sendMessage(player, "command-sell-disabled-item");
            return;
        }

        // Check if the item's NBT is allowed to be listed
        boolean hasDisabledNBT = Settings.DISABLED_NBT.getStringList()
                .stream()
                .anyMatch(nbt -> NMSAdapter.getHandler().hasTag(item, nbt));

        if (hasDisabledNBT) {
            locale.sendMessage(player, "command-sell-disabled-item");
            return;
        }

        // Remove the item and check if it has been removed
        if (!player.getInventory().removeItem(item).isEmpty()) {
            locale.sendMessage(player, "command-sell-no-item");
            return;
        }

        this.data.createAuction(player.getUniqueId(), item, price);
        if (listPrice > 0) {
            VaultHook.getEconomy().withdrawPlayer(player, listPrice);
        }

        locale.sendMessage(player, "command-sell-success", StringPlaceholders.single("price", String.format("%.2f", price)));
    }

    /**
     * Allow a player to buy an auction with the given information
     *
     * @param player    The player buying the auction
     * @param auctionId The id of the auction being bought
     */
    public void buyAuction(Player player, int auctionId) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        Optional<Auction> optionalAuction = this.getAuctionById(auctionId);
        if (optionalAuction.isEmpty()) {
            locale.sendMessage(player, "command-buy-auction-gone");
            return;
        }

        final Auction auction = optionalAuction.get();

        // Make sure the auction is has not been sold or expired
        if (auction.isSold() || this.isAuctionExpired(auction)) {
            locale.sendMessage(player, "command-buy-auction-gone");
            return;
        }

        // Check if the player has enough money to buy an auction
        double buyPrice = auction.getPrice();
        buyPrice = buyPrice - (buyPrice * Settings.LIST_TAX.getDouble());

        double playerBalance = VaultHook.getEconomy().getBalance(player);

        if (buyPrice > playerBalance) {
            locale.sendMessage(player, "invalid-funds", StringPlaceholders.builder().addPlaceholder("price", String.format("%.2f", buyPrice)).build());
            return;
        }

        // Remove the item and check if it has been removed
        ItemStack item = auction.getItem();
        if (!player.getInventory().addItem(item).isEmpty()) {
            locale.sendMessage(player, "command-buy-no-space");
            return;
        }

        auction.setSoldTime(System.currentTimeMillis());
        auction.setSold(true);
        auction.setSoldPrice(buyPrice);
        auction.setBuyer(player.getUniqueId());

        this.data.saveAuction(auction);
        VaultHook.getEconomy().withdrawPlayer(player, buyPrice);

        final StringPlaceholders placeholders = StringPlaceholders.builder()
                .addPlaceholder("price", String.format("%.2f", buyPrice))
                .addPlaceholder("seller", Bukkit.getOfflinePlayer(auction.getSeller()).getName())
                .build();

        locale.sendMessage(player, "auction-buy-success", placeholders);
    }

    /**
     * Expire an auction
     *
     * @param auction The auction to expire
     */
    public void expireAuction(Auction auction) {
        auction.setExpired(true);
        auction.setSold(false);
        auction.setExpiredTime(System.currentTimeMillis());
        this.data.saveAuction(auction);
    }

    /**
     * Delete an auction from the database
     *
     * @param auction The auction to delete
     */
    public void deleteAuction(Auction auction) {
        auction.setExpired(true);
        auction.setSold(true);
        this.data.deleteAuction(auction);
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
     * Get all active auctions that are not expired or sold
     *
     * @return a list of auctions
     */
    public List<Auction> getActiveActions() {
        return this.data.getAuctionCache().values()
                .stream()
                .filter(auction -> !auction.isSold() && !auction.isExpired())
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
     * Get the time left for an auction
     *
     * @param auction the auction
     * @return the time left in milliseconds
     */
    public long getTimeLeft(Auction auction) {
        // get the amount of time left since the auction was created in milliseconds
        long timeLeft = Duration.between(Instant.now(), Instant.ofEpochMilli(auction.getCreatedTime() + this.getEndTime())).toMillis();

        if (timeLeft <= 0) {
            timeLeft = 0;
        }
        return timeLeft;
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
        return PluginUtils.parseTime(Settings.LIST_TIME.getString());
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
        int amount = 1;
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
