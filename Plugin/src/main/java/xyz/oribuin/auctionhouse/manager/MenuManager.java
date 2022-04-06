package xyz.oribuin.auctionhouse.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import xyz.oribuin.auctionhouse.gui.ConfirmMenu;
import xyz.oribuin.auctionhouse.gui.ExpiredAuctionsMenu;
import xyz.oribuin.auctionhouse.gui.MainAuctionMenu;
import xyz.oribuin.auctionhouse.gui.OriMenu;
import xyz.oribuin.auctionhouse.gui.PersonalAuctionsMenu;
import xyz.oribuin.auctionhouse.gui.SellerMenu;
import xyz.oribuin.auctionhouse.gui.SoldAuctionsMenu;
import xyz.oribuin.auctionhouse.gui.ViewMenu;

import java.util.LinkedHashMap;
import java.util.Map;

public class MenuManager extends Manager {

    private Map<Class<? extends OriMenu>, OriMenu> registeredMenus;

    public MenuManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {

        this.registeredMenus = new LinkedHashMap<>() {{
            this.put(ConfirmMenu.class, new ConfirmMenu(rosePlugin));
            this.put(ExpiredAuctionsMenu.class, new ExpiredAuctionsMenu(rosePlugin));
            this.put(SoldAuctionsMenu.class, new SoldAuctionsMenu(rosePlugin));
            this.put(MainAuctionMenu.class, new MainAuctionMenu(rosePlugin));
            this.put(ViewMenu.class, new ViewMenu(rosePlugin));
            this.put(PersonalAuctionsMenu.class, new PersonalAuctionsMenu(rosePlugin));
            this.put(SellerMenu.class, new SellerMenu(rosePlugin));
        }};

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
