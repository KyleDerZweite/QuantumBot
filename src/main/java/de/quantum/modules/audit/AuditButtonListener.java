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

        switch (action) {
            case "next" -> {
                auditRequest.next();
                event.deferEdit().queue();
                event.reply("Showing next :check_mark:").queue();
            }
            case "previous" -> {
                auditRequest.previous();
                event.reply("Showing previous :check_mark:").queue();
            }
            case "edit-filter" -> {
                event.reply("Attempting to change").queue();
            }
            default -> event.reply("Unknown button Interaction, please contact a bot admin!").queue();
        }

    }

}
