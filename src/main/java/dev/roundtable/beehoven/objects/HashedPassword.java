package dev.roundtable.beehoven.objects;

import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * An object representing a hashed password
 */
public class HashedPassword {

    private static final SecureRandom RANDOM;
    private static final SecretKeyFactory FACTORY;

    static {
        RANDOM = new SecureRandom();
        try {
            // This hashing algorithm was designed for password usage
            FACTORY = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private final byte[] hash, salt;

    private HashedPassword(byte[] salt, byte[] hash) {
        this.salt = salt;
        this.hash = hash;
    }

    /**
     * Calculates a Base64 Encoded string of the salt
     *
     * @return Base64 encoded salt
     */
    @NotNull
    public String calculateEncodedSalt() {
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Returns the current salt
     *
     * @return The salt
     */
    @NotNull
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Calculates a Base64 Encoded hash of the password
     *
     * @return The encoded hash of the password
     */
    @NotNull
    public String calculateEncodedHash() {
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Returns the current hash
     *
     * @return The hash
     */
    @NotNull
    public byte[] getHash() {
        return hash;
    }

    /**
     * Creates a new HashedPassword object based on a given password and salt
     *
     * @param password The plain text password
     * @param salt     The salt to use
     * @return A new HashedPassword object populated with a hash generated from the parameters
     * @throws InvalidKeySpecException If the hashing process failed
     */
    @NotNull
    public static HashedPassword hash(String password, byte[] salt) throws InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        byte[] hash = FACTORY.generateSecret(spec).getEncoded();
        return new HashedPassword(salt, hash);
    }

    /**
     * Creates a new HashedPassword object based on a plain text password and a randomly generated salt
     *
     * @param password The plain text password
     * @return A new HashedPassword object populated with the password, hash, and salt
     * @throws InvalidKeySpecException If the hashing process failed
     */
    @NotNull
    public static HashedPassword hash(String password) throws InvalidKeySpecException {

        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        return hash(password, salt);

    }

}
