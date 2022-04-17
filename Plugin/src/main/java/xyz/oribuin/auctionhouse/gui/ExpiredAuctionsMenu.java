package xyz.oribuin.auctionhouse.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.oribuin.auctionhouse.manager.AuctionManager;
import xyz.oribuin.auctionhouse.manager.MenuManager;
import xyz.oribuin.auctionhouse.util.PluginUtils;
import xyz.oribuin.gui.Item;
import xyz.oribuin.gui.PaginatedGui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExpiredAuctionsMenu extends OriMenu {

    private final MenuManager menuManager = this.rosePlugin.getManager(MenuManager.class);

    public ExpiredAuctionsMenu(RosePlugin rosePlugin) {
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
        this.put(gui, "refresh-menu", player, event -> this.setAuctions(gui, player));
        this.put(gui, "previous-page", player, event -> gui.previous(player));

        this.put(gui, "sold-auctions", player, event -> this.menuManager.get(SoldAuctionsMenu.class).open(player));
        this.put(gui, "main-auctions", player, event -> this.menuManager.get(MainAuctionMenu.class).open(player));
        this.put(gui, "my-auctions", player, event -> this.menuManager.get(PersonalAuctionsMenu.class).open(player));

        this.setAuctions(gui, player);

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
    public void setAuctions(PaginatedGui gui, Player player) {
        gui.getPageItems().clear();

        for (int slot : gui.getItemMap().keySet()) {
            if (this.getPageSlots().contains(slot)) {
                gui.getItemMap().remove(slot);
            }
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat(this.get("date-format", "HH:mm:ss dd/MM/yy"));
        final AuctionManager auctionManager = this.rosePlugin.getManager(AuctionManager.class);

        List<String> configLore = this.get("auction-lore", List.of("Missing option auction-lore in /menus/sold_auctions.yml"));

        boolean loreBefore = this.get("lore-before", false);


        this.async(() -> auctionManager.getExpiredAuctionsBySeller(player.getUniqueId()).forEach(value -> {
            ItemStack baseItem = value.getItem().clone();
            final ItemMeta meta = baseItem.getItemMeta();
            if (meta == null) {
                return;
            }

            List<String> lore = new ArrayList<>();

            if (loreBefore) {
                lore.addAll(configLore);
            }

            if (meta.getLore() != null) {
                lore.addAll(meta.getLore());
            }

            if (!loreBefore) {
                lore.addAll(configLore);
            }

            final StringPlaceholders auctionPls = StringPlaceholders.builder()
                    .addPlaceholder("price", String.format("%.2f", value.getPrice()))
                    .addPlaceholder("expired", value.getExpiredTime() == 0 ? "Unknown" : dateFormat.format(new Date(value.getExpiredTime())))
                    .build();

            lore = lore.stream().map(s -> this.format(player, s, auctionPls)).collect(Collectors.toList());
            baseItem = new Item.Builder(baseItem)
                    .setLore(lore)
                    .create();

            gui.addPageItem(baseItem, event -> {
                ItemStack item = value.getItem().clone();
                if (player.getInventory().addItem(item).isEmpty()) {
                    auctionManager.deleteAuction(value);
                    // Do this sync to prevent duplicate items
                    this.sync(() -> this.setAuctions(gui, player));
                }
            });

            gui.update();
        }));
        
    }

    @Override
    public int rows() {
        return this.get("gui-settings.rows", 6);
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("gui-settings.title", "Expired Auctions (%page%/%total%)");
            this.put("gui-settings.rows", 6);
            this.put("gui-settings.page-slots", List.of("9-44"));
            this.put("gui-settings.border-slots", List.of("0-8", "45-53"));

            this.put("#1", "Auction Lore Settings");
            this.put("auction-lore", List.of(
                    " &8------ #00B4DB&lSelling Item &8------",
                    " &f| &7Price: &f$%price%",
                    " &f| &7Expired: &f%expired%",
                    " &f|",
                    " &f| &7Click to reclaim."
            ));

            this.put("#2", "Expired Time Format");
            this.put("date-format", "HH:mm:ss dd/MM/yy");

            this.put("#3", "Should the lore be before or after the item's lore?");
            this.put("lore-before", false);

            this.put("#4", "Border Settings");
            this.put("border-item.material", "BLACK_STAINED_GLASS_PANE");
            this.put("border-item.name", " ");

            this.put("#5", "Next Page");
            this.put("next-page.material", "PAPER");
            this.put("next-page.name", "#00B4DB&lNext Page");
            this.put("next-page.lore", List.of(" &f| &7Click to go to", " &f| &7the next page."));
            this.put("next-page.glow", true);
            this.put("next-page.slot", 50);

            this.put("#6", "Previous Page");
            this.put("previous-page.material", "PAPER");
            this.put("previous-page.name", "#00B4DB&lPrevious Page");
            this.put("previous-page.lore", List.of(" &f| &7Click to go to", " &f| &7the previous page."));
            this.put("previous-page.glow", true);
            this.put("previous-page.slot", 48);

            this.put("#7", "Sold Auctions Menu");
            this.put("sold-auctions.enabled", true);
            this.put("sold-auctions.material", "GOLD_INGOT");
            this.put("sold-auctions.name", "#00B4DB&lSold Auctions");
            this.put("sold-auctions.lore", List.of(" &f| &7Click to go to", " &f| &7the sold auctions menu."));
            this.put("sold-auctions.glow", true);
            this.put("sold-auctions.slot", 2);

            this.put("#8", "My Auctions");
            this.put("my-auctions.enabled", true);
            this.put("my-auctions.material", "BOOK");
            this.put("my-auctions.name", "#00B4DB&lMy Auctions");
            this.put("my-auctions.lore", List.of(" &f| &7Click to go to", " &f| &7your auctions."));
            this.put("my-auctions.glow", true);
            this.put("my-auctions.slot", 4);

            this.put("#9", "Main Auctions Menu");
            this.put("main-auctions.enabled", true);
            this.put("main-auctions.material", "BEACON");
            this.put("main-auctions.name", "#00B4DB&lMain Menu");
            this.put("main-auctions.lore", List.of(" &f| &7Click to go to", " &f| &7the main auction menu."));
            this.put("main-auctions.glow", true);
            this.put("main-auctions.slot", 6);

            this.put("#10", "Refresh Menu");
            this.put("refresh-menu.enabled", true);
            this.put("refresh-menu.material", "SUNFLOWER");
            this.put("refresh-menu.name", "#00B4DB&lRefresh Menu");
            this.put("refresh-menu.lore", List.of(" &f| &7Click to refresh the menu."));
            this.put("refresh-menu.glow", true);
            this.put("refresh-menu.slot", 49);
        }};
    }

    @Override
    public String getMenuName() {
        return "expired_auctions";
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
