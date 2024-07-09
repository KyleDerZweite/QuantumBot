package de.quantum.listener;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Objects;

@EventAnnotation
public class ButtonListener implements EventInterface<ButtonInteractionEvent> {
    @Override
    public void perform(ButtonInteractionEvent event) {
        if (Objects.requireNonNull(event.getButton().getId()).startsWith("delete")) {
            String[] buttonArgs = event.getButton().getId().split("_");
            if (buttonArgs.length <= 1) {
                return;
            }
            if (!buttonArgs[1].equals(Objects.requireNonNull(event.getMember()).getId())) {
                event.reply("You are not allowed to delete this message!").queue();
                return;
            }
            event.reply("Deleting Message...").queue();
            event.getMessage().delete().queue();
        }
    }
}