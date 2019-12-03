package handlers;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.handlers.project.CreateProjectHandler;
import dev.roundtable.beehoven.objects.Project;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Request;
import spark.Response;
import testing.Account;
import testing.TestState;
import testing.TestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;

public class TestCreateProject {

    private static CreateProjectHandler handler;

    @BeforeClass(dependsOnGroups = {"TestRegister"})
    public static void setup() {
        handler = new CreateProjectHandler();
    }

    @Test(dependsOnGroups = {"TestRegister"})
    public void testBadPayload() throws Exception {

        int id = TestState.ACCOUNTS.keySet().stream().mapToInt(entry -> entry).findFirst().orElse(0);

        Request request = mock(Request.class);
        when(request.attribute("accountID")).thenReturn(id);
        when(request.body()).thenReturn("{PxbvNeYTDG49jcs3JXT42mkJ, xs3JXT4}");

        Response response = mock(Response.class);
        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(response).status(statusCode.capture());

        handler.handle(request, response);

        Assert.assertEquals((int) statusCode.getValue(), 400, "Unexpected response code");

    }

    @Test(groups = {"TestCreateProject"}, dependsOnGroups = {"TestRegister"})
    public void test() throws Exception {

        for (Account account : TestState.ACCOUNTS.values()) {

            Project project = TestUtils.randomProject();
            Assert.assertNotNull(project);

            Request request = mock(Request.class);
            when(request.attribute("accountID")).thenReturn(account.getAccountID());
            when(request.body()).thenReturn(Beehoven.GSON.toJson(project));

            Response response = mock(Response.class);
            ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
            doNothing().when(response).status(statusCode.capture());

            int lastID = -1;
            int projectsToGenerate = ThreadLocalRandom.current().nextInt(5, 20);

            // Create new projects. Test to see if the 4 projects after the first one increase its ID sequentially.
            for (int i = 0; i < projectsToGenerate; i++) {

                project.setId(lastID + 1);

                String responseBody = Objects.toString(handler.handle(request, response));
                Project responseProject = Beehoven.GSON.fromJson(responseBody, Project.class);

                Assert.assertEquals((int) statusCode.getValue(), 201, "Response should be HTTP 201 (Created)");
                project.setId(responseProject.getId());
                Assert.assertEquals(responseProject, project, "Returned project does not match submitted project");

                Assert.assertTrue(account.getProjects().add(responseProject));

                lastID = responseProject.getId();

                // Query the database and see if the project was added correctly
                try (Connection connection = Beehoven.getInstance().getConnection();
                     PreparedStatement query = connection.prepareStatement("SELECT owner,name,title,subtitle,artist,album,wordsBy,musicBy FROM projects WHERE id = ?")) {

                    query.setInt(1, responseProject.getId());

                    try (ResultSet rs = query.executeQuery()) {

                        Assert.assertTrue(rs.next(), "No database record found for created project");

                        Assert.assertEquals(rs.getInt(1), account.getAccountID());
                        Assert.assertEquals(rs.getString(2), responseProject.getName());
                        Assert.assertEquals(rs.getString(3), responseProject.getTitle());
                        Assert.assertEquals(rs.getString(4), responseProject.getSubtitle());
                        Assert.assertEquals(rs.getString(5), responseProject.getArtist());
                        Assert.assertEquals(rs.getString(6), responseProject.getAlbum());
                        Assert.assertEquals(rs.getString(7), responseProject.getWordsBy());
                        Assert.assertEquals(rs.getString(8), responseProject.getMusicBy());

                        Assert.assertFalse(rs.next(), "Duplicate records found for created project");

                    }

                }

            }

        }

    }

}
