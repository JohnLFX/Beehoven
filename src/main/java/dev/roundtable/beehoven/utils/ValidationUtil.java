package dev.roundtable.beehoven.utils;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{1,16}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,5}");

    private ValidationUtil() {
    }

    /**
     * Checks to see if the given username is valid
     *
     * @param username The username to check
     * @return True if the username is valid, false otherwise
     */
    public static boolean checkUsername(String username) {
        if (username == null || username.isEmpty())
            return false;

        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Checks to see if the given email is valid
     *
     * @param email The email to check
     * @return True if the email is valid, false otherwise
     */
    public static boolean checkEmail(String email) {
        if (email == null || email.isEmpty())
            return false;

        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks to see if the given password is valid
     *
     * @param password The password to check
     * @return True if the password is valid, false otherwise
     */
    public static boolean checkPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Checks to see if a given first/last name is valid.
     * All this method does is check to see if the name is not null and the length is <= 127.
     *
     * @param name The string to check
     * @return If the name is valid
     */
    public static boolean checkName(String name) {
        return name != null && !name.isEmpty() && name.length() <= 127;
    }

}
