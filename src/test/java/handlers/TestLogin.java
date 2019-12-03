package handlers;

import dev.roundtable.beehoven.handlers.LoginHandler;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.HaltException;
import spark.Request;
import spark.Response;
import testing.Account;
import testing.TestState;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;

public class TestLogin {

    private static LoginHandler handler;

    @BeforeClass
    public static void setup() {
        handler = new LoginHandler();
    }

    @Test(groups = "TestLogin", dependsOnGroups = {"TestRegister"})
    public void testIncorrectCredentials() throws Exception {

        for (Account account : TestState.ACCOUNTS.values()) {

            Request request = mock(Request.class);
            when(request.queryParams("username")).thenReturn(account.getUsername());
            when(request.queryParams("password")).thenReturn(String.valueOf(ThreadLocalRandom.current().nextDouble()));

            Response response = mock(Response.class);
            ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
            doNothing().when(response).status(statusCode.capture());

            try {

                handler.handle(request, response);

                Assert.fail("Login with incorrect credentials appeared to have succeeded");

            } catch (HaltException e) {
                Assert.assertEquals(e.statusCode(), 401, "Login failed successfully, but wrong status code was returned");
            }

        }

    }

    @Test
    public void testNonExistingCredentials() throws Exception {

        Request request = mock(Request.class);
        when(request.queryParams("username")).thenReturn("CD4cy2UygnXHuAhE");
        when(request.queryParams("password")).thenReturn(ThreadLocalRandom.current().nextDouble() + "y2g4cU");

        Response response = mock(Response.class);
        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        doNothing().when(response).status(statusCode.capture());

        try {

            handler.handle(request, response);

            Assert.fail("Login with non-existent credentials appeared to have succeeded");

        } catch (HaltException e) {
            Assert.assertEquals(e.statusCode(), 401, "Login failed successfully, but wrong status code was returned");
        }

    }

    @Test(groups = "TestLogin", dependsOnGroups = {"TestRegister"})
    public void test() throws Exception {

        for (Account account : TestState.ACCOUNTS.values()) {

            Request request = mock(Request.class);
            when(request.queryParams("username")).thenReturn(account.getUsername());
            when(request.queryParams("password")).thenReturn(account.getPassword());

            Response response = mock(Response.class);
            ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
            doNothing().when(response).status(statusCode.capture());

            String responseToken = Objects.toString(handler.handle(request, response));

            Assert.assertEquals((int) statusCode.getValue(), 200, "Unexpected status code for proper login credentials");
            Assert.assertEquals(responseToken.length(), 58, "Unexpected response length");

            account.setToken(responseToken);

        }

    }

}
