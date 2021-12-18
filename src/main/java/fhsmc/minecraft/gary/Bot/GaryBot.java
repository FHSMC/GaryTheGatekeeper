package fhsmc.minecraft.gary.Bot;

import fhsmc.minecraft.gary.Config;
import fhsmc.minecraft.gary.Storage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
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
                )/*,
                new SubcommandData("remove", "Remove your Java or Bedrock account from the whitelist").addOptions(
                        new OptionData(OptionType.STRING, "platform", "Which platform would you like to remove the account from?", true)
                                .addChoice("Java", "java")
                                .addChoice("Bedrock", "bedrock")
                )*/
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

                if (event.getSubcommandName().equals("info")) {
                    event.getHook().editOriginalEmbeds(
                            InfoEmbed.fromString("Gary is the server gate system. We are allowing "
                                            + "users to whitelist themselves using this, and are "
                                            + "restricted to one of each platform, Java and Bedrock. "
                                            + "This prevents a mass use of alts, and allows us to restrict whitelisting to "
                                            + " those with a real school email. If you wish to use an alt, or are outside of the school"
                                            + ", then contact staff.")
                                    .addField("Commands", "/whitelist add - Add one of your accounts to the whitelist\n"
                                            // "/whitelist remove - Remove one of your accounts from the whitelist"
                                            , true)
                                    .build()
                    ).queue();
                    return;
                }

                if (!Storage.discordUserInWhitelist(event.getUser().getId())) {
                    if (authFlows.containsKey(event.getUser().getId())) {
                        authFlows.get(event.getUser().getId()).sendFirstEmbed(event);
                    } else {
                        authFlows.put(event.getUser().getId(), new AuthFlow(event));
                    }
                    return;
                }

                switch (event.getSubcommandName()) {
                    case "set":
                        Storage.setIGNFromDiscord(
                                event.getUser().getId(),
                                Objects.requireNonNull(event.getOption("username")).getAsString(),
                                Objects.requireNonNull(event.getOption("platform")).getAsString().equals("bedrock")
                        );
                        event.getHook().editOriginalEmbeds(
                                InfoEmbed.fromString(":white_check_mark: "
                                                        + Objects.requireNonNull(event.getOption("platform")).getAsString()
                                                        + " username set to "
                                                        + Objects.requireNonNull(event.getOption("username")).getAsString())
                                        .build()
                        ).queue();
                    case "remove":
                        break;
                }
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
                Storage.addDiscordId(event.getUser().getId());
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
