package xyz.oribuin.auctionhouse.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.auctionhouse.gui.ConfirmMenu;
import xyz.oribuin.auctionhouse.gui.ExpiredAuctionsMenu;
import xyz.oribuin.auctionhouse.gui.MainAuctionMenu;
import xyz.oribuin.auctionhouse.gui.OriMenu;
import xyz.oribuin.auctionhouse.gui.PersonalAuctionsMenu;
import xyz.oribuin.auctionhouse.gui.SoldAuctionsMenu;
import xyz.oribuin.auctionhouse.gui.ViewMenu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuManager extends Manager {

    private final Map<Class<? extends OriMenu>, OriMenu> registeredMenus = new LinkedHashMap<>();

    public MenuManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        this.registeredMenus.put(ConfirmMenu.class, new ConfirmMenu(this.rosePlugin));
        this.registeredMenus.put(ExpiredAuctionsMenu.class, new ExpiredAuctionsMenu(this.rosePlugin));
        this.registeredMenus.put(SoldAuctionsMenu.class, new SoldAuctionsMenu(this.rosePlugin));
        this.registeredMenus.put(MainAuctionMenu.class, new MainAuctionMenu(this.rosePlugin));
        this.registeredMenus.put(ViewMenu.class, new ViewMenu(this.rosePlugin));
        this.registeredMenus.put(PersonalAuctionsMenu.class, new PersonalAuctionsMenu(this.rosePlugin));

        this.registeredMenus.forEach((name, gui) -> gui.load());
    }

    @SuppressWarnings("unchecked")
    public <T extends OriMenu> T get(Class<T> menuClass) {
        if (this.registeredMenus.containsKey(menuClass)) {
            return (T) this.registeredMenus.get(menuClass);
        }

        return null;
    }

    @Override
    public void disable() {

    }
}
