package dev.roundtable.beehoven.handlers.project;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.objects.Project;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ListProjectsMetaHandler implements Route {

    //private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public Object handle(Request request, Response response) throws Exception {

        /* Attributes */
        int accountID = request.attribute("accountID");

        try (Connection connection = Beehoven.getInstance().getConnection();
             PreparedStatement query = connection.prepareStatement("SELECT id,name,title,subtitle,artist,album,wordsBy,musicBy FROM projects WHERE owner = ?")) {

            query.setInt(1, accountID);

            List<Project> projects = new ArrayList<>();

            ResultSet rs = query.executeQuery();

            while (rs.next()) {

                Project project = new Project();
                project.setId(rs.getInt(1));
                project.setName(rs.getString(2));
                project.setTitle(rs.getString(3));
                project.setSubtitle(rs.getString(4));
                project.setArtist(rs.getString(5));
                project.setAlbum(rs.getString(6));
                project.setWordsBy(rs.getString(7));
                project.setMusicBy(rs.getString(8));

                projects.add(project);

            }

            rs.close();

            response.status(200); // HTTP 200 - OK
            return Beehoven.GSON.toJson(projects);

        }

    }

}
