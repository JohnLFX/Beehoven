package handlers;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.handlers.project.GetProjectScoreHandler;
import dev.roundtable.beehoven.objects.Project;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Request;
import spark.Response;
import testing.Account;
import testing.TestState;

import java.util.Objects;

import static org.mockito.Mockito.*;

public class TestGetProjectScore {

    private static GetProjectScoreHandler handler;

    @BeforeClass(dependsOnGroups = {"TestRegister", "TestUpdateProjectScore"})
    public static void setup() {
        handler = new GetProjectScoreHandler();
    }

    @Test(dependsOnGroups = {"TestRegister", "TestUpdateProjectScore"})
    public void test() throws Exception {

        for (Account account : TestState.ACCOUNTS.values()) {

            for (Project project : account.getProjects()) {

                Request request = mock(Request.class);
                when(request.attribute("accountID")).thenReturn(account.getAccountID());
                when(request.queryParams("project_id")).thenReturn(String.valueOf(project.getId()));

                Response response = mock(Response.class);
                ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
                doNothing().when(response).status(statusCode.capture());

                String responseBody = Objects.toString(handler.handle(request, response));

                Assert.assertEquals((int) statusCode.getValue(), 200, "Unexpected status code for proper score request");

                Assert.assertNotNull(responseBody);
                Assert.assertFalse(responseBody.isEmpty());

                Project responseProject = Beehoven.GSON.fromJson(responseBody, Project.class);

                Assert.assertEquals(responseProject.getId(), project.getId(), "Project ID returned from server does not match expected");

            }

        }

    }

}
