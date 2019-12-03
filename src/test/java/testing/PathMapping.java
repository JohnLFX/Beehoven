package testing;

import org.apache.http.client.methods.*;
import spark.route.HttpMethod;

public class PathMapping {

    private HttpMethod method;
    private String path;

    public PathMapping(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpUriRequest getRequest() {
        switch (method) {
            case get:
                return new HttpGet(TestState.BASE_URL + path);
            case post:
                return new HttpPost(TestState.BASE_URL + path);
            case put:
                return new HttpPut(TestState.BASE_URL + path);
            case patch:
                return new HttpPatch(TestState.BASE_URL + path);
            case delete:
                return new HttpDelete(TestState.BASE_URL + path);
            default:
                throw new UnsupportedOperationException("HTTP Method not implemented (" + method + ")");
        }
    }

}
