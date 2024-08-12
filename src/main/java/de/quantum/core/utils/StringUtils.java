package de.quantum.core.utils;

public class StringUtils {

    public static String convertUpperCaseToTitleCase(String input) {
        StringBuilder output = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (c == '_') {
                output.append(' ');
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    output.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    output.append(Character.toLowerCase(c));
                }
            }
        }

        return output.toString();
    }

}
