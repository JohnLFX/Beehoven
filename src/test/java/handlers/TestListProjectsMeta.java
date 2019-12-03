package handlers;

import com.google.gson.reflect.TypeToken;
import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.handlers.project.ListProjectsMetaHandler;
import dev.roundtable.beehoven.objects.Project;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.Request;
import spark.Response;
import testing.Account;
import testing.TestState;

import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

public class TestListProjectsMeta {

    private static ListProjectsMetaHandler handler;

    @BeforeClass
    public static void setup() {
        handler = new ListProjectsMetaHandler();
    }

    @Test(dependsOnGroups = {"TestRegister", "TestCreateProject"})
    public void test() throws Exception {

        for (Account account : TestState.ACCOUNTS.values()) {

            Request request = mock(Request.class);
            when(request.attribute("accountID")).thenReturn(account.getAccountID());

            Response response = mock(Response.class);
            ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
            doNothing().when(response).status(statusCode.capture());

            String responseBody = Objects.toString(handler.handle(request, response));

            Assert.assertEquals((int) statusCode.getValue(), 200, "Unexpected status code for proper score request");

            Assert.assertNotNull(responseBody);
            Assert.assertFalse(responseBody.isEmpty());

            List<Project> projectList = Beehoven.GSON.fromJson(responseBody, new TypeToken<List<Project>>() {
            }.getType());

            Assert.assertEquals(projectList, account.getProjects());

        }

    }

}
