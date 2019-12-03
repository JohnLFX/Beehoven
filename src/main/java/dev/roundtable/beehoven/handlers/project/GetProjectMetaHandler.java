package dev.roundtable.beehoven.handlers.project;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.objects.Project;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetProjectMetaHandler implements Route {

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
             PreparedStatement query = connection.prepareStatement("SELECT name,title,subtitle,artist,album,wordsBy,musicBy FROM projects WHERE owner = ? AND id = ?");
             PreparedStatement permissionsQuery = connection.prepareStatement("SELECT username FROM permissions INNER JOIN accounts ON accounts.id = permissions.accountID WHERE permissions.pid = ?")) {

            query.setInt(1, accountID);
            query.setInt(2, projectID);

            permissionsQuery.setInt(1, projectID);

            try (ResultSet rs = query.executeQuery()) {

                if (rs.next()) {

                    Project project = new Project();
                    project.setName(rs.getString(1));
                    project.setTitle(rs.getString(2));
                    project.setSubtitle(rs.getNString(3));
                    project.setArtist(rs.getNString(4));
                    project.setAlbum(rs.getNString(5));
                    project.setWordsBy(rs.getNString(6));
                    project.setMusicBy(rs.getNString(7));

                    try (ResultSet permsRS = permissionsQuery.executeQuery()) {

                        while (permsRS.next())
                            project.getSharedWith().add(permsRS.getString(1));

                    }

                    response.status(200);
                    return Beehoven.GSON.toJson(project);

                } else {

                    response.status(410); // HTTP 410 - Gone (Because project IDs are always unique)
                    return "Project score does not exist";

                }

            }

        }

    }

}
