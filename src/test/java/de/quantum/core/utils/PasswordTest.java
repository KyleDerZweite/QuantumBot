package de.quantum.core.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    private Password password;

    @BeforeEach
    void setUp() {
        password = new Password();
    }

    @Test
    void generateWithLengthZero() {
        assertEquals("", password.generate(0));
    }

    @Test
    void generateStringInstance() {
        assertInstanceOf(String.class, password.generate(10));
    }

    @Test
    void generateWithLength() {
        assertEquals(10, password.generate(10).length());
    }
}