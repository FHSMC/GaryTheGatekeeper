package fhsmc.minecraft.gary.Bot;

import fhsmc.minecraft.gary.Config;
import fhsmc.minecraft.gary.Storage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

public class GaryBot extends ListenerAdapter {

    private static JDA client;

    private static HashMap<String, AuthFlow> authFlows = new HashMap<String, AuthFlow>();

    public static void run() throws LoginException {
        JDABuilder botBuilder = JDABuilder.createLight(
                Config.getString("discord.token"),
                Collections.emptyList()
        );

        botBuilder.addEventListeners(new GaryBot());
        botBuilder.setActivity(Activity.playing("around with the whitelist"));

        client = botBuilder.build();
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getButton().getId().equals("start_1")) {
            try {
                if (!Storage.discordUserInWhitelist(event.getUser().getId())){
                    if (authFlows.containsKey(event.getUser().getId())) {
                        event.reply("Hey! You've already started the process! If you dismissed the message, wait a few minutes and try again.")
                                .setEphemeral(true)
                                .queue();
                    } else {
                        authFlows.put(event.getUser().getId(), new AuthFlow(event));
                    }
                } else {
                    whitelistProcess(event);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void authFlowComplete(ButtonClickEvent event, boolean authorized, String email){
        authFlows.remove(event.getUser().getId());
        if (authorized) {
            try {
                Storage.addDiscordId(event.getUser().getId());
            } catch (SQLException e) {
                event.getHook().sendMessage("There was an issue storing some information. Contact staff for help.").setEphemeral(true).queue();
                e.printStackTrace();
            }
            event.getHook().sendMessage("You have been authorized").setEphemeral(true).queue();
        } else {
            event.getHook().sendMessage("The email `" + email + "` is not a valid school email. If you believe this to be an error, please contact staff for help.").setEphemeral(true).queue();
        }
    }

    public static void whitelistProcess(ButtonClickEvent event) {
        event.reply("Heyyy, you're approved!").setEphemeral(true).queue();
    }
}
