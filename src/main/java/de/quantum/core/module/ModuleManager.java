package de.quantum.core.module;

import de.quantum.core.commands.CommandInterface;
import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ModuleManager {

    private static volatile ModuleManager INSTANCE = null;

    private static final long ALLOWED_PERMISSION_OFFSET = 590435596631761L;

    @Getter
    private final ConcurrentHashMap<String, ModuleRecord> moduleHashMap;

    private ModuleManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + ModuleManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
        this.moduleHashMap = new ConcurrentHashMap<>();
    }

    public static ModuleManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ModuleManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ModuleManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init() {
        loadModules();
        loadModuleCommands();
        loadModuleEvents();
    }


    private void loadModules() {
        for (Class<?> clazz : new Reflections().getTypesAnnotatedWith(ModuleAnnotation.class)) {
            String moduleName = clazz.getAnnotation(ModuleAnnotation.class).moduleName();
            ModuleRecord moduleRecord = getModuleRecord(clazz, moduleName);
            moduleHashMap.put(moduleName, moduleRecord);
        }
    }

    private void loadModuleCommands() {
        for (Class<?> clazz : new Reflections().getTypesAnnotatedWith(ModuleCommand.class)) {
            String moduleName = clazz.getAnnotation(ModuleCommand.class).moduleName();
            ModuleRecord moduleRecord = moduleHashMap.get(moduleName);

            try {
                CommandInterface<? extends GenericCommandInteractionEvent> commandInterface = (CommandInterface<? extends GenericCommandInteractionEvent>) clazz.getDeclaredConstructors()[0].newInstance();
                CommandDataImpl commandData = commandInterface.getCommandData();
                String commandName = commandData.getName();

                if (commandData.getSubcommands().size() > 1) {
                    commandData.getSubcommands().forEach((subcommand) -> moduleRecord.addCommand(commandName + " " + subcommand.getName()));
                } else {
                    moduleRecord.addCommand(commandName);
                }

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void loadModuleEvents() {
        for (Class<?> clazz : new Reflections().getTypesAnnotatedWith(ModuleEvent.class)) {
            String moduleName = clazz.getAnnotation(ModuleEvent.class).moduleName();
            ModuleRecord moduleRecord = moduleHashMap.get(moduleName);

            try {
                EventInterface<? extends GenericEvent> eventInterface = (EventInterface<? extends GenericEvent>) clazz.getDeclaredConstructors()[0].newInstance();
                ParameterizedType parameterizedType = (ParameterizedType) eventInterface.getClass().getGenericInterfaces()[0];

                Class<?> eventType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                moduleRecord.addEvent(eventType.getName());
                Class<?>[] eventTypes = clazz.getAnnotation(EventAnnotation.class).value();
                if (eventTypes.length == 0) {
                    continue;
                }
                for (Class<?> type : eventTypes) {
                    moduleRecord.addEvent(type.getName());
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static @NotNull ModuleRecord getModuleRecord(Class<?> clazz, String moduleName) {
        String moduleDescription = clazz.getAnnotation(ModuleAnnotation.class).moduleDescription();
        String moduleAuthorName = clazz.getAnnotation(ModuleAnnotation.class).moduleAuthorName();
        String moduleAuthorID = clazz.getAnnotation(ModuleAnnotation.class).moduleAuthorID();
        String moduleVersion = clazz.getAnnotation(ModuleAnnotation.class).moduleVersion();
        return new ModuleRecord(moduleName, moduleDescription, moduleAuthorName, moduleAuthorID, moduleVersion);
    }


}