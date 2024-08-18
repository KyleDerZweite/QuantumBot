package de.quantum.modules.audit.entries;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public record DatabaseEntry(String qid, String botId, String guildId, String memberId, String targetId, String reason,
                            int typeKey, String memberMention, String targetString, String changeString,
                            long epochSecond) implements AuditEntry {

    public static DatabaseEntry fromResultSet(ResultSet rs) throws SQLException {
        return new DatabaseEntry(
                rs.getString("qid"),
                rs.getString("bot_id"),
                rs.getString("guild_id"),
                rs.getString("member_id"),
                rs.getString("target_id"),
                rs.getString("reason"),
                rs.getInt("type_key"),
                rs.getString("member_mention"),
                rs.getString("target_string"),
                rs.getString("change_string"),
                rs.getLong("epoch_second")
        );
    }

}
