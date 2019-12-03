package dev.roundtable.beehoven.handlers.project;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.utils.Gzip;
import spark.Request;
import spark.Response;
import spark.Route;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetProjectScoreHandler implements Route {

    //private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Attributes */
        int accountID = request.attribute("accountID");

        /* Query Parameters */
        if (request.queryParams("project_id") == null) {
            response.status(400); // HTTP 400 Bad Request
            return "";
        }

        int projectID = Integer.parseInt(request.queryParams("project_id"));

        try (Connection connection = Beehoven.getInstance().getConnection();
             PreparedStatement query = connection.prepareStatement("SELECT score FROM projects WHERE owner = ? AND id = ?")) {

            query.setInt(1, accountID);
            query.setInt(2, projectID);

            try (ResultSet rs = query.executeQuery()) {

                if (rs.next()) {

                    byte[] compressedScore = rs.getBytes(1);

                    if (compressedScore == null) {

                        response.status(204);
                        return "";

                    } else {

                        byte[] score = Gzip.decompress(compressedScore);
                        response.status(200); // HTTP 200 - OK
                        return new String(score, StandardCharsets.UTF_8);

                    }

                } else {

                    response.status(410); // HTTP 410 - Gone (Because project IDs are always unique)
                    return "Project score does not exist";

                }

            }

        }

    }

}
