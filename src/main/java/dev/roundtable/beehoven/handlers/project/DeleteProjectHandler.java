package dev.roundtable.beehoven.handlers.project;

import dev.roundtable.beehoven.Beehoven;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteProjectHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Attributes */
        int accountID = request.attribute("accountID");

        /* Query Parameters */
        if (request.queryParams("project_id") == null) {
            response.status(400); // HTTP 400 Bad Request
            return "Missing project_id query parameter";
        }

        int projectID = Integer.parseInt(request.queryParams("project_id"));

        try (Connection connection = Beehoven.getInstance().getConnection();
             PreparedStatement query = connection.prepareStatement("DELETE FROM projects WHERE id = ? AND owner = ?")) {

            query.setInt(1, projectID);
            query.setInt(2, accountID);

            query.executeUpdate();

            response.status(200);
            return "Project ID " + projectID + " deleted";

        }

    }

}
