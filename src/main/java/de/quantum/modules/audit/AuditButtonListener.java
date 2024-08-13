package de.quantum.modules.audit;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@EventAnnotation
public class AuditButtonListener implements EventInterface<ButtonInteractionEvent> {

    @Override
    public void perform(ButtonInteractionEvent event) {
        if (event.getButton().getId() == null || !event.getButton().getId().contains(AuditHandler.AUDIT_BUTTON_ID)) {
            return;
        }

        String[] buttonArgs = event.getButton().getId().split("_");

        String requestId = buttonArgs[1];
        String action = buttonArgs[2];

        AuditRequest auditRequest = AuditHandler.getInstance().getActiveAuditRequests().get(requestId);
        if (auditRequest == null) {
            event.getMessage().delete().queue();
            event.reply("Message timeout, please request a new audit request.").setEphemeral(true).queue();
            return;
        }

        switch (action) {
            case "next" -> {
                auditRequest.next();
                event.deferEdit().queue();
            }
            case "previous" -> {
                auditRequest.previous();
                event.deferEdit().queue();
            }
            case "edit-filter" -> event.replyModal(auditRequest.getChangeFilterModal()).queue();
            default ->
                    event.reply("Unknown button Interaction, please contact a bot admin!").setEphemeral(true).queue();

        }
    }

}
