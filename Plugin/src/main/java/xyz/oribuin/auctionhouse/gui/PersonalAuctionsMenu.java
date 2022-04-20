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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PersonalAuctionsMenu extends OriMenu {

    private final MenuManager menuManager = this.rosePlugin.getManager(MenuManager.class);

    public PersonalAuctionsMenu(RosePlugin rosePlugin) {
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
        this.put(gui, "expired-auctions", player, event -> this.menuManager.get(ExpiredAuctionsMenu.class).open(player));

        this.setAuctions(gui, player);

        gui.open(player);
        gui.updateTitle(this.format(player, this.get("gui-settings.title", "gui-settings.title"), getPagePlaceholders(gui)));
    }


    /**
     * Set the auctions for the gui
     *
     * @param gui    Gui
     * @param player Player
     */
    public void setAuctions(PaginatedGui gui, Player player) {
        final AuctionManager auctionManager = this.rosePlugin.getManager(AuctionManager.class);

        List<String> configLore = this.get("auction-lore", List.of("Missing option auction-lore in /menus/sold_auctions.yml"));

        boolean loreBefore = this.get("lore-before", false);
        
        for (int slot : gui.getItemMap().keySet()) {
            if (this.getPageSlots().contains(slot)) {
                gui.getItemMap().remove(slot);
            }
        }
        
        gui.getPageItems().clear();
        this.async(() -> {
            auctionManager.getActiveAuctionsBySeller(player.getUniqueId()).forEach(value -> {

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

                final String timeLeft = PluginUtils.formatTime(auctionManager.getTimeLeft(value));
                final String formattedTime = timeLeft.equals("0") ? "Expired" : timeLeft;

                final StringPlaceholders auctionPls = StringPlaceholders.builder()
                        .addPlaceholder("price", String.format("%.2f", value.getPrice()))
                        .addPlaceholder("time", formattedTime)
                        .build();


                lore = lore.stream().map(s -> this.format(player, s, auctionPls)).collect(Collectors.toList());
                baseItem = new Item.Builder(baseItem)
                        .setLore(lore)
                        .create();

                gui.addPageItem(baseItem, event -> {
                    if (value.isExpired() || value.isSold()) {
                        return;
                    }

                    ItemStack item = value.getItem().clone();
                    if (player.getInventory().addItem(item).isEmpty()) {
                        auctionManager.deleteAuction(value);
                        this.sync(() -> this.setAuctions(gui, player));
                    }
                });
            });

            gui.update();
            // opening a gui cannot be async iirc
            this.sync(() -> gui.updateTitle(this.format(player, this.get("gui-settings.title", "gui-settings.title"), this.getPagePlaceholders(gui))));
        });
    }

    @Override
    public int rows() {
        return this.get("gui-settings.rows", 6);
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "GUI Settings");
            this.put("gui-settings.title", "Your Auctions (%page%/%total%)");
            this.put("gui-settings.rows", 6);
            this.put("gui-settings.page-slots", List.of("9-44"));
            this.put("gui-settings.border-slots", List.of("0-8", "45-53"));

            this.put("#1", "Auction Lore Settings");
            this.put("auction-lore", List.of(
                    " &8------ #00B4DB&lSelling Item &8------",
                    " &f| &7Price: &f$%price%",
                    " &f| &7Time: &f%time%",
                    " &f|",
                    " &f| &7Click to cancel & claim."
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
        return "personal_auctions";
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
