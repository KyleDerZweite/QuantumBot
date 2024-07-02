package de.quantum.core.utils;

import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Secret {

    public static String encrypt(String plaintext, @Nullable String encryptKey) throws Exception {
        if (CheckUtils.checkNull(encryptKey)) {
            encryptKey = Utils.getEncryptKey();
        }
        SecretKeySpec secretKey = new SecretKeySpec(encryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String ciphertext, @Nullable String decryptKey) throws Exception {
        if (CheckUtils.checkNull(decryptKey)) {
            decryptKey = Utils.getEncryptKey();
        }
        SecretKeySpec secretKey = new SecretKeySpec(decryptKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(plainText, StandardCharsets.UTF_8);
    }

}
