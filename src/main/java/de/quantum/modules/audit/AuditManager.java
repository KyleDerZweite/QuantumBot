package de.quantum.modules.audit;

import de.quantum.core.entities.EmptyMentionable;
import de.quantum.core.entities.TimeoutMap;
import de.quantum.core.module.ModuleAnnotation;
import de.quantum.core.shutdown.ShutdownAnnotation;
import de.quantum.core.shutdown.ShutdownInterface;
import de.quantum.core.utils.Secret;
import de.quantum.core.utils.StringUtils;
import de.quantum.modules.audit.entries.AuditEntry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.TargetType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ShutdownAnnotation
@ModuleAnnotation(
        moduleName = "Audit",
        moduleDescription = "Audit system for a better AuditLogs",
        moduleVersion = "v0.0.1",
        moduleAuthorName = "kylederzweite",
        moduleAuthorID = "378542649579143188"
)
public class AuditManager implements ShutdownInterface {

    public static final String AUDIT_BUTTON_ID = "audit";

    private static volatile AuditManager INSTANCE = null;

    private Long qidLogCounter;

    /**
     * A concurrent cache for storing audit log entries.
     * The outer ConcurrentHashMap uses the guild ID (String) as the key,
     * and maps to another ConcurrentHashMap that contains the audit logs for that guild.
     * <p>
     * The inner ConcurrentHashMap uses the hex ID (String) as the key, which is the hexadecimal
     * representation of the quantum log ID, and maps to a LogEntry object.
     * <p>
     * This structure allows efficient storage and retrieval of audit logs by their guild and log ID.
     */
    @Getter
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AuditEntry>> guildAuditLogCache;

    @Getter
    private final TimeoutMap<String, AuditRequest> activeAuditRequests;

    private AuditManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + AuditManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
        guildAuditLogCache = AuditDatabaseManager.getAuditCache();
        activeAuditRequests = new TimeoutMap<>();
        qidLogCounter = AuditDatabaseManager.getQidCounter();
    }

    public static AuditManager getInstance() {
        if (INSTANCE == null) {
            synchronized (AuditManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuditManager();
                }
            }
        }
        return INSTANCE;
    }

    public String getQidLogCounter() {
        String hexLongId = Long.toHexString(qidLogCounter); // 16 characters wide for long
        qidLogCounter++;
        return hexLongId;
    }

    public List<AuditEntry> getFilteredLogEntries(@NotNull String guildId, String memberId, String targetId, Integer actionTypeId, Integer targetTypeOrdinal, String keyword) {
        return getFilteredLogEntries(guildId, memberId, targetId, actionTypeId, targetTypeOrdinal, keyword, 100);
    }

    public List<AuditEntry> getFilteredLogEntries(@NotNull String guildId, String memberId, String targetId, Integer actionTypeId, Integer targetTypeOrdinal, String keyword, int amount) {
        return getAuditLogCache(guildId).values().stream()
                .filter(entry -> {
                    if (memberId != null && !entry.memberId().equals(memberId)) return false;
                    if (targetId != null && !entry.targetId().equals(targetId)) return false;
                    if (actionTypeId != null && entry.typeKey() != actionTypeId) return false;
                    if (keyword != null && !checkEntryForKeyword(keyword)) return false;
                    if (targetTypeOrdinal != null && ActionType.from(entry.typeKey()).getTargetType().ordinal() != targetTypeOrdinal)
                        return false;
                    return true;
                })
                .limit(amount)
                .toList();
    }

    public boolean checkEntryForKeyword(String keyword) {
        return true; //TODO implement keyword search, not to sure anymore what to do with it but gonna see...
    }

    public ConcurrentHashMap<String, AuditEntry> getAuditLogCache(String guildId) {
        if (!guildAuditLogCache.containsKey(guildId)) {
            guildAuditLogCache.put(guildId, new ConcurrentHashMap<>());
        }
        return guildAuditLogCache.get(guildId);
    }

    public void cacheLog(Guild guild, AuditEntry auditEntry) {
        if (!guildAuditLogCache.containsKey(guild.getId())) {
            guildAuditLogCache.put(guild.getId(), new ConcurrentHashMap<>());
        }
        getAuditLogCache(guild.getId()).put(auditEntry.qid(), auditEntry);
    }

    public String getTargetString(Guild guild, TargetType targetType, String targetId) {
        return switch (targetType) {
            case MEMBER, INTEGRATION -> Objects.requireNonNull(guild.getMemberById(targetId)).getAsMention();
            case ROLE -> Objects.requireNonNull(guild.getRoleById(targetId)).getAsMention();
            case CHANNEL ->
                    guild.getChannelById(StandardGuildChannel.class, targetId) != null ? Objects.requireNonNull(guild.getChannelById(StandardGuildChannel.class, targetId)).getAsMention() : "DeletedChannel(id= " + targetId + ")";
            case GUILD -> guild.getName();
            case INVITE -> "Invite";
            case WEBHOOK ->
                    guild.retrieveWebhooks().submit().join().stream().filter(webhook -> webhook.getId().equals(targetId)).toList().get(0).getName();
            case EMOJI -> {
                try {
                    yield Objects.requireNonNull(guild.getEmojiById(targetId)).getAsMention();
                } catch (NullPointerException e) {
                    yield "Emoji";
                }
            }
            case STAGE_INSTANCE -> "Stage";
            case STICKER ->
                    guild.retrieveStickers().submit().join().stream().filter(sticker -> sticker.getId().equals(targetId)).toList().get(0).getName();
            case THREAD -> Objects.requireNonNull(guild.getThreadChannelById(targetId)).getAsMention();
            case SCHEDULED_EVENT -> Objects.requireNonNull(guild.getScheduledEventById(targetId)).getName();
            case AUTO_MODERATION_RULE -> guild.retrieveAutoModRuleById(targetId).submit().join().getName();
            default -> "Unknown";
        };
    }

    public IMentionable getTarget(Guild guild, TargetType targetType, String targetId) {
        return switch (targetType) {
            case MEMBER, INTEGRATION -> guild.getMemberById(targetId);
            case ROLE -> guild.getRoleById(targetId);
            case CHANNEL -> guild.getChannelById(StandardGuildChannel.class, targetId);
            case EMOJI -> {
                try {
                    yield guild.getEmojiById(targetId);
                } catch (NullPointerException e) {
                    yield new EmptyMentionable(targetId, targetType);
                }
            }
            case THREAD -> guild.getThreadChannelById(targetId);
            default -> new EmptyMentionable(targetId, targetType);
        };
    }

    public IMentionable getTarget(Guild guild, int targetType, String targetId) {
        return getTarget(guild, getTargetTypeByOrdinal(targetType), targetId);
    }

    public TargetType getTargetTypeByOrdinal(int targetOrdinal) {
        return TargetType.values()[targetOrdinal];
    }

    @Override
    public void shutdown() {
        activeAuditRequests.shutdown();
        AuditDatabaseManager.saveAuditEntries(guildAuditLogCache);
    }

    public String getAuditRequestId() {
        String rId = Secret.getRandomIdentifier(6);
        while (activeAuditRequests.containsKey(rId)) {
            rId = Secret.getRandomIdentifier(6);
        }
        return rId;
    }

    public StringSelectMenu getTargetTypesMenu() {
        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("target-type-select");
        for (TargetType value : TargetType.values()) {
            menuBuilder.addOption(StringUtils.convertUpperCaseToTitleCase(value.name()), String.valueOf(value.ordinal()));
        }
        return menuBuilder.build();
    }

}
