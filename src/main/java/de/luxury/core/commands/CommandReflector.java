package de.luxury.core.commands;

import de.luxury.core.database.DatabaseManager;
import de.luxury.core.events.EventAnnotation;
import de.luxury.core.events.EventInterface;
import de.luxury.core.utils.CheckUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

import java.util.concurrent.ConcurrentHashMap;

@EventAnnotation({UserContextInteractionEvent.class, MessageContextInteractionEvent.class, SlashCommandInteractionEvent.class})
public class CommandReflector implements EventInterface<GenericCommandInteractionEvent> {

    @Override
    public void perform(GenericCommandInteractionEvent event) {
        //if (CommandManager.cmdMap.containsKey(event.getCommandIdLong())) {
        //    CommandInterface<? extends GenericCommandInteractionEvent> cmdController = CommandManager.cmdMap.get(event.getCommandIdLong());
        //    if (event.getMember() == null) {
        //        ErrorManager.replyWithErrorMessage(event,Error.UNKNOWN,"Member is null [" + getClass() + "]");
        //        return;
        //    }
        //    Permission[] requiredPerms = cmdController.getPermissions();
        //    boolean hasPermission = CheckUtils.checkMemberHasPermissions(event.getJDA(), event.getMember(), event.getGuildChannel(), requiredPerms);
//
        //    if (!hasGuildPerms) {
        //        ErrorManager.replyWithErrorMessage(event, Error.NO_PERMISSION,"[Guild]" + cmdController.getPermission().getName());
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
