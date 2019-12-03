package dev.roundtable.beehoven.handlers.chat;

import dev.roundtable.beehoven.Beehoven;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PostChatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

        if (request.queryParams("project_id") == null)
            Spark.halt(400, "Missing project_id query parameter");

        int accountID = request.attribute("accountID");
        int projectID = Integer.parseInt(request.queryParams("project_id"));

        String username = "Account " + accountID;

        try (Connection connection = Beehoven.getInstance().getConnection();
             PreparedStatement query = connection.prepareStatement("SELECT username FROM accounts WHERE id = ?")) {

            query.setInt(1, accountID);

            try (ResultSet rs = query.executeQuery()) {

                if (rs.next())
                    username = rs.getString(1);

            }

        }

        ChatMessages.addMessage(accountID, projectID, username, request.body());

        response.status(202); // HTTP 202 Accepted
        return "";

    }

}
