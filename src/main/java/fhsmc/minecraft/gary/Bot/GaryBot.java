package fhsmc.minecraft.gary.Bot;

import fhsmc.minecraft.gary.Config;
import fhsmc.minecraft.gary.GaryTheGatekeeper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Collections;

public class GaryBot extends ListenerAdapter {

    private static JDA client;

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
            event.reply("Hello There!").setEphemeral(true).queue();
        }
    }
}
