package fhsmc.minecraft.gary;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainCommand extends Command implements TabExecutor {

    public MainCommand() {
        super("gary", "gary.command", "gwhitelist", "gw");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            try {

                switch (args[0]) {
                    // Set a username for a Discord ID
                    case "set":

                        if (args.length != 4) {
                            sender.sendMessage(new TextComponent("Usage: /gary set <discord id> <java|bedrock> <username>"));
                            break;
                        }

                        Storage.setIGNFromDiscord(args[1], args[3], args[2].equals("bedrock"));
                        sender.sendMessage(new TextComponent("Set " + args[2] + " ign to " + args[3] + " for Discord user " + args[1]));
                        break;
                    
                    // Add an anonomous username to the whitelist
                    case "add":

                        if (args.length != 3) {
                            sender.sendMessage(new TextComponent("Usage: /gary add <java|bedrock> <username>"));
                            break;
                        }

                        Storage.addAnonymousIGN(args[2], args[1].equals("bedrock"));
                        sender.sendMessage(new TextComponent("Added username " + args[2] + "(" + args[1] + ") to whitelist."));
                        break;

                    // Remove a username from the whitelist
                    case "remove":

                        if (args.length != 3) {
                            sender.sendMessage(new TextComponent("Usage: /gary remove <username> [java|bedrock]"));
                            break;
                        }
                        
                        Storage.removeEntryFromIGN(args[1], args[2].equals("bedrock"));
                        sender.sendMessage(new TextComponent("Removed username " + args[1] + " from whitelist."));
                        break;

                    // Allow a Discord ID to use commands without auhenticating
                    // or remove a block
                    case "allow":

                        if (args.length != 2) {
                            sender.sendMessage(new TextComponent("Usage: /gary allow <discord id>"));
                            break;
                        }

                        if(Storage.isDiscordIdDisabled(args[1])) {
                            Storage.enableDiscordId(args[1]);
                        } else {
                            Storage.addDiscordId(args[1]);
                        }
                        sender.sendMessage(new TextComponent("Allowed Discord ID " + args[1] + " to use bot commands."));
                        break;
                    
                    // Remove a Discord ID from the whitelist
                    case "revoke":

                        if (args.length != 2) {
                            sender.sendMessage(new TextComponent("Usage: /gary revoke <discord id>"));
                            break;
                        }

                        Storage.removeDiscordId(args[1]);
                        sender.sendMessage(new TextComponent("Revoked Discord ID " + args[1] + "'s ability to use bot commands."));
                        break;
                    
                    // Block a Discord ID from using commands
                    case "block":

                        if (args.length != 2) {
                            sender.sendMessage(new TextComponent("Usage: /gary set <discord id>"));
                            break;
                        }

                        if(!Storage.discordUserInWhitelist(args[1])) {
                            Storage.addDiscordId(args[1]);
                        }
                        Storage.disableDiscordId(args[1]);
                        sender.sendMessage(new TextComponent("Blocked Discord ID " + args[1] + " from using bot commands."));
                        break;
                    
                    case "show":
                        
                        if (args.length == 1) {
                            sender.sendMessage(new TextComponent("Usage: /gary show <id|email|username> <value>"));
                            break;
                        } else {
                            String discord_id;
                            String email;

                            switch (args[1]) {
                                case "id":
                                    discord_id = args[2];
                                    if (!Storage.discordUserInWhitelist(discord_id)) {
                                        sender.sendMessage(new TextComponent("Discord ID " + discord_id + " is not in the whitelist."));
                                        return;
                                    }
                                    email = Storage.getEmailFromId(discord_id);
                                    break;
                                case "email":
                                    email = args[2];
                                    if (!Storage.emailInWhitelist(email)) {
                                        sender.sendMessage(new TextComponent("Email " + email + " not found in whitelist."));
                                        return;
                                    }
                                    discord_id = Storage.getIdFromEmail(email);
                                    break;
                                case "java":
                                    discord_id = Storage.getDiscordFromIGN(args[2], false);
                                    if (discord_id == null) {
                                        sender.sendMessage(new TextComponent(args[2] + " not found in whitelist."));
                                        return;
                                    }
                                    email = Storage.getEmailFromId(discord_id);
                                    break;
                                case "bedrock":
                                    discord_id = Storage.getDiscordFromIGN(args[2], true);
                                    if (discord_id == null) {
                                        sender.sendMessage(new TextComponent(args[2] + " not found in whitelist."));
                                        return;
                                    }
                                    email = Storage.getEmailFromId(discord_id);
                                    break;
                                default:
                                    sender.sendMessage(new TextComponent("Usage: /gary show <id|email|java|bedrock> <value>"));
                                    return;
                            }

                            sender.sendMessage(
                                new TextComponent("---\nDiscord ID: " + discord_id
                                                + "\nEmail: " + email
                                                + "\nJava IGN: " + Storage.getIGNFromDiscord(discord_id, false)
                                                + "\nBedrock IGN: " + Storage.getIGNFromDiscord(discord_id, true)
                                                + "\n---"
                                                )
                            );
                        }

                        break;

                    default:
                        sender.sendMessage(new TextComponent("§4I don't recognize that command.§r"));
                        break;
                }

            } catch (SQLException error) {
                error.printStackTrace();
            }
        } else {
            sender.sendMessage(new TextComponent("§4Usage:§r /gary <set|add|remove|allow|revoke|block|show>\nType a command to see it's syntax."));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> suggestions = new ArrayList<String>();
        return suggestions;
    }
}
