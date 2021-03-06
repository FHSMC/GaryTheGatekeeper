package fhsmc.minecraft.gary.Bot;

import fhsmc.minecraft.gary.Config;
import fhsmc.minecraft.gary.Storage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class GaryBot extends ListenerAdapter {

    private static JDA client;

    private static final HashMap<String, AuthFlow> authFlows = new HashMap<>();

    private static final String usernameRegex = "^[a-zA-Z0-9_]{2,16}$";

    public static void run() throws LoginException, InterruptedException {
        JDABuilder botBuilder = JDABuilder.createLight(
                Config.getString("discord.token"),
                Collections.emptyList()
        );

        botBuilder.addEventListeners(new GaryBot());
        botBuilder.setActivity(Activity.playing("around with the whitelist"));

        client = botBuilder.build().awaitReady();

        CommandData command = new CommandData("whitelist", "Access the server whitelist");
        command.addSubcommands(
                new SubcommandData("info", "Show info about the whitelist"),
                new SubcommandData("set", "Set your Java or Bedrock username in the whitelist").addOptions(
                        new OptionData(OptionType.STRING, "platform", "Is this a Java or Bedrock account?", true)
                                .addChoice("Java", "java")
                                .addChoice("Bedrock", "bedrock"),
                        new OptionData(OptionType.STRING, "username", "The username of the account", true)
                ),
                new SubcommandData("remove", "Remove your Java or Bedrock account from the whitelist").addOptions(
                        new OptionData(OptionType.STRING, "platform", "Which platform would you like to remove the account from?", true)
                                .addChoice("Java", "java")
                                .addChoice("Bedrock", "bedrock")
                )
        );

        Guild guild = client.getGuildById(String.valueOf(Config.get("discord.guild")));
        if (guild != null){
            guild.updateCommands().addCommands(command).queue();
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        event.deferReply().setEphemeral(true).queue();
        try {
            if (event.getName().equals("whitelist")) {
                
                MessageEmbed response;

                // Info command

                if (event.getSubcommandName().equals("info")) {

                    EmbedBuilder res;

                    res = InfoEmbed.fromString("Gary is the server gate system. We are allowing "
                                                + "users to whitelist themselves using this, and are "
                                                + "restricted to one of each platform, Java and Bedrock. "
                                                + "This prevents a mass use of alts, and allows us to restrict whitelisting to "
                                                + " those with a real school email. If you wish to use an alt, or are outside of the school"
                                                + ", then contact staff."
                                                + "\n\n**Commands**\n```ansi\n\u001b[0;37m/whitelist set\u001b[0m - Set one of your account's usernames on the whitelist\n"
                                                + "\u001b[0;37m/whitelist remove\u001b[0m - Remove one of your accounts from the whitelist\n```"
                                            );
                    
                    
                    if (Storage.discordUserInWhitelist(event.getUser().getId())) {
                        res.getDescriptionBuilder()
                            .append("\n**Your info**\n"
                                    + "```ansi\n\u001b[0;37mJava Username:\u001b[0m " + Storage.getIGNFromDiscord(event.getUser().getId(), false)
                                    + "\n\u001b[0;37mBedrock Username:\u001b[0m " + Storage.getIGNFromDiscord(event.getUser().getId(), true)
                                    + "\n\u001b[0;37mEmail:\u001b[0m " + Storage.getEmailFromId(event.getUser().getId())
                                    + "\n```");
                    }

                    event.getHook().editOriginalEmbeds(res.build()).queue();
                    return;
                }
                
                // Checking if user is in the whitelist for the remaining commands
                if (!Storage.discordUserInWhitelist(event.getUser().getId())) {
                    if (authFlows.containsKey(event.getUser().getId())) {
                        // Show an embed displaying user's current auth flow
                        authFlows.get(event.getUser().getId()).sendFirstEmbed(event);
                    } else {
                        // Start the auth flow
                        authFlows.put(event.getUser().getId(), new AuthFlow(event));
                    }
                    return;
                }
                
                // Rest of the commands

                String platform = Objects.requireNonNull(event.getOption("platform")).getAsString();
                boolean isBedrock = platform.equals("bedrock");

                switch (event.getSubcommandName()) {

                    case "set":

                        // Check if user is on cooldown
                        if (isBedrock && Storage.isBedrockOnCooldown(event.getUser().getId())) {
                            int cooldownSeconds = (int) (Storage.getBedrockCooldown(event.getUser().getId()) / 1000);
                            response = InfoEmbed.fromString(":timer: You are on cooldown for changing your Bedrock username! Your cooldown will end <t:" + cooldownSeconds + ":R>")
                                    .build();
                            break;
                        } else if (!isBedrock && Storage.isJavaOnCooldown(event.getUser().getId())) {
                            int cooldownSeconds = (int) (Storage.getJavaCooldown(event.getUser().getId()) / 1000);
                            response = InfoEmbed.fromString(":timer: You are on cooldown for changing your Java username! Your cooldown will end <t:" + cooldownSeconds + ":R>")
                                    .build();
                            break;
                        }
                        
                        if (Storage.isDiscordIdDisabled(event.getUser().getId())) {
                            response = InfoEmbed.fromString(":x: Your account is disallowed from using Gary.")
                                    .build();
                            break;
                        }

                        if (event.getOption("username").getAsString().matches(usernameRegex)) {

                            if (Storage.isPlayerIGNWhitelisted(event.getOption("username").getAsString(), isBedrock)) {
                                response = InfoEmbed.fromString(":x: This username is already whitelisted!")
                                        .build();
                                break;
                            }

                            Storage.setIGNFromDiscord(
                                    event.getUser().getId(),
                                    Objects.requireNonNull(event.getOption("username")).getAsString(),
                                    isBedrock
                            );

                            Storage.removeUUIDFromDiscord(
                                    event.getUser().getId(),
                                    isBedrock
                            );

                            response = InfoEmbed.fromString(":white_check_mark: "
                                        + platform
                                        + " username set to "
                                        + Objects.requireNonNull(event.getOption("username")).getAsString())
                                .build();

                            // set cooldown
                            long cooldown = System.currentTimeMillis() + Config.getInt("discord.cooldown");
                            if (isBedrock){
                                Storage.setBedrockCooldown(event.getUser().getId(), cooldown);
                            } else {
                                Storage.setJavaCooldown(event.getUser().getId(), cooldown);
                            }

                        } else {
                            response = InfoEmbed.fromString(":warning: Invalid username.").build();
                        }
                        break;

                    case "remove":
                        
                        Storage.removeEntryFromDiscord(event.getUser().getId(), isBedrock);

                        response = InfoEmbed.fromString(":white_check_mark: Removed your " + platform + " account from the whitelist.").build();
                        break;

                    default:
                        response = InfoEmbed.fromString("**Unknown command**\n How did you even manage this?").build();
                        break;
                }

                event.getHook().editOriginalEmbeds(response).queue();
            }

        } catch(SQLException e){
            event.getHook().editOriginalEmbeds(
                    InfoEmbed.fromString(":warning: There was an error in running that command. Please contact staff for help.")
                            .build()
            ).queue();
            e.printStackTrace();
        }
    }

    public static void authFlowTimeout(SlashCommandEvent event) {
        authFlows.remove(event.getUser().getId());
        event.getHook().editOriginalEmbeds(
                InfoEmbed.fromString(":timer: Timed out. Please try again.").build()
        ).queue();
    }

    public static void authFlowComplete(SlashCommandEvent event, boolean authorized, String email){
        authFlows.remove(event.getUser().getId());
        if (authorized) {

            try {

                if (Storage.emailInWhitelist(email)) {
                    event.getHook().editOriginalEmbeds(
                            InfoEmbed.fromString(":warning: That email is already in use by another Discord account.")
                                    .build()
                    ).queue();
                    return;
                }

                Storage.addDiscordId(event.getUser().getId(), email);

            } catch (SQLException e) {
                event.getHook().editOriginalEmbeds(
                        InfoEmbed.fromString(":warning: There was an issue storing some information. Contact staff for help.")
                                .build()
                ).queue();
                e.printStackTrace();
            }

            event.getHook().editOriginalEmbeds(
                    InfoEmbed.fromString(":white_check_mark: You have been authorized. You can now use the whitelist commands")
                    .build()
            ).queue();

        } else {

            event.getHook().editOriginalEmbeds(
                    InfoEmbed.fromString(":no_entry: The email `" + email + "` is not a valid school email. If you believe this to be an error, please contact staff for help.")
                            .build()
            ).queue();

        }
    }

}
