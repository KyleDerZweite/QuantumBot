package de.quantum.modules.speeddating;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.core.module.ModuleEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

@Slf4j
@ModuleEvent(moduleName = "SpeedDating")
@EventAnnotation
public class SpeedDatingListener implements EventInterface<GuildVoiceUpdateEvent> {

    @Override
    public void perform(GuildVoiceUpdateEvent event) {

    }
}
