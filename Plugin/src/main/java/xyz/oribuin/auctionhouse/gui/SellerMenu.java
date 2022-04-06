package xyz.oribuin.auctionhouse.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.auctionhouse.manager.AuctionManager;
import xyz.oribuin.auctionhouse.manager.MenuManager;
import xyz.oribuin.auctionhouse.util.PluginUtils;
import xyz.oribuin.gui.Item;
import xyz.oribuin.gui.PaginatedGui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SellerMenu extends OriMenu {

    public SellerMenu(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void open(Player player) {
        final PaginatedGui gui = this.createPagedGUI(player, this.getPageSlots());
        List<Integer> borderSlots = this.parseList(this.get("gui-settings.border-slots", List.of("35-54")));
        final ItemStack item = PluginUtils.getItemStack(this.config, "border-item", player, StringPlaceholders.empty());
        for (int slot : borderSlots) {
            gui.setItem(slot, item, this.getEmptyConsumer());
        }

        this.put(gui, "next-page", player, event -> gui.next(player));
        this.put(gui, "previous-page", player, event -> gui.previous(player));

        if (this.get("refresh-menu.enabled", true)) {
            this.put(gui, "refresh-menu", player, event -> this.setSellers(gui, player));
        }

        this.setSellers(gui, player);

        final StringPlaceholders pagePlaceholders = StringPlaceholders.builder("page", gui.getPage())
                .addPlaceholder("total", Math.max(gui.getTotalPages(), 1))
                .build();

        gui.open(player);
        gui.updateTitle(this.format(player, this.get("gui-settings.title", "gui-settings.title"), pagePlaceholders));
    }


    /**
     * Set the auctions for the gui
     *
     * @param gui    Gui
     * @param player Player
     */
    public void setSellers(PaginatedGui gui, Player player) {

        final AuctionManager auctionManager = this.rosePlugin.getManager(AuctionManager.class);

        for (int slot : gui.getItemMap().keySet()) {
            if (this.getPageSlots().contains(slot)) {
                gui.getItemMap().remove(slot);
            }
        }

        gui.getPageItems().clear();
        auctionManager.getActiveSellers().forEach(value -> {
            final ItemStack item = PluginUtils.getItemStack(this.config, "seller-item", player, StringPlaceholders.single("player", value.getName()));
            final ItemStack newItem = new Item.Builder(item)
                    .setOwner(value)
                    .create();

            gui.addPageItem(newItem, event -> this.rosePlugin.getManager(MenuManager.class).get(ViewMenu.class).open(player, value));
        });

        gui.update();
    }

    @Override
    public int rows() {
        return this.get("gui-settings.rows", 4);
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("gui-settings.title", "Auction Sellers (%page%/%total%)");
            this.put("gui-settings.rows", 4);
            this.put("gui-settings.page-slots", List.of("9-26"));
            this.put("gui-settings.border-slots", List.of("0-8", "27-35"));

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
