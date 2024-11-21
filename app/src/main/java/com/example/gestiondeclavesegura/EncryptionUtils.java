package com.example.gestiondeclavesegura;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String AES = "AES";

    // Generar una clave AES válida (128 bits) a partir del userId
    public static SecretKey generateKey(String userId) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(userId.getBytes("UTF-8")); // Hash del userId
        byte[] key = new byte[16]; // Clave de 128 bits
        System.arraycopy(keyBytes, 0, key, 0, key.length);
        return new SecretKeySpec(key, AES);
    }

    // Cifrar un texto con AES
    public static String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT); // Convertir a Base64
    }

    // Descifrar un texto cifrado con AES
    public static String decrypt(String encryptedText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
