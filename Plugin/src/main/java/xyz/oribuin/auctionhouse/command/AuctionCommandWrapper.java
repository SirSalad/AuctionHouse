package xyz.oribuin.auctionhouse.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class AuctionCommandWrapper extends RoseCommandWrapper {

    public AuctionCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "auctionhouse";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("ah");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("xyz.oribuin.auctionhouse.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return false;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }
}
