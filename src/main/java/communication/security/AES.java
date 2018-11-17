package communication.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class AES {

    private final SecretKeySpec secretKey;  // the actual key generated from the string
    private final String randomString;      // string to be transmitted between edges


    public AES() {
        this.randomString = generateBase64String();
        this.secretKey = generateKey(randomString);
    }

    public AES(String randomString) {
        this.randomString = randomString;
        this.secretKey = generateKey(randomString);
    }

    private SecretKeySpec generateKey(String randomString) {
        try {
            // hash the json string to increase entropy
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] key = randomString.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // we need only the first 16 bytes for the actual AES key
            return new SecretKeySpec(key, "AES");

        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateBase64String() {
        byte[] array = new byte[80];
        new Random().nextBytes(array);
        return Base64.getEncoder().encodeToString(array);
    }

    public String getRandomString() {
        return this.randomString;
    }

    public String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
