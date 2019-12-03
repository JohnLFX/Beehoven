package misc;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import testing.TestState;

import java.io.IOException;

public class TestSparkMappings {

    private static HttpClient httpClient;

    @BeforeClass
    public static void setup() {
        httpClient = HttpClientBuilder.create().build();
    }

    @Test
    public void testIndex() throws Exception {

        HttpResponse response = httpClient.execute(new HttpGet(TestState.BASE_URL + "/"));
        String content = EntityUtils.toString(response.getEntity());

        Assert.assertEquals("Hello from Beehoven", content);

    }

    @Test
    public void testWebSocket() throws Exception {

    }

    @Test
    public void test404() throws Exception {

        HttpResponse response = httpClient.execute(new HttpGet(TestState.BASE_URL + "/cnaYKNEWawPV3u4rwWD5qBHu"));
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());

    }

    @Test
    public void testBadContentType() throws IOException {

        HttpGet request = new HttpGet(TestState.BASE_URL + "/project/list");
        HttpResponse response = httpClient.execute(request);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 406);
        EntityUtils.consume(response.getEntity());

    }

}
