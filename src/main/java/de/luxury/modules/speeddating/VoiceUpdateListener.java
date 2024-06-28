package de.luxury.modules.speeddating;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;


public class VoiceUpdateListener {

    @SubscribeEvent
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        // pass
    }
}
