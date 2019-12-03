package dev.roundtable.beehoven.handlers.project;

import com.google.gson.JsonParseException;
import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.objects.Project;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CreateProjectHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Attributes */
        int accountID = request.attribute("accountID");

        /* Payload: Project Object */
        Project project;

        try {
            project = Beehoven.GSON.fromJson(request.body(), Project.class);
        } catch (JsonParseException e) {
            response.status(400); // HTTP 400 Bad Request
            return "";
        }

        if (project == null) {
            response.status(400); // HTTP 400 Bad Request
            return "";
        }

        try (Connection connection = Beehoven.getInstance().getConnection()) {

            try (PreparedStatement createProject = connection.prepareStatement("INSERT INTO projects(owner,name,title,subtitle,artist,album,wordsBy,musicBy) VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {

                createProject.setInt(1, accountID);
                createProject.setString(2, project.getName());
                createProject.setString(3, project.getTitle());
                createProject.setString(4, project.getSubtitle());
                createProject.setString(5, project.getArtist());
                createProject.setString(6, project.getAlbum());
                createProject.setString(7, project.getWordsBy());
                createProject.setString(8, project.getMusicBy());

                int affectedRows = createProject.executeUpdate();

                if (affectedRows == 0)
                    Spark.halt(500, "Failed to create project in database");

                try (ResultSet generatedKeys = createProject.getGeneratedKeys()) {

                    if (generatedKeys.next()) {

                        response.status(201);

                        project.setId(generatedKeys.getInt(1));
                        return Beehoven.GSON.toJson(project);

                    } else {

                        Spark.halt(500, "Creating project failed, no ID obtained");

                    }

                }

            }

        }

        response.status(500);
        return "";

    }

}
