package dev.roundtable.beehoven.handlers;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.objects.HashedPassword;
import dev.roundtable.beehoven.utils.ValidationUtil;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Handler for the register page
 */
public class RegisterHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Parameters required */
        String firstName = request.queryParams("first_name");
        String lastName = request.queryParams("last_name");
        String username = request.queryParams("username");
        String email = request.queryParams("email");
        String rawPassword = request.queryParams("password");

        /* Validate parameters */

        if (!ValidationUtil.checkName(firstName))
            Spark.halt(400, "Invalid First Name");

        if (!ValidationUtil.checkName(lastName))
            Spark.halt(400, "Invalid Last Name");

        if (!ValidationUtil.checkUsername(username))
            Spark.halt(400, "Invalid Username");

        if (!ValidationUtil.checkEmail(email))
            Spark.halt(400, "Invalid Email");

        if (!ValidationUtil.checkPassword(rawPassword))
            Spark.halt(400, "Invalid Password");

        /* Trim parameters */
        firstName = firstName.trim();
        lastName = lastName.trim();
        email = email.trim();

        HashedPassword password = HashedPassword.hash(rawPassword);

        try (Connection connection = Beehoven.getInstance().getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO accounts (email,username,salt,password,display_name) VALUES (?,?,?,?,?)"
            )) {

                preparedStatement.setString(1, email);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, password.calculateEncodedSalt());
                preparedStatement.setString(4, password.calculateEncodedHash());
                preparedStatement.setString(5, firstName + " " + lastName);

                preparedStatement.executeUpdate();

            } catch (SQLException e) {

                if (isConstraintViolation(e)) {

                    // This exception fails when a unique constraint (e.g. emails, username) fails
                    response.status(409); // HTTP 409 Conflict

                    // Determine if it was the either the
                    // email or username unique index that triggered this exception
                    if (e.getMessage().contains("accounts_email_uindex")) {
                        return "Email already registered";
                    } else if (e.getMessage().contains("accounts_username_uindex")) {
                        return "Username is unavailable";
                    } else {
                        return "Username/Email is already registered";
                    }

                } else {

                    // Re-throw the exception to have it handled by a superior
                    throw e;

                }

            }

        }

        response.status(201);
        return "";

    }

    private static boolean isConstraintViolation(SQLException e) {
        return e.getErrorCode() == 19 || e instanceof SQLIntegrityConstraintViolationException || (e.getSQLState() != null && e.getSQLState().startsWith("23"));
    }

}
