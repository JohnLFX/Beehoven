package misc;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import testing.PathMapping;
import testing.TestUtils;

import java.io.IOException;
import java.util.List;

/**
 * Tests that determine if account authorization is working correctly
 */
public class TestSparkAuthentication {

    private static HttpClient httpClient;
    private static List<PathMapping> paths;

    @BeforeClass(dependsOnGroups = {"DatabaseSetup"})
    public static void setup() throws Exception {
        httpClient = HttpClientBuilder.create().build();
        paths = TestUtils.fetchAllRegisteredSparkPaths(false);
    }

    /**
     * Tests each /project/* path to confirm that authorization is required
     *
     * @throws IOException If the test failed
     */
    @Test
    public void testUnauthorizedRequests() throws IOException {

        for (PathMapping pathMapping : paths) {

            if (pathMapping.getPath().startsWith("/project")) {

                HttpUriRequest request = pathMapping.getRequest();
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                Assert.assertEquals(response.getStatusLine().getStatusCode(), 401);
                EntityUtils.consume(response.getEntity());

            }

        }

    }

    /**
     * Tests each /project/* path to confirm that invalid tokens cause an unauthorized HTTP response
     *
     * @throws IOException If the test failed
     */
    @Test
    public void testInvalidToken() throws IOException {

        for (PathMapping pathMapping : paths) {

            if (pathMapping.getPath().startsWith("/project")) {

                HttpUriRequest request = pathMapping.getRequest();
                request.addHeader("Content-Type", "application/json");
                request.setHeader("Authorization", "Bearer z8r4j");
                HttpResponse response = httpClient.execute(request);

                int actualCode = response.getStatusLine().getStatusCode();

                Assert.assertEquals(actualCode, 401,
                        (actualCode == 500 ?
                                "500 server error response code, make sure the path mappings are correct"
                                : "Unexpected response code"
                        ));

                EntityUtils.consume(response.getEntity());

            }

        }

    }

}
