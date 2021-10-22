package fhsmc.minecraft.gary.Bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.LayoutComponent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.MessageEditSpec;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import reactor.core.publisher.Mono;

import fhsmc.minecraft.gary.GaryTheGatekeeper;

import java.io.IOException;

public class GaryBot {

    private static GatewayDiscordClient client;

    private static boolean ready = false;

    private static Configuration config = GaryTheGatekeeper.getConfig();

    /*
    * "Async programming in Java? Can't be that hard"
    * We love java!!!
    * We love java!!!
    * We love java!!!
    * We love java!!!
    */

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
                .flatMap(channel -> {

                    Mono<Message> startMessage;

                    ActionRow startButton = ActionRow.of(Button.primary("start_1", "Start"));

                    if (config.getInt("discord.message_id") != 0){
                        startMessage = channel.getMessageById(
                                Snowflake.of(String.valueOf(config.get("discord.message_id")))
                        ).flatMap(msg -> {
                            return msg.edit(
                                    MessageEditSpec.builder()
                                            .contentOrNull(config.getString("discord.message"))
                                            .build()
                            );
                        });
                    } else {
                        startMessage = channel.createMessage(
                                MessageCreateSpec.builder()
                                        .content(config.getString("discord.message"))
                                        .addComponent(startButton)
                                        .build()
                        );
                    }

                    Mono<Void> buttonListener = client.on(ButtonInteractionEvent.class, Buttons::handleButtonClick).then();

                    return startMessage.flatMap(msg -> {
                        if (config.getInt("discord.message_id") == 0){
                            config.set("discord.message_id", msg.getId().asBigInteger());
                            try {
                                GaryTheGatekeeper.saveConfig();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return Mono.just(msg);
                    }).then(buttonListener);
                }).subscribe();
    }

    public static GatewayDiscordClient getClient() {
        return client;
    }

}
