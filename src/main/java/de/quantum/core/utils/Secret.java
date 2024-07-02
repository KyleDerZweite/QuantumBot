package de.quantum.core.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Secret {

    public static String encrypt(String plaintext) {
        return encrypt(plaintext, Utils.getEncryptKey());
    }

    public static String encrypt(@NotNull String plaintext, @NotNull String encryptKey) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(encryptKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception ignored) {
            return plaintext;
        }
    }

    public static String decrypt(String ciphertext) {
        return decrypt(ciphertext, Utils.getEncryptKey());
    }

    public static String decrypt(@NotNull String ciphertext, @NotNull String decryptKey) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(decryptKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return ciphertext;
        }
    }
}
