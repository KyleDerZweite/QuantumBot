package de.quantum.core.commands;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

@EventAnnotation({UserContextInteractionEvent.class, MessageContextInteractionEvent.class, SlashCommandInteractionEvent.class})
public class CommandReflector implements EventInterface<GenericCommandInteractionEvent> {

    @Override
    public void perform(GenericCommandInteractionEvent event) {
        //if (CommandManager.getInstance().getCommandHashMap().containsKey(event.getCommandId())) {
        //    CommandInterface<? extends GenericCommandInteractionEvent> cmdController = CommandManager.getInstance().getCommandHashMap().get(event.getCommandId());
        //    if (event.getMember() == null) {
        //        return;
        //    }
        //    Permission[] requiredPerms = cmdController.getPermissions();
        //    ArrayList<Permission> missingPermissions = CheckUtils.checkMemberPermissions(event.getJDA(), event.getMember(), event.getGuildChannel(), requiredPerms);
//
        //    if (!missingPermissions.isEmpty()) {
        //        ErrorManager.replyInteraction(event.getHook(), "You do not have permission to use this command.");
        //        return;
        //    }
        //    ConcurrentHashMap<Long, ConcurrentHashMap<String,Long>> cmdCooldownMap = CommandManager.cmdCooldownMap;
        //    ConcurrentHashMap<String,Long> memberCooldownMap = new ConcurrentHashMap<>();
        //    long currentEpochSeconds = new Date().toInstant().getEpochSecond();
        //    if (cmdCooldownMap.containsKey(event.getCommandIdLong())) {
        //        memberCooldownMap = cmdCooldownMap.get(event.getCommandIdLong());
        //        if (memberCooldownMap.containsKey(event.getMember().getId())) {
        //            long cooldownSeconds = DatabaseManager.getInstance().getCommandCooldown(event.getGuild(), event.getCommandId());
        //            long lastUsageEpochSeconds = memberCooldownMap.get(event.getMember().getId());
        //            if (currentEpochSeconds < (lastUsageEpochSeconds + cooldownSeconds)) {
        //                long neededSeconds = (lastUsageEpochSeconds + cooldownSeconds) - currentEpochSeconds;
        //                ErrorManager.replyWithErrorMessage(event,Error.ON_COOLDOWN,"" + neededSeconds);
        //                return;
        //            }
        //        }
        //    }
        //    memberCooldownMap.put(event.getMember().getId(),currentEpochSeconds);
        //    cmdCooldownMap.put(event.getCommandIdLong(),memberCooldownMap);
        //    try {
        //        Method m = cmdController.getClass().getDeclaredMethod("perform", GenericCommandInteractionEvent.class);
        //        m.invoke(cmdController, event);
        //    } catch (Exception e) {
        //        System.out.println("-----" + getClass() + "-----");
        //        e.printStackTrace();
        //    }
        //} else {
        //    if (event.getGuild() == null) {
        //        ErrorManager.replyWithErrorMessage(event,Error.COMMAND_NOT_FOUND,"",true);
        //        return;
        //    }
        //    ErrorManager.sendErrorLogMessage(event.getGuild(),event.getMember(), event.getChannel(), Error.COMMAND_NOT_FOUND,"" + event.getName());
        //}
    }

}