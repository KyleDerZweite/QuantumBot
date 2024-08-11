package de.quantum.core.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;

public class EmbedUtils {

    private static EmbedBuilder getEmbedBuilder() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTimestamp(Instant.now());
        eb.setFooter("☆ Quantum ☆", "https://cdn.discordapp.com/avatars/973526417595846696/35c5751aa0a8a11f984f44baefa84e53.png?size=4096");
        return eb;
    }

    public static EmbedBuilder getStandardEmbedBuilder() {
        return getEmbedBuilder();
    }

    public static EmbedBuilder getStandardEmbedBuilder(Color color) {
        return getEmbedBuilder().setColor(color);
    }


    public static Button getMessageDeleteButton(String authorId) {
        return Button.danger("delete_%s".formatted(authorId),"\u2716").asEnabled();
    }

}
