package de.quantum.core.errors;

import de.quantum.core.LanguageManager;
import de.quantum.core.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.ArrayList;
import java.util.Locale;

public class ErrorManager {

    private static final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();


    public static void replyInteraction(InteractionHook interactionHook, String errorMessage) {


    }

    public static void replyMissingPermission(InteractionHook interactionHook, ArrayList<Permission> missingPermissions) {
        Locale locale = interactionHook.getInteraction().getUserLocale().toLocale();
        EmbedBuilder embedBuilder = EmbedUtils.getStandardEmbedBuilder();

        StringBuilder missingPermissionStringBuilder = new StringBuilder();
        missingPermissionStringBuilder.append(LanguageManager.getString("missing_permission", locale));
        missingPermissionStringBuilder.append("```");
        for (Permission permission : missingPermissions) {
            missingPermissionStringBuilder.append("- ").append(permission.getName()).append("\n");
        }
        missingPermissionStringBuilder.append("```");

    }


}
