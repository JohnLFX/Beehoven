package handlers;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.handlers.project.UpdateProjectScoreHandler;
import dev.roundtable.beehoven.objects.Project;
import dev.roundtable.beehoven.utils.Gzip;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Request;
import spark.Response;
import testing.Account;
import testing.TestState;
import testing.TestUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

import static org.mockito.Mockito.*;

public class TestUpdateProjectScore {

    private static UpdateProjectScoreHandler handler;

    @BeforeClass(dependsOnGroups = {"TestRegister", "TestCreateProject"})
    public static void setup() {
        handler = new UpdateProjectScoreHandler();
    }

    @Test(dependsOnGroups = {"TestRegister"})
    public void testBadPayload() throws Exception {

        int id = TestState.ACCOUNTS.keySet().stream().mapToInt(entry -> entry).findFirst().orElse(0);

        Request request = mock(Request.class);
        when(request.attribute("accountID")).thenReturn(id);
        when(request.queryParams("project_id")).thenReturn(String.valueOf(0));
        when(request.body()).thenReturn("");

        Response response = mock(Response.class);
        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(response).status(statusCode.capture());

        handler.handle(request, response);

        Assert.assertEquals((int) statusCode.getValue(), 400, "Unexpected response code");

    }

    @Test(groups = "TestUpdateProjectScore", dependsOnGroups = {"TestRegister", "TestCreateProject"})
    public void test() throws Exception {

        for (Account account : TestState.ACCOUNTS.values()) {

            Assert.assertFalse(account.getProjects().isEmpty());

            for (Project currentProject : account.getProjects()) {

                Project project = TestUtils.randomProject();
                project.setId(currentProject.getId());

                String projectJSON = Beehoven.GSON.toJson(project);

                Request request = mock(Request.class);
                when(request.attribute("accountID")).thenReturn(account.getAccountID());
                when(request.queryParams("project_id")).thenReturn(String.valueOf(project.getId()));
                when(request.body()).thenReturn(projectJSON);
                when(request.bodyAsBytes()).thenReturn(projectJSON.getBytes(StandardCharsets.UTF_8));

                Response response = mock(Response.class);
                ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
                doNothing().when(response).status(statusCode.capture());

                handler.handle(request, response);

                Assert.assertEquals((int) statusCode.getValue(), 204, "Unexpected response code for proper project meta update");

                try (Connection connection = Beehoven.getInstance().getConnection();
                     PreparedStatement query = connection.prepareStatement("SELECT score,owner FROM projects WHERE id = ?")) {

                    query.setInt(1, project.getId());

                    try (ResultSet rs = query.executeQuery()) {

                        Assert.assertTrue(rs.next(), "No database record found for updated project score");

                        Assert.assertEquals(rs.getInt(2), account.getAccountID());
                        Assert.assertTrue(Objects.deepEquals(rs.getBytes(1), Gzip.compress(projectJSON.getBytes(StandardCharsets.UTF_8))));

                        Assert.assertFalse(rs.next(), "Multiple database rows returned for a supposed unique ID");

                    }

                }

            }

        }

    }

}
