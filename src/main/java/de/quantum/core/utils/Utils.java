package de.quantum.core.utils;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class Utils {

    public static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

    public static final int TOTAL_SHARDS = 2;

    public static final String SUPPORT_GUILD_ID = "1255944449700270201";

    /*
    public static void registerEventListener(JDA jda) {
        jda.setEventManager(new AnnotatedEventManager());
        for (Class<?> clazz : new Reflections(I_Strings.REFLECTION_EVENTS).getTypesAnnotatedWith(A_Event.class)) {
            try {
                Object listener = clazz.getDeclaredConstructors()[0].newInstance();
                jda.getEventManager().register(listener);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerCommands(ConcurrentHashMap<Long, E_Guild> guildConcurrentHashMap) {
        H_CommandHandler handler = H_CommandHandler.getInstance();
        guildConcurrentHashMap.forEach((aLong, eGuild) -> {
            new Thread(() -> handler.upsertCommands(eGuild)).start();
        });
    }
     */

    public static String getEncryptKey() {
        return System.getenv("SECRET_KEY");
    }

    public static String getToken() {
        return System.getenv("TOKEN");
    }


    public static void throwNewCommandException(InteractionHook hook, String msg) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#8b0000"));
        embedBuilder.setTitle("⚠ Command execution error");
        embedBuilder.setDescription(msg);
        embedBuilder.addField("Support Server", "For further support open a Ticket on the Support Server.\n" +
                "~~Use '/support' to get to the Support server~~", false);
        hook.editOriginalEmbeds(embedBuilder.build()).queue();
    }

    public static Date findPrevDay(Date date) {
        return new Date(date.getTime() - MILLIS_IN_A_DAY);
    }

    public static Date findDateInPast(Date date, int days) {
        return new Date(date.getTime() - (MILLIS_IN_A_DAY * days));
    }

    public static long getCurrentMillis() {
        return new Date().toInstant().toEpochMilli();
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getJdaShardGuildCountString(JDA jda) {
        String shardGuildCountString = jda.getSelfUser().getName();
        shardGuildCountString += "(Shard: " + jda.getShardInfo().getShardId() + ", ";
        shardGuildCountString += "GuildCount: " + jda.getGuilds().size() + ")";
        return shardGuildCountString;
    }

    public static ApplicationTeam getTeamInfo(JDA jda) {
        return jda.retrieveApplicationInfo().complete().getTeam();
    }

}
