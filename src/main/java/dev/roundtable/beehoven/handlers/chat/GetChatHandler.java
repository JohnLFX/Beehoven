package dev.roundtable.beehoven.handlers.chat;

import dev.roundtable.beehoven.Beehoven;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.util.List;

public class GetChatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {

        if (request.queryParams("project_id") == null)
            Spark.halt(400, "Missing project_id query parameter");

        int projectID = Integer.parseInt(request.queryParams("project_id"));

        List<Message> messages = ChatMessages.pollMessages(projectID);

        if (messages.isEmpty()) {

            response.status(204);
            return "";

        } else {

            response.status(200);
            return Beehoven.GSON.toJson(messages);

        }

    }

}
