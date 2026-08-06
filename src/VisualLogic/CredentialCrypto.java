package VisualLogic;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Obfuscates the repository password before it is written to the plaintext
 * config.xml. This is not real key management (the key is derived from
 * application constants), but it stops casual plaintext disclosure of the
 * credential in the config file. The value stays in plaintext in memory while
 * the application runs because it is needed for HTTP Basic auth.
 */
public final class CredentialCrypto {

    private static final String PREFIX = "enc:v1:";
    private static final int GCM_IV_LENGTH = 12;

    private CredentialCrypto() {
    }

    public static String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return "";
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(deriveKey(), "AES"), new GCMParameterSpec(128, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] payload = Arrays.copyOf(iv, iv.length + ciphertext.length);
            System.arraycopy(ciphertext, 0, payload, iv.length, ciphertext.length);
            return PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (Exception ex) {
            return plaintext;
        }
    }

    public static String decrypt(String stored) {
        if (stored == null || stored.isEmpty() || !stored.startsWith(PREFIX)) {
            return stored;
        }
        try {
            byte[] payload = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(deriveKey(), "AES"),
                    new GCMParameterSpec(128, payload, 0, GCM_IV_LENGTH));
            return new String(cipher.doFinal(payload, GCM_IV_LENGTH, payload.length - GCM_IV_LENGTH),
                    StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return stored;
        }
    }

    private static byte[] deriveKey() throws Exception {
        KeySpec spec = new PBEKeySpec("MyOpenLabRepositoryCredentials".toCharArray(),
                "MyOpenLab-Credential-v1".getBytes(StandardCharsets.UTF_8), 100000, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }
}
