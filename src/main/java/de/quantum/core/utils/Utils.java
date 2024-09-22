package de.quantum.core.utils;

import de.quantum.Main;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationTeam;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class Utils {

    public static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

    public static final int TOTAL_SHARDS = 1;
    public static final String SUPPORT_GUILD_ID = "1255944449700270201";

    public static final Locale GERMAN = new Locale("de");
    public static final Locale ENGLISH = new Locale("en");

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
        return Main.SECRET_KEY;
    }

    public static String getToken() {
        return Main.TOKEN;
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

    public static <T extends GuildChannel> List<T> getAccessibleChannels(List<T> channels, Member selfMember) {
        return channels.stream()
                .filter(channel -> selfMember.hasPermission(channel, Permission.VIEW_CHANNEL))
                .collect(Collectors.toList());
    }

    public static double calculatePercentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        return ((double) part / total) * 100;
    }

    public static String formatUptime(long uptimeMillis) {
        long days = TimeUnit.MILLISECONDS.toDays(uptimeMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptimeMillis) % 60;
        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }

    public static String getResourcePath(String resource) {
        try {
            return Paths.get(Objects.requireNonNull(Utils.class.getClassLoader().getResource(resource)).toURI()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving resource path for: " + resource, e);
        }
    }
}
