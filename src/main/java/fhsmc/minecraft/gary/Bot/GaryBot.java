package fhsmc.minecraft.gary.Bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import net.md_5.bungee.config.Configuration;
import reactor.core.publisher.Mono;

import fhsmc.minecraft.gary.GaryTheGatekeeper;

public class GaryBot {

    private static GatewayDiscordClient client;

    private static boolean ready = false;

    private static Configuration config = GaryTheGatekeeper.getConfig();

    public static void run(Configuration config) {
        client = DiscordClientBuilder.create(config.getString("discord.token"))
                .build()
                .login()
                .block();

        client.on(ReadyEvent.class, event -> {
            if (!ready){onReady();}
            ready = true;
            return Mono.empty();
        }).subscribe();
    }

    public static void onReady(){
        client.getChannelById(Snowflake.of(String.valueOf(config.get("discord.channel"))))
                .ofType(GuildMessageChannel.class)
                .flatMap(channel -> channel.createMessage(config.getString("discord.message")))
                .subscribe();
    }

}
