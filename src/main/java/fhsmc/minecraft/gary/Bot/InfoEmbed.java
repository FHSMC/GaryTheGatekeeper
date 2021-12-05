package fhsmc.minecraft.gary.Bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InfoEmbed {

    public static EmbedBuilder fromString(String text) {
        return new EmbedBuilder()
                .setDescription(text)
                .setColor(0x2F3136);
    }
}
