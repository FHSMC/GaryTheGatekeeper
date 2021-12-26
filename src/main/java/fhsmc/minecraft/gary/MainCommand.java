package fhsmc.minecraft.gary;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Locale;

public class MainCommand extends Command implements TabExecutor {

    public MainCommand() {
        super("gary");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> suggestions = new ArrayList<String>();
        return suggestions;
    }
}
