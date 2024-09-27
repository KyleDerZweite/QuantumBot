package de.quantum.core.commands;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Slf4j
@EventAnnotation({UserContextInteractionEvent.class, MessageContextInteractionEvent.class, SlashCommandInteractionEvent.class})
public class CommandReflector implements EventInterface<GenericCommandInteractionEvent> {

    @Override
    public void perform(GenericCommandInteractionEvent event) {
        if (event.isAcknowledged()) {
            log.warn("Command is already acknowledged!\nSource: {}\nCommand: {}", Utils.getJdaShardGuildCountString(event.getJDA()), event.getFullCommandName());
            return;
        }
        event.deferReply(true).submit().join();
        if (CommandManager.getInstance().getCommandHashMap().containsKey(event.getCommandId())) {
            CommandInterface<? extends GenericCommandInteractionEvent> cmdController = CommandManager.getInstance().getCommandHashMap().get(event.getCommandId());

            if (!cmdController.getType().isDmEnabled() && !event.isFromGuild()) {
                event.getHook().editOriginal("This command cannot be run in the direct messages!").queue();
                return;
            }

            Permission[] requiredPerms = cmdController.getPermissions();
            ArrayList<Permission> missingPermissions = CheckUtils.getMissingPermissions(event.getJDA(), event.getMember(), event.getGuildChannel(), requiredPerms);

            if (missingPermissions != null && !missingPermissions.isEmpty()) {
                event.getHook().editOriginal("You do not have permission to use this command!").queue();
                return;
            }

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


            try {
                Method m = cmdController.getClass().getDeclaredMethod("perform", GenericCommandInteractionEvent.class);
                m.invoke(cmdController, event);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            event.reply("This command does not exist!").setEphemeral(true).queue();
            //TODO maybe add an option to show other error message if its disabled or more specific fail cases...
        }
    }
}
