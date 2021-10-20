package fhsmc.minecraft.gary;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Locale;

public class MainCommand extends Command {

    public MainCommand() {
        super("gary");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 1) {
            sender.sendMessage(new TextComponent("You can use the following actions: add, remove"));
        } else {
            switch (args[0].toLowerCase(Locale.ROOT)){
                case "add":
                    Storage.addWhitelistEntry(args[1]);
                    sender.sendMessage(new TextComponent("Added player " + args[1] + " to the whitelist"));
                    return;
                case "remove":
                    Storage.removeWhitelistEntry(args[1]);
                    sender.sendMessage(new TextComponent("Removed player " + args[1] + " from the whitelist"));
                    return;
                default:
                    sender.sendMessage(new TextComponent("That is not a valid action"));
                    return;
            }
        }




    }
}
