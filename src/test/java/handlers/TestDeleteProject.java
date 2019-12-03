package handlers;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.handlers.project.DeleteProjectHandler;
import dev.roundtable.beehoven.objects.Project;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Request;
import spark.Response;
import testing.Account;
import testing.TestState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;

public class TestDeleteProject {

    private static DeleteProjectHandler handler;

    @BeforeClass
    public static void setup() {
        handler = new DeleteProjectHandler();
    }

    @Test
    public void testBadPayload() throws Exception {

        Request request = mock(Request.class);
        when(request.attribute("accountID")).thenReturn(0);
        when(request.body()).thenReturn("{PxbvNeYTDG49jcs3JXT42mkJ, xs3JXT4}");

        Response response = mock(Response.class);
        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(response).status(statusCode.capture());

        handler.handle(request, response);

        Assert.assertEquals((int) statusCode.getValue(), 400, "Unexpected response code");

    }

    @Test(dependsOnGroups = {"TestUpdateProjectMeta"})
    public void test() throws Exception {

        Assert.assertFalse(TestState.ACCOUNTS.isEmpty());

        for (Account account : TestState.ACCOUNTS.values()) {

            Assert.assertFalse(account.getProjects().isEmpty());

            Project deletedProject = account.getProjects().get(ThreadLocalRandom.current().nextInt(0, account.getProjects().size()));

            Request request = mock(Request.class);
            when(request.attribute("accountID")).thenReturn(account.getAccountID());
            when(request.queryParams("project_id")).thenReturn(String.valueOf(deletedProject.getId()));

            Response response = mock(Response.class);
            ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
            doNothing().when(response).status(statusCode.capture());

            handler.handle(request, response);

            Assert.assertEquals((int) statusCode.getValue(), 200, "Unexpected response code");

            try (Connection connection = Beehoven.getInstance().getConnection();
                 PreparedStatement query = connection.prepareStatement("SELECT id FROM projects WHERE id = ?")) {

                query.setInt(1, deletedProject.getId());

                try (ResultSet rs = query.executeQuery()) {

                    Assert.assertFalse(rs.next(), "Project that was supposed to be deleted is still in the database");

                }

            }

            Assert.assertTrue(account.getProjects().remove(deletedProject));

        }

    }

}
