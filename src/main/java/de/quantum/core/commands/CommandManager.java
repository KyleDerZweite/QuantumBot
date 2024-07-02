package de.quantum.core.commands;

import de.quantum.core.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CommandManager {

    private static volatile CommandManager INSTANCE = null;

    @Getter
    private final ConcurrentHashMap<String, CommandInterface<?>> commandHashMap;

    private CommandManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + CommandManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
        this.commandHashMap = new ConcurrentHashMap<>();
    }

    public static CommandManager getInstance() {
        if (INSTANCE == null) {
            synchronized (CommandManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommandManager();
                }
            }
        }
        return INSTANCE;
    }

    public void registerCommands(JDA jda) {
        for (Class<?> clazz : new Reflections().getTypesAnnotatedWith(CommandAnnotation.class)) {
            try {
                CommandInterface<? extends GenericCommandInteractionEvent> commandInterface = (CommandInterface<? extends GenericCommandInteractionEvent>) clazz.getDeclaredConstructors()[0].newInstance();
                String commandName = commandInterface.getCommandData().getName();
                if (commandInterface.getType().isSupportGuildOnly()) {
                    Guild guild = jda.getGuildById(Utils.SUPPORT_GUILD_ID);
                    if (guild == null) {
                        continue;
                    }
                    Command command = guild.upsertCommand(commandInterface.getCommandData()).submit().join();
                    commandHashMap.put(command.getId(), commandInterface);
                } else {
                    Command command = jda.upsertCommand(commandInterface.getCommandData()).submit().join();
                    commandHashMap.put(command.getId(), commandInterface);
                }
                log.debug("{} registered Command: {}", Utils.getJdaShardGuildCountString(jda), commandName);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void deleteUnusedCommands(JDA jda) {
        List<Command> commands = jda.retrieveCommands().submit().join();
        for (Command command : commands) {
            if (!commandHashMap.containsKey(command.getId())) {
                log.debug("{} delete Command: {}", Utils.getJdaShardGuildCountString(jda), command.getName());
                command.delete().queue();
            }
        }
    }

}