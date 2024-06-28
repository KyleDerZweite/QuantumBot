package de.luxury.core.commands;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandManager {
    private static volatile CommandManager INSTANCE = null;

    public final CopyOnWriteArrayList<?> commandList;

    private CommandManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + CommandManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
        commandList = new CopyOnWriteArrayList<>();
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

    /*
    @Override
    public void init() {
        for (Class<?> clazz : new Reflections(de.luxury.interfaces.I_Strings.REFLECTION_COMMAND).getTypesAnnotatedWith(A_Command.class)) {
            try {
                de.luxury.interfaces.I_Command<? extends GenericCommandInteractionEvent> i_command = (de.luxury.interfaces.I_Command<? extends GenericCommandInteractionEvent>) clazz.getDeclaredConstructors()[0].newInstance();
                de.luxury.entities.E_Guild.GuildType requiredGuildType = clazz.getAnnotation(A_Command.class).requiredGuildType();
                switch (requiredGuildType) {
                    case UNVERIFIED -> unverifiedCommandList.add(i_command);
                    case VERIFIED -> verifiedCommandList.add(i_command);
                    case NYA_GUILD -> nyaCommandList.add(i_command);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();

            }
        }
    }

    public void upsertCommands(de.luxury.entities.E_Guild guild) {
        unverifiedCommandList.forEach(iCommand -> upsertCommand(guild, iCommand));
        if (guild.getGuildType().getId() >= de.luxury.entities.E_Guild.GuildType.VERIFIED.getId()) {
            verifiedCommandList.forEach(iCommand -> upsertCommand(guild, iCommand));
        }
        if (guild.getGuildType().getId() >= de.luxury.entities.E_Guild.GuildType.NYA_GUILD.getId()) {
            nyaCommandList.forEach(iCommand -> upsertCommand(guild, iCommand));
        }
        deleteUnusedCommands(guild);
    }

    private void upsertCommand(de.luxury.entities.E_Guild eGuild, de.luxury.interfaces.I_Command<? extends GenericCommandInteractionEvent> iCommand) {
        net.dv8tion.jda.api.interactions.commands.Command command = eGuild.getGuild().upsertCommand(iCommand.getCommandData()).submit().join();
        de.luxury.entities.E_Command eCommand = new de.luxury.entities.E_Command(iCommand, command, eGuild.getGuildType());
        ResultSet rs = H_DataBaseHandler.getInstance().getResultSetCommand(command.getIdLong());
        try {
            if (rs.next()) {
                eCommand.fromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        eGuild.getCommandHashMap().put(command.getIdLong(), eCommand);
    }

    private void deleteUnusedCommands(de.luxury.entities.E_Guild eGuild) {
        List<net.dv8tion.jda.api.interactions.commands.Command> commands = eGuild.getGuild().retrieveCommands().submit().join();
        for (Command command : commands) {
            if (!eGuild.getCommandHashMap().containsKey(command.getIdLong())) {
                command.delete().queue();
            }
        }
    }
     */


}