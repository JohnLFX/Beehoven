package dev.roundtable.beehoven.handlers.project;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.utils.Gzip;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateProjectScoreHandler implements Route {

    //private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Attributes */
        int accountID = request.attribute("accountID");

        /* Query Parameters */
        if (request.queryParams("project_id") == null || request.body().isEmpty()) {
            response.status(400); // HTTP 400 Bad Request
            return "";
        }

        int projectID = Integer.parseInt(request.queryParams("project_id"));

        /* Payload: Score JSON object */

        try (Connection connection = Beehoven.getInstance().getConnection();
             PreparedStatement query = connection.prepareStatement("UPDATE projects SET score = ? WHERE id = ? AND owner = ?")) {

            query.setBytes(1, Gzip.compress(request.bodyAsBytes()));
            query.setInt(2, projectID);
            query.setInt(3, accountID);

            query.executeUpdate();
        }

        response.status(204);
        return "";

    }

}
