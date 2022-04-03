package xyz.oribuin.auctionhouse.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.auctionhouse.gui.ConfirmMenu;
import xyz.oribuin.auctionhouse.gui.MainAuctionMenu;
import xyz.oribuin.auctionhouse.gui.OriMenu;
import xyz.oribuin.auctionhouse.gui.SoldAuctionsMenu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuManager extends Manager {

    private final Map<Class<? extends OriMenu>, OriMenu> registeredMenus = new LinkedHashMap<>();

    public MenuManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.getMenu(ConfirmMenu.class);
        this.getMenu(MainAuctionMenu.class);
        this.getMenu(SoldAuctionsMenu.class);

        this.registeredMenus.forEach((name, gui) -> gui.load());

    }

    @Override
    public void disable() {

    }

    /**
     * Get a menu by its class
     *
     * @param menuClass The class of the menu
     * @param <T>       The type of the menu
     * @return The menu
     */
    @SuppressWarnings("unchecked")
    public <T extends OriMenu> T getMenu(Class<T> menuClass) {
        if (this.registeredMenus.containsKey(menuClass)) {
            return (T) this.registeredMenus.get(menuClass);
        }
        try {
            T menu = (T) menuClass.getConstructor(RosePlugin.class).newInstance(this.rosePlugin);
            this.registeredMenus.put(menuClass, menu);
            menu.load();
            return menu;
        } catch (Exception ignored) {
            throw new NullPointerException("Menu class " + menuClass.getName() + " is not registered!");
        }
    }
}
