package de.quantum.core;

import de.quantum.core.utils.LanguageManager;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LanguageManagerTest {

    @Test
    void getString() {
        assertEquals("Test English", LanguageManager.getString("test"));
    }

    @Test
    void getStringWithValidLocale() {
        assertEquals("Test English", LanguageManager.getString("test", Locale.ENGLISH));
        assertEquals("Test Deutsch", LanguageManager.getString("test", Locale.GERMAN));
    }

    @Test
    void getStringWithInvalidLocale() {
        assertEquals("Test English", LanguageManager.getString("test", Locale.CHINESE));
    }

    @Test
    void getStringWithInvalidKey() {
        assertEquals("invalid_key", LanguageManager.getString("invalid_key", Locale.ENGLISH));
    }

}