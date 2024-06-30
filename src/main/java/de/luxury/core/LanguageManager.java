package de.luxury.core;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LanguageManager {

    private static final ConcurrentHashMap<Locale, ResourceBundle> LANGUAGES = new ConcurrentHashMap<>();

    private static final String[] LANG_KEYS = new String[]{"en", "de"};

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static void loadLanguages() {
        LANGUAGES.clear();
        for (String key : LANG_KEYS) {
            Locale locale = new Locale(key);
            ResourceBundle bundle = ResourceBundle.getBundle("lang", locale);
            LANGUAGES.put(locale, bundle);
        }

        System.out.println(LANGUAGES);

    }

    public static String getString(Locale locale, String key) {
        if (LANGUAGES.isEmpty()) {
            loadLanguages();
        }
        if (!LANGUAGES.containsKey(locale)) {
            log.warn("Language not found for locale {}", locale);
            locale = DEFAULT_LOCALE;
        }
        if (!LANGUAGES.get(locale).containsKey(key)) {
            log.warn("Value not found for (locale {}, key {}", locale, key);
            return key;
        }
        return LANGUAGES.get(locale).getString(key);
    }

    public static void main(String[] args) {
        LanguageManager.loadLanguages();
    }

}
