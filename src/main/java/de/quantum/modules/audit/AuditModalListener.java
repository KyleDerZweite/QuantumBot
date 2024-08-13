package de.quantum.modules.audit;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

@EventAnnotation
public class AuditModalListener implements EventInterface<ModalInteractionEvent> {
    @Override
    public void perform(ModalInteractionEvent event) {
        if (!event.getModalId().contains(AuditHandler.AUDIT_BUTTON_ID) || !event.getModalId().contains("modal")) {
            return;
        }

        String[] buttonArgs = event.getModalId().split("_");

        String requestId = buttonArgs[1];

        AuditRequest auditRequest = AuditHandler.getInstance().getActiveAuditRequests().get(requestId);

        event.getValues().forEach(modalMapping -> {
            if (!modalMapping.getAsString().isEmpty()) {
                auditRequest.changeFilter(modalMapping.getId(), modalMapping.getAsString());
            }
        });

        event.deferEdit().queue();
    }
}