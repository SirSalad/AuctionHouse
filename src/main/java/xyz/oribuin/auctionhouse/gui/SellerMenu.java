package xyz.oribuin.auctionhouse.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.auctionhouse.manager.AuctionManager;
import xyz.oribuin.auctionhouse.manager.MenuManager;
import xyz.oribuin.auctionhouse.util.ItemBuilder;
import xyz.oribuin.auctionhouse.util.PluginUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SellerMenu extends OriMenu {

    private final MenuManager menuManager = this.rosePlugin.getManager(MenuManager.class);

    public SellerMenu(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void open(Player player) {
        boolean shouldScroll = this.get("gui-settings.should-scroll", false);
        ScrollType scrollType = ScrollType.valueOf(this.get("gui-settings.scroll-type", "HORIZONTAL"));
        final PaginatedGui gui = shouldScroll ? this.createScrollingGui(player, scrollType) : this.createPagedGUI(player);

        final ItemStack item = PluginUtils.getItemStack(this.config, "border-item", player, StringPlaceholders.empty());
        List<Integer> borderSlots = this.parseList(this.get("gui-settings.border-slots", List.of("35-54")));
        gui.setItem(borderSlots, new GuiItem(item));

        this.put(gui, "next-page", player, event -> {
            gui.next();
            gui.updateTitle(this.formatString(player, this.get("gui-settings.title", "gui-settings.title"), this.getPagePlaceholders(gui)));
        });

        this.put(gui, "refresh-menu", player, event -> this.setSellers(gui, player));
        this.put(gui, "previous-page", player, event -> {
            gui.previous();
            gui.updateTitle(this.formatString(player, this.get("gui-settings.title", "gui-settings.title"), this.getPagePlaceholders(gui)));
        });

        this.put(gui, "sold-auctions", player, event -> this.menuManager.get(SoldAuctionsMenu.class).open(player));
        this.put(gui, "main-auctions", player, event -> this.menuManager.get(MainAuctionMenu.class).open(player));
        this.put(gui, "expired-auctions", player, event -> this.menuManager.get(ExpiredAuctionsMenu.class).open(player));

        this.setSellers(gui, player);
        gui.open(player);
        this.sync(() -> gui.updateTitle(this.formatString(player, this.get("gui-settings.title", "gui-settings.title"), this.getPagePlaceholders(gui))));
    }


    /**
     * Set the auctions for the gui
     *
     * @param gui    Gui
     * @param player Player
     */
    public void setSellers(PaginatedGui gui, Player player) {

        final AuctionManager auctionManager = this.rosePlugin.getManager(AuctionManager.class);
        gui.clearPageItems(true);


        this.async(() -> {
            List<OfflinePlayer> activeSellers = new ArrayList<>(auctionManager.getActiveSellers());
            activeSellers.sort(Comparator.comparing(OfflinePlayer::getName));
            activeSellers.forEach(value -> {
                final ItemStack item = PluginUtils.getItemStack(this.config, "seller-item", player, StringPlaceholders.single("player", value.getName()));
                final ItemStack newItem = new ItemBuilder(item)
                        .setOwner(value)
                        .create();

                gui.addItem(new GuiItem(newItem, event -> this.rosePlugin.getManager(MenuManager.class).get(ViewMenu.class).open(player, value)));
            });

            gui.update();
            // opening a gui cannot be async iirc
            this.sync(() -> gui.updateTitle(this.formatString(player, this.get("gui-settings.title", "gui-settings.title"), this.getPagePlaceholders(gui))));
        });
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("gui-settings.title", "Auction Sellers (%page%/%total%)");
            this.put("gui-settings.rows", 4);
            this.put("gui-settings.page-slots", List.of("9-26"));
            this.put("gui-settings.border-slots", List.of("0-8", "27-35"));
            this.put("gui-settings.should-scroll", false);
            this.put("gui-settings.scroll-type", "HORIZONTAL");

            this.put("#1", "Seller Item");
            this.put("seller-item.material", "PLAYER_HEAD");
            this.put("#2", "Player head will default to the player's skin");
            this.put("seller-item.name", "#00B4DB&l%player%");
            this.put("seller-item.lore", List.of(" &f| &7Click to view all", " &f| &7auctions from this seller"));

            this.put("#3", "Border Settings");
            this.put("border-item.material", "BLACK_STAINED_GLASS_PANE");
            this.put("border-item.name", " ");

            this.put("#4", "Next Page");
            this.put("next-page.material", "PAPER");
            this.put("next-page.name", "#00B4DB&lNext Page");
            this.put("next-page.lore", List.of(" &f| &7Click to go to", " &f| &7the next page."));
            this.put("next-page.glow", true);
            this.put("next-page.slot", 32);

            this.put("#5", "Previous Page");
            this.put("previous-page.material", "PAPER");
            this.put("previous-page.name", "#00B4DB&lPrevious Page");
            this.put("previous-page.lore", List.of(" &f| &7Click to go to", " &f| &7the previous page."));
            this.put("previous-page.glow", true);
            this.put("previous-page.slot", 30);

            this.put("#6", "Refresh Menu");
            this.put("refresh-menu.enabled", true);
            this.put("refresh-menu.material", "SUNFLOWER");
            this.put("refresh-menu.name", "#00B4DB&lRefresh Menu");
            this.put("refresh-menu.lore", List.of(" &f| &7Click to refresh the menu."));
            this.put("refresh-menu.glow", true);
            this.put("refresh-menu.slot", 31);

            this.put("#7", "Sold Auctions Menu");
            this.put("sold-auctions.enabled", true);
            this.put("sold-auctions.material", "GOLD_INGOT");
            this.put("sold-auctions.name", "#00B4DB&lSold Auctions");
            this.put("sold-auctions.lore", List.of(" &f| &7Click to go to", " &f| &7the sold auctions menu."));
            this.put("sold-auctions.glow", true);
            this.put("sold-auctions.slot", 2);

            this.put("#8", "Main Auctions Menu");
            this.put("main-auctions.enabled", true);
            this.put("main-auctions.material", "BEACON");
            this.put("main-auctions.name", "#00B4DB&lMain Menu");
            this.put("main-auctions.lore", List.of(" &f| &7Click to go to", " &f| &7the main auction menu."));
            this.put("main-auctions.glow", true);
            this.put("main-auctions.slot", 4);

            this.put("#9", "Expired Auctions Menu");
            this.put("expired-auctions.enabled", true);
            this.put("expired-auctions.material", "CLOCK");
            this.put("expired-auctions.name", "#00B4DB&lExpired Auctions");
            this.put("expired-auctions.lore", List.of(" &f| &7Click to go to", " &f| &7the expired auctions menu."));
            this.put("expired-auctions.glow", true);
            this.put("expired-auctions.slot", 6);
        }};
    }

    @Override
    public String getMenuName() {
        return "seller_menu";
    }

    @Override
    public List<Integer> getPageSlots() {
        return this.parseList(this.get("gui-settings.page-slots", List.of("0-44")));
    }

    /**
     * Get an empty consumer
     *
     * @return Consumer
     */
    private Consumer<InventoryClickEvent> getEmptyConsumer() {
        return e -> {
            // Empty function
        };
    }
}
