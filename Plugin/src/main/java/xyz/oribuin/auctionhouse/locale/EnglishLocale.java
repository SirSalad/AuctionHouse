package xyz.oribuin.auctionhouse.locale;

import dev.rosewood.rosegarden.locale.Locale;

import java.util.HashMap;
import java.util.Map;

public class EnglishLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Oribuin";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new HashMap<>() {{
            this.put("#0", "Plugin Command Prefix");
            this.put("prefix", "<g:#00B4DB:#0083B0>&lAuctionHouse& &8| &f");

            this.put("#1", "Generic Command Messages");
            this.put("no-permission", "You do not have permission to do that!");
            this.put("only-player", "This command can only be executed by a player!");
            this.put("unknown-command", "Unknown command, use #00B4DB/%cmd%&f help for more info");
        }};
    }
}
