package dev.roundtable.beehoven.handlers.project.share;

import dev.roundtable.beehoven.Beehoven;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AddSharedUser implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Attributes */
        int accountID = request.attribute("accountID");

        /* Query Parameters */
        if (request.queryParams("share_username") == null || request.queryParams("project_id") == null) {
            response.status(400); // HTTP 400 Bad Request
            return "";
        }

        String sharedUsername = request.queryParams("share_username");
        int projectID = Integer.parseInt(request.queryParams("project_id"));

        try (Connection connection = Beehoven.getInstance().getConnection();
             PreparedStatement fetchAccountIDQuery = connection.prepareStatement("SELECT id FROM accounts WHERE username = ?");
             PreparedStatement addUserQuery = connection.prepareStatement("INSERT INTO permissions (pid,accountID) VALUES (?,?)")) {

            fetchAccountIDQuery.setString(1, sharedUsername);

            int sharedAccountID = -1;

            try (ResultSet rs = fetchAccountIDQuery.executeQuery()) {

                if (rs.next()) {

                    sharedAccountID = rs.getInt(1);

                }

            }

            if (sharedAccountID == -1) {
                Spark.halt(410, "Account does not exist");
                return "";
            }

            addUserQuery.setInt(1, projectID);
            addUserQuery.setInt(2, sharedAccountID);

            addUserQuery.executeUpdate();

            response.status(201);
            return "";

        }

    }

}
