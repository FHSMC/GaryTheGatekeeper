package fhsmc.minecraft.gary.Bot;

import fhsmc.minecraft.gary.GaryTheGatekeeper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class GaryBot {

    private static JDA client;

    public static void run() throws LoginException {
        JDABuilder botBuilder = JDABuilder.createDefault(GaryTheGatekeeper.getConfig().getString("discord.token"));

        botBuilder.setActivity(Activity.playing("around with the whitelist"));

        client = botBuilder.build();
    }
}
