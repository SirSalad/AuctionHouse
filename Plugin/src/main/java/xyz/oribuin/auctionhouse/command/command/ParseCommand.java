package xyz.oribuin.auctionhouse.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.command.framework.types.GreedyString;
import xyz.oribuin.auctionhouse.util.PluginUtils;

public class ParseCommand extends RoseCommand {
    public ParseCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, GreedyString time) {
        context.getSender().sendMessage("Parsing time: " + PluginUtils.formatTime(PluginUtils.parseTime(time.get())));
    }

    @Override
    protected String getDefaultName() {
        return "parse";
    }

    @Override
    public String getDescriptionKey() {
        return "command-parse-description";
    }

    @Override
    public String getRequiredPermission() {
        return "auctionhouse.parse";
    }
}
