package xyz.oribuin.auctionhouse.gui;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.auctionhouse.util.PluginUtils;
import xyz.oribuin.gui.BaseGui;
import xyz.oribuin.gui.Gui;
import xyz.oribuin.gui.Item;
import xyz.oribuin.gui.PaginatedGui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class OriMenu {

    protected final RosePlugin rosePlugin;
    protected CommentedFileConfiguration config;

    public OriMenu(RosePlugin rosePlugin) {
        this.rosePlugin = rosePlugin;
    }

    /**
     * Get the amount of rows for the gui
     */
    public abstract int rows();

    /**
     * Get the default config values for the GUI
     *
     * @return The default config values
     */
    public abstract Map<String, Object> getDefaultValues();

    /**
     * @return The name of the GUI
     */
    public abstract String getMenuName();

    /**
     * @return The slots for page items
     */
    public List<Integer> getPageSlots() {
        return new ArrayList<>();
    }

    /**
     * Get the config values for the GUI
     *
     * @param path The path to the config values
     * @param def  The default value if the path is not found
     * @param <T>  The type of the config value
     */
    @SuppressWarnings("unchecked")
    public final <T> T get(String path, T def) {
        return PluginUtils.get(this.config, path, def);
    }

    /**
     * Create the menu file if it doesn't exist and add the default values
     */
    public void load() {
        final File folder = new File(this.rosePlugin.getDataFolder(), "menus");
        boolean newFile = false;
        if (!folder.exists()) {
            folder.mkdirs();
        }

        final File file = new File(folder, this.getMenuName() + ".yml");
        try {
            if (!file.exists()) {
                file.createNewFile();
                newFile = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.config = CommentedFileConfiguration.loadConfiguration(file);
        if (newFile) {
            this.getDefaultValues().forEach((path, object) -> {
                if (path.startsWith("#")) {
                    this.config.addPathedComments(path, (String) object);
                } else {
                    this.config.set(path, object);
                }
            });
        }

        this.config.save();
    }

    /**
     * Create a paged GUI for the given player
     *
     * @param player    The player to create the GUI for
     * @param pageSlots The slots for the page items
     * @return The created GUI
     */
    public final PaginatedGui createPagedGUI(Player player, List<Integer> pageSlots) {
        final PaginatedGui gui = new PaginatedGui(this.rows() * 9, this.format(player, this.get("gui-settings.title", this.getMenuName())), pageSlots);


        // Prevent the player from opening the gui.
        gui.setDefaultClickFunction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();
        });

        // Prevent the player from interacting with their inventory while the GUI is open
        gui.setPersonalClickAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();

        });

        // Prevent the player from dragging items.
        gui.setDragAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        });

        return gui;
    }

    /**
     * Create a GUI for the given player without pages
     *
     * @param player The player to create the GUI for
     * @return The created GUI
     */
    public Gui createGUI(Player player) {
        final Gui gui = new Gui(this.rows() * 9, this.format(player, this.get("gui-settings.title", this.getMenuName())));

        // Prevent the player from opening the gui.
        gui.setDefaultClickFunction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();
        });

        // Prevent the player from interacting with their inventory while the GUI is open
        gui.setPersonalClickAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            ((Player) event.getWhoClicked()).updateInventory();

        });

        // Prevent the player from dragging items.
        gui.setDragAction(event -> {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        });

        return gui;
    }

    /**
     * Place an empty item in the gui.
     *
     * @param gui  The GUI
     * @param slot The Item Slot
     * @param item The Item
     */
    public final void put(BaseGui gui, int slot, ItemStack item) {
        gui.setItem(slot, item, inventoryClickEvent -> {
            // Empty Function
        });
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui      The GUI
     * @param itemPath The path to the item
     * @param player  The item viewer
     */
    public final void put(BaseGui gui, String itemPath, Player player) {
        this.put(gui, itemPath, player, StringPlaceholders.empty());
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui          The GUI
     * @param itemPath     The path to the item
     * @param viewer       The item viewer
     * @param placeholders The placeholders to use
     */
    public final void put(BaseGui gui, String itemPath, Player viewer, StringPlaceholders placeholders) {
        this.put(gui, itemPath, viewer, placeholders, event -> {
            // Empty Function
        });
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui           The GUI
     * @param itemPath      The path to the item
     * @param viewer        The item viewer
     * @param eventConsumer The event consumer
     */
    public final void put(BaseGui gui, String itemPath, Player viewer, Consumer<InventoryClickEvent> eventConsumer) {
        this.put(gui, itemPath, viewer, StringPlaceholders.empty(), eventConsumer);
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui           The GUI
     * @param itemPath      The path to the item
     * @param viewer        The item viewer
     * @param placeholders  The placeholders to use
     * @param eventConsumer The event consumer
     */
    public final void put(BaseGui gui, String itemPath, Player viewer, StringPlaceholders placeholders, Consumer<InventoryClickEvent> eventConsumer) {
        Integer slot = this.get(itemPath + ".slot", null);
        if (slot == null)
            return;

        if (this.get(itemPath + ".enabled", true)) {
            this.put(gui, slot, itemPath, viewer, placeholders, eventConsumer);
        }
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui      The GUI
     * @param slot     The slot of the item
     * @param itemPath The path to the item
     */
    public final void put(BaseGui gui, int slot, String itemPath, Player viewer) {
        this.put(gui, slot, itemPath, viewer, StringPlaceholders.empty());
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui          The GUI
     * @param slot         The slot of the item
     * @param itemPath     The path to the item
     * @param placeholders
     */
    public final void put(BaseGui gui, int slot, String itemPath, Player viewer, StringPlaceholders placeholders) {
        this.put(gui, slot, itemPath, viewer, placeholders, inventoryClickEvent -> {
            // Empty Function
        });
    }

    /**
     * A cleaner function for setting an item in the gui.
     *
     * @param gui           The GUI
     * @param slot          The slot of the item
     * @param itemPath      The path to the item
     * @param eventConsumer The item functionality.
     */
    public final void put(BaseGui gui, int slot, String itemPath, Player viewer, StringPlaceholders placeholders, Consumer<InventoryClickEvent> eventConsumer) {
        ItemStack item = PluginUtils.getItemStack(this.config, itemPath, viewer, placeholders);
        if (item == null)
            item = new Item.Builder(Material.BARRIER)
                    .setName(HexUtils.colorify("&cInvalid Material:" + itemPath + ".material"))
                    .create();

        gui.setItem(slot, item, eventConsumer);
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player The player to format the string for
     * @param text   The string to format
     * @return The formatted string
     */
    public final String format(Player player, String text) {
        return PluginUtils.format(player, text);
    }

    /**
     * Format a string with placeholders and color codes
     *
     * @param player       The player to format the string for
     * @param text         The text to format
     * @param placeholders The placeholders to replace
     * @return The formatted string
     */
    public final String format(Player player, String text, StringPlaceholders placeholders) {
        return PluginUtils.format(player, text, placeholders);
    }

    /**
     * Get a bukkit color from a hex code
     *
     * @param hex The hex code
     */
    public Color getColor(String hex) {
        return PluginUtils.fromHex(hex);
    }


    // Parse a List of X-X into a List of Integers
    public List<Integer> parseList(List<String> list) {
        List<Integer> newList = new ArrayList<>();
        for (String s : list) {
            String[] split = s.split("-");
            if (split.length != 2) {
                continue;
            }

            newList.addAll(this.getNumberRange(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        }

        return newList;
    }

    /**
     * Get a range of numbers as a list
     *
     * @param start The start of the range
     * @param end   The end of the range
     * @return A list of numbers
     */
    public List<Integer> getNumberRange(int start, int end) {
        if (start == end) {
            return List.of(start);
        }

        final List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(i);
        }

        return list;
    }

    public final void sync(Runnable runnable) {
        this.rosePlugin.getServer().getScheduler().runTask(this.rosePlugin, runnable);
    }

    public final void async(Runnable runnable) {
        this.rosePlugin.getServer().getScheduler().runTaskAsynchronously(this.rosePlugin, runnable);
    }
}
