package fhsmc.minecraft.gary.Bot;

import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import reactor.core.publisher.Mono;

public class Buttons {

    public static Mono<Void> handleButtonClick(ButtonInteractionEvent event) {
        switch (event.getCustomId()) {
            case "start_1":
                return event.reply("Yes, you click button").withEphemeral(true);
        }
        return Mono.empty();
    }
}
