package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utility class for AES encryption and decryption.
 */
public class AESUtil {

    /**
     * Algorithm used for encryption and decryption.
     */
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);
    /**
     * IV parameter used for encryption and decryption.
     */
    private final IvParameterSpec IV;
    /**
     * Salt used for encryption and decryption.
     */
    private final String SALT;

    /**
     * Default constructor.
     */
    public AESUtil() {
        IV = generateIv();
        SALT = generateSalt();
    }

    /**
     * Get a secret key from a password.
     *
     * @param password the password.
     * @param salt     the salt.
     * @return the secret key.
     * @throws NoSuchAlgorithmException if the algorithm is not found.
     * @throws InvalidKeySpecException  if the key specification is invalid.
     */
    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    /**
     * Generate a random IV.
     *
     * @return the IV.
     */
    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Generate a random salt.
     *
     * @return the salt.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom secRandom = new SecureRandom();
        secRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Encrypt the data.
     *
     * @param algorithm the algorithm.
     * @param input     the input data.
     * @param key       the secret key.
     * @param iv        the IV.
     * @return the encrypted data.
     */
    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder()
                    .encodeToString(cipherText);
        } catch (Exception e) {
            logger.error("Error while encrypting: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypt the data.
     *
     * @param algorithm the algorithm.
     * @param encrypted the encrypted data.
     * @param key       the secret key.
     * @param iv        the IV.
     * @return the decrypted data.
     */
    public static String decrypt(String algorithm, String encrypted, SecretKey key, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(Base64.getDecoder()
                    .decode(encrypted)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error while decrypting: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypt the data using the default algorithm and the given password.
     *
     * @param input    the input data.
     * @param password the password.
     * @return the encrypted data.
     */
    public String encrypt(String input, String password) {
        try {
            SecretKey key = getKeyFromPassword(password, SALT);
            return encrypt(ALGORITHM, input, key, IV);
        } catch (Exception e) {
            logger.error("Error while encrypting: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypt the data using the default algorithm and the given password.
     *
     * @param encrypted the encrypted data.
     * @param password  the password.
     * @return the decrypted data.
     */
    public String decrypt(String encrypted, String password) {
        try {
            SecretKey key = getKeyFromPassword(password, SALT);
            return decrypt(ALGORITHM, encrypted, key, IV);
        } catch (Exception e) {
            logger.error("Error while decrypting: ", e);
            throw new RuntimeException(e);
        }
    }
}
