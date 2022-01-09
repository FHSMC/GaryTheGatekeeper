package fhsmc.minecraft.gary;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainCommand extends Command implements TabExecutor {

    public MainCommand() {
        super("gary");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        System.out.println(args.length);
        if (args.length > 0) {
            try {

                switch (args[0]) {
                    case "set":
                        Storage.setIGNFromDiscord(args[1], args[3], args[2].equals("bedrock"));
                        sender.sendMessage(new TextComponent("Set " + args[2] + " ign to " + args[3] + " for Discord user " + args[1]));
                        break;
                    case "add":
                        Storage.addAnonymousIGN(args[2], args[1].equals("bedrock"));
                        sender.sendMessage(new TextComponent("Added username " + args[2] + "(" + args[1] + ") to whitelist."));
                        break;
                    case "allow":
                        Storage.addDiscordId(args[1]);
                        sender.sendMessage(new TextComponent("Allowed Discord ID " + args[1] + " to use bot commands."));
                        break;
                    case "revoke":
                        sender.sendMessage(new TextComponent("Revoked Discord ID " + args[1] + "'s ability to use bot commands."));
                        break;
                    default:
                        break;
                }

            } catch (SQLException error) {
                error.printStackTrace();
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> suggestions = new ArrayList<String>();
        return suggestions;
    }
}
