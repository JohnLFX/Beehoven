package handlers;

import dev.roundtable.beehoven.Beehoven;
import dev.roundtable.beehoven.handlers.RegisterHandler;
import org.apache.commons.text.RandomStringGenerator;
import org.mockito.ArgumentCaptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import spark.HaltException;
import spark.Request;
import spark.Response;
import testing.Account;
import testing.TestState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.*;

public class TestRegister {

    private static RegisterHandler handler;

    @BeforeClass
    public static void setup() {
        handler = new RegisterHandler();
    }

    @Test
    public void testBadPayload() throws Exception {

        String[] parameters = new String[]{"first_name", "last_name", "username", "email", "password"};
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();

        for (int badIndex = 0; badIndex < parameters.length; badIndex++) {

            try {

                Request request = mock(Request.class);

                for (int i = 0; i < parameters.length; i++) {
                    if (i == badIndex) {
                        when(request.queryParams(parameters[i])).thenReturn(ThreadLocalRandom.current().nextBoolean() ? stringGenerator.generate(500) : null);
                    } else {
                        when(request.queryParams(parameters[i])).thenReturn(stringGenerator.generate(5, 16));
                    }
                }

                handler.handle(request, null);

                Assert.fail("Handler did not halt execution upon receiving a bad payload (Parameter " + parameters[badIndex] + ")");

            } catch (HaltException e) {
                Assert.assertEquals(e.statusCode(), 400, "Login failed successfully, but wrong status code was returned");
            }

        }

    }

    @Test(groups = {"TestRegister"})
    public void test() throws Exception {

        char[][] pairs = {{'a', 'z'}, {'0', '9'}, {'!', '?'}};
        RandomStringGenerator passwordGenerator = new RandomStringGenerator.Builder().withinRange(pairs).build();
        RandomStringGenerator nameGenerator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();

        for (int i = 0; i < TestState.ACCOUNTS_TO_GENERATE; i++) {

            String firstName = nameGenerator.generate(10, 20);
            String lastName = nameGenerator.generate(10, 20);
            String username = nameGenerator.generate(3, 16);
            String email = nameGenerator.generate(10) + "@gmail.com";
            String password = passwordGenerator.generate(6, 50);

            Request request = mock(Request.class);
            when(request.queryParams("first_name")).thenReturn(firstName + " "); // Random white space to test .trim()
            when(request.queryParams("last_name")).thenReturn(lastName);
            when(request.queryParams("username")).thenReturn(username);
            when(request.queryParams("email")).thenReturn(email);
            when(request.queryParams("password")).thenReturn(password);

            Response response = mock(Response.class);
            ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
            doNothing().when(response).status(statusCode.capture());

            handler.handle(request, response);
            Assert.assertEquals((int) statusCode.getValue(), 201, "Unexpected status code for proper account creation");

            handler.handle(request, response);
            Assert.assertEquals((int) statusCode.getValue(), 409, "Unexpected status code for duplicate account creation");

            try (Connection connection = Beehoven.getInstance().getConnection();
                 PreparedStatement query = connection.prepareStatement("SELECT id,email,username,display_name,password,salt" +
                         " FROM accounts WHERE username = ? AND email = ?")) {

                query.setString(1, username);
                query.setString(2, email);

                try (ResultSet rs = query.executeQuery()) {

                    Assert.assertTrue(rs.next(), "Account is not in database");

                    int accountID = rs.getInt(1);
                    Assert.assertTrue(accountID > 0);

                    Assert.assertEquals(email, rs.getString(2));
                    Assert.assertEquals(username, rs.getString(3));
                    Assert.assertEquals(firstName + " " + lastName, rs.getString(4));
                    Assert.assertFalse(rs.getString(5).isEmpty());
                    Assert.assertFalse(rs.getString(6).isEmpty());

                    TestState.ACCOUNTS.put(
                            accountID,
                            new Account(accountID, username, email, password)
                    );

                }

            }

        }

    }

}
