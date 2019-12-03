package dev.roundtable.beehoven.handlers;

import com.google.gson.JsonObject;
import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.objects.HashedPassword;
import dev.roundtable.beehoven.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.UUID;

public class LoginHandler implements Route {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Parameters required */
        String username = request.queryParams("username");
        String rawPassword = request.queryParams("password");

        if (!ValidationUtil.checkUsername(username))
            Spark.halt(400, "Invalid Username");

        if (!ValidationUtil.checkPassword(rawPassword))
            Spark.halt(400, "Invalid Password");

        try (Connection connection = Beehoven.getInstance().getConnection()) {

            byte[] salt;

            try (PreparedStatement lookupSalt = connection.prepareStatement("SELECT salt FROM accounts WHERE username = ?")) {

                lookupSalt.setString(1, username);

                ResultSet rs = lookupSalt.executeQuery();

                salt = rs.next() ? Base64.getDecoder().decode(rs.getString(1)) : null;

                rs.close();

            }

            if (salt == null) {

                LOGGER.debug("Could not find salt for username \"" + username + "\"");
                Spark.halt(401);

            }

            HashedPassword password = HashedPassword.hash(rawPassword, salt);

            Integer accountID = null;

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id FROM accounts WHERE username = ? AND password = ? AND salt = ?"
            )) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password.calculateEncodedHash());
                preparedStatement.setString(3, password.calculateEncodedSalt());

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next())
                    accountID = rs.getInt(1);

                rs.close();

            }

            if (accountID != null) {

                UUID token = UUID.randomUUID();

                String encodedToken = token.toString().replace("-", "");

                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE accounts SET token = ?, last_login = ? WHERE id = ?"
                )) {

                    preparedStatement.setString(1, encodedToken);
                    preparedStatement.setDate(2, new Date(System.currentTimeMillis()));
                    preparedStatement.setInt(3, accountID);

                    preparedStatement.executeUpdate();

                }

                response.status(200);
                response.type("plain/text");
                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("token", encodedToken);
                responseBody.addProperty("accountID", accountID);
                return Beehoven.GSON.toJson(responseBody);

            } else {

                Spark.halt(401);

            }

        }

        response.status(500);
        return "";

    }

}
