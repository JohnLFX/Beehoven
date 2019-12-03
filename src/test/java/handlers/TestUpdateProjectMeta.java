package handlers;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.handlers.project.UpdateProjectMetaHandler;
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

import static org.mockito.Mockito.*;

public class TestUpdateProjectMeta {

    private static UpdateProjectMetaHandler handler;

    @BeforeClass(dependsOnGroups = {"TestRegister"})
    public static void setup() {
        handler = new UpdateProjectMetaHandler();
    }

    @Test(dependsOnGroups = {"TestRegister"})
    public void testBadPayload() throws Exception {

        int id = TestState.ACCOUNTS.keySet().stream().mapToInt(entry -> entry).findFirst().orElse(0);

        Request request = mock(Request.class);
        when(request.attribute("accountID")).thenReturn(id);
        when(request.body()).thenReturn("{System.currentTimeMillis()}");

        Response response = mock(Response.class);
        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(response).status(statusCode.capture());

        handler.handle(request, response);

        Assert.assertEquals((int) statusCode.getValue(), 400, "Unexpected response code");

    }

    @Test(groups = {"TestUpdateProjectMeta"}, dependsOnGroups = {"TestCreateProject"})
    public void test() throws Exception {

        for (Account account : TestState.ACCOUNTS.values()) {

            Assert.assertFalse(account.getProjects().isEmpty(), "There should be a project saved within the account. Check to make sure projects are being saved correctly.");

            for (Project currentProject : account.getProjects()) {

                Project project = TestUtils.randomProject();
                project.setId(currentProject.getId());

                Request request = mock(Request.class);
                when(request.attribute("accountID")).thenReturn(account.getAccountID());
                when(request.body()).thenReturn(Beehoven.GSON.toJson(project));

                Response response = mock(Response.class);
                ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
                doNothing().when(response).status(statusCode.capture());

                handler.handle(request, response);

                Assert.assertEquals((int) statusCode.getValue(), 204, "Unexpected response code for proper project meta update");

                // Update our current test state for this account (expected value)
                currentProject.setId(project.getId());
                currentProject.setName(project.getName());
                currentProject.setTitle(project.getTitle());
                currentProject.setSubtitle(project.getSubtitle());
                currentProject.setArtist(project.getArtist());
                currentProject.setAlbum(project.getAlbum());
                currentProject.setWordsBy(project.getWordsBy());
                currentProject.setMusicBy(project.getMusicBy());

                try (Connection connection = Beehoven.getInstance().getConnection();
                     PreparedStatement query = connection.prepareStatement("SELECT name,title,subtitle,artist,album,wordsBy,musicBy,owner FROM projects WHERE id = ?")) {

                    query.setInt(1, project.getId());

                    try (ResultSet rs = query.executeQuery()) {

                        Assert.assertTrue(rs.next(), "No database record found for updated project");

                        Assert.assertEquals(rs.getString(1), project.getName());
                        Assert.assertEquals(rs.getString(2), project.getTitle());
                        Assert.assertEquals(rs.getString(3), project.getSubtitle());
                        Assert.assertEquals(rs.getString(4), project.getArtist());
                        Assert.assertEquals(rs.getString(5), project.getAlbum());
                        Assert.assertEquals(rs.getString(6), project.getWordsBy());
                        Assert.assertEquals(rs.getString(7), project.getMusicBy());
                        Assert.assertEquals(rs.getInt(8), account.getAccountID());

                        Assert.assertFalse(rs.next(), "Multiple database rows returned for a supposed unique ID");

                    }

                }

            }

        }

    }

}
