package dev.roundtable.beehoven.handlers.project;

import com.google.gson.JsonParseException;
import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.objects.Project;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateProjectMetaHandler implements Route {

    //private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Attributes */
        int accountID = request.attribute("accountID");

        /* Payload: Project Object */
        Project project;

        try {

            project = Beehoven.GSON.fromJson(request.body(), Project.class);

            if (project == null) {
                response.status(400); // HTTP 400 Bad Request
                return "";
            }

        } catch (JsonParseException e) {
            response.status(400); // HTTP 400 Bad Request
            return "";
        }

        try (Connection connection = Beehoven.getInstance().getConnection();
             PreparedStatement query = connection.prepareStatement("UPDATE projects SET name = ?, title = ?, subtitle = ?, " +
                     "artist = ?, album = ?, wordsBy = ?, musicBy = ? WHERE id = ? AND owner = ?")) {

            query.setString(1, project.getName());
            query.setString(2, project.getTitle());
            query.setString(3, project.getSubtitle());
            query.setString(4, project.getArtist());
            query.setString(5, project.getAlbum());
            query.setString(6, project.getWordsBy());
            query.setString(7, project.getMusicBy());
            query.setInt(8, project.getId());
            query.setInt(9, accountID);

            query.executeUpdate();
        }

        response.status(204);
        return "";

    }

}
