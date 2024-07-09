package de.quantum.modules.dummybots;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.core.module.ModuleEvent;
import de.quantum.core.utils.EmbedUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@EventAnnotation
@ModuleEvent(moduleName = "DummyBot")
public class DummyBotListener implements EventInterface<ButtonInteractionEvent> {

    @Override
    public void perform(ButtonInteractionEvent event) {
        try {
            assert event.getButton().getId() != null;
            if (!event.getButton().getId().startsWith(DummyBotManager.getInstance().getStatusUtils().getButtonKeyPrefix())) {
                return;
            }
            if (!event.isFromGuild()) {
                event.reply("This button can only be user in a Guild.\nPlease contact a Bot-Admin, this button should not be able to access in a DM!").setEphemeral(true).queue();
                return;
            }
            assert event.getGuild() != null;
            assert event.getMember() != null;
            MessageEmbed.Footer footer = event.getMessage().getEmbeds().get(0).getFooter();
            assert footer != null;
            String footerText = footer.getText();
            assert footerText != null;
            if (!footerText.contains(event.getMember().getEffectiveName())) {
                event.reply("Only the message author can interact with this message!").setEphemeral(true).queue();
                return;
            }
            String buttonId = event.getButton().getId();
            MessageEditBuilder messageEditBuilder = new MessageEditBuilder();

            MessageEmbed messageEmbed = DummyBotManager.getInstance().getStatusUtils().getStatusEmbedPage(buttonId, event.getJDA(), event.getGuild(), event.getMember());
            Button[] buttons = DummyBotManager.getInstance().getStatusUtils().getButtonsForPage(buttonId);
            LinkedList<Button> buttonsForPage = new LinkedList<>(List.of(buttons));
            buttonsForPage.addLast(EmbedUtils.getMessageDeleteButton(event.getMember().getId()));
            messageEditBuilder
                    .setEmbeds(messageEmbed)
                    .setActionRow(buttonsForPage);
            event.editMessage(messageEditBuilder.build()).queue();

        } catch (AssertionError e) {
            log.warn("Caught AssertionError!");
            log.debug(e.getMessage(), e);
            event.reply("Something went wrong").setEphemeral(true).queue();
        }

    }

}
