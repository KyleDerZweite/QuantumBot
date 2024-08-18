package de.quantum.modules.audit;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.core.module.ModuleEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

@EventAnnotation
@ModuleEvent(moduleName = "Audit")
public class AuditModalListener implements EventInterface<ModalInteractionEvent> {
    @Override
    public void perform(ModalInteractionEvent event) {
        if (!event.getModalId().contains(AuditManager.AUDIT_BUTTON_ID) || !event.getModalId().contains("modal")) {
            return;
        }

        String[] buttonArgs = event.getModalId().split("_");

        String requestId = buttonArgs[1];

        AuditRequest auditRequest = AuditManager.getInstance().getActiveAuditRequests().get(requestId);

        event.getValues().forEach(modalMapping -> {
            if (!modalMapping.getAsString().isEmpty()) {
                auditRequest.changeFilter(modalMapping.getId(), modalMapping.getAsString());
            }
        });

        event.deferEdit().queue();
    }
}
