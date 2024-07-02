package de.quantum.core.errors;

import java.util.Locale;

public enum Error {

    UNKNOWN(-1,"unknown",true),
    NO_PERMISSION(0,"no_permission"),
    COMMAND_NOT_FOUND(1,"command_not_found",true),
    COMMAND_NOT_ALLOWED(2,"command_not_allowed"),
    ROLE_NOT_FOUND(3,"role_not_found"),
    CHANNEL_NOT_FOUND(4,"channel_not_found"),
    CMD_BLACKLIST_CHANNEL(5,"cmd_blacklist_channel"),
    INSTRUCTION_ERROR(6,"instruction_error"),
    ON_COOLDOWN(7,"on_cooldown"),
    RUNTIME_ERROR(8,"runtime_error"),
    FEATURE_NOT_FOUND(9,"feature_not_found"),
    GUILD_NOT_PERMITTED(10,"guild_not_permitted",true),
    COMMAND_FAILURE(11,"command_failure"),
    COMMAND_NOT_FOUND_WITH_PARAM(12,"command_not_found_with_param"),
    METHOD_NOT_FOUND(13,"method_not_found",true);

    private final int id;
    private final String key;
    private final boolean reportable;

    Error(int id,String key,boolean reportable) {
        this.id = id;
        this.key = key;
        this.reportable = reportable;
    }
    Error(int id,String key) {
        this(id,key,false);
    }

    public String getKey() {
        return key;
    }
    public int getId() {
        return id;
    }
    public String getErrorValue(Locale locale, String var) {
        return "String.format(LanguageManager.getValue(locale,getKey()),var)";
    }
    public boolean isReportable() {
        return reportable;
    }

    public static Error getErrorById(int id) {
        for (Error error : Error.values()) {
            if (error.getId() == id) {
                return error;
            }
        }
        return null;
    }

}
