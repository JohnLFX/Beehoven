package testing;

import dev.roundtable.beehoven.objects.Project;
import org.apache.commons.text.RandomStringGenerator;
import spark.Service;
import spark.Spark;
import spark.route.HttpMethod;
import spark.route.Routes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to support the other unit and integration tests
 */
public class TestUtils {

    private static final RandomStringGenerator STRING_GENERATOR;

    static {
        char[][] pairs = {{'a', 'z'}, {'0', '9'}};
        STRING_GENERATOR = new RandomStringGenerator.Builder().withinRange(pairs).build();
    }

    private TestUtils() {
    }

    /**
     * Generates a new project object populated with random, but valid, data.
     * The only field that is not set is the ID.
     *
     * @return The project object populated with data
     */
    public static Project randomProject() {

        Project project = new Project();
        project.setName(STRING_GENERATOR.generate(5, 255));
        project.setTitle(STRING_GENERATOR.generate(5, 255));
        project.setSubtitle(STRING_GENERATOR.generate(5, 255));
        project.setArtist(STRING_GENERATOR.generate(5, 255));
        project.setAlbum(STRING_GENERATOR.generate(5, 255));
        project.setWordsBy(STRING_GENERATOR.generate(5, 255));
        project.setMusicBy(STRING_GENERATOR.generate(5, 255));

        return project;

    }

    /**
     * <p>Fetches all paths registered in the current Spark instance.
     * An exception will be thrown if Spark has not been ignited prior to invocation.</p>
     * <p>Because this method heavily relies on reflection, the algorithm will
     * likely need frequent maintenance for different Spark versions.</p>
     *
     * @param includeNonHTTP If the list should include non-HTTP paths (such as "before /proect/*")
     * @return All paths found in the current Spark (embedded HTTP web server) instance
     * @throws NoSuchMethodException     If this algorithm does not support the current Spark version
     * @throws NoSuchFieldException      If this algorithm does not support the current Spark version
     * @throws ClassNotFoundException    If this algorithm does not support the current Spark version
     * @throws IllegalAccessException    If the JVM prevents reflection access
     * @throws InvocationTargetException If the method caused an unchecked exception
     */
    public static List<PathMapping> fetchAllRegisteredSparkPaths(boolean includeNonHTTP) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        Method sparkServiceGetter = Spark.class.getDeclaredMethod("getInstance");
        sparkServiceGetter.setAccessible(true);
        Service service = (Service) sparkServiceGetter.invoke(null);

        Field routesField = service.getClass().getDeclaredField("routes");
        routesField.setAccessible(true);
        Routes routes = (Routes) routesField.get(service);

        Field routeEntriesField = routes.getClass().getDeclaredField("routes");
        routeEntriesField.setAccessible(true);
        //noinspection unchecked
        List<Object> routeEntries = (List<Object>) routeEntriesField.get(routes);

        Class<?> routeEntryClass = Class.forName("spark.route.RouteEntry");

        Field httpMethodField = routeEntryClass.getDeclaredField("httpMethod");
        Field pathField = routeEntryClass.getDeclaredField("path");
        httpMethodField.setAccessible(true);
        pathField.setAccessible(true);

        List<PathMapping> results = new ArrayList<>();

        for (Object entry : routeEntries) {

            HttpMethod method = (HttpMethod) httpMethodField.get(entry);
            String path = (String) pathField.get(entry);

            if (!includeNonHTTP && (method == HttpMethod.before || method == HttpMethod.after
                    || method == HttpMethod.afterafter || method == HttpMethod.unsupported))
                continue;

            //System.out.println(method + " " + path);

            results.add(new PathMapping(method, path));

        }

        return results;

    }

}

