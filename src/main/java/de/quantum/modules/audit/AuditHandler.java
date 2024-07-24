package de.quantum.modules.audit;

import de.quantum.core.database.DatabaseManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class AuditHandler {

    //TODO
    private static int qidCounter = 0;

    public void getAllAuditLogs() {
        ResultSet rs = DatabaseManager.getInstance().selectFrom("*", "audit_log");
        try {
            while (rs.next()) {
                //
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    //TODO
    public static boolean isGuildLogging(Guild guild, Class<?> clazz) {
        return false;
    }



}
