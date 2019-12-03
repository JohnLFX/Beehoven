package dev.roundtable.beehoven;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import dev.roundtable.beehoven.handlers.LoginHandler;
import dev.roundtable.beehoven.handlers.RegisterHandler;
import dev.roundtable.beehoven.handlers.chat.GetChatHandler;
import dev.roundtable.beehoven.handlers.chat.PostChatHandler;
import dev.roundtable.beehoven.handlers.project.*;
import dev.roundtable.beehoven.handlers.project.share.AddSharedUser;
import dev.roundtable.beehoven.handlers.project.share.RemoveSharedUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Beehoven {

    /**
     * Public static instance of Gson (JSON serialization and deserialization API)
     */
    public static final Gson GSON = new Gson();

    private static final Logger LOGGER = LoggerFactory.getLogger(Beehoven.class);
    private static final Beehoven INSTANCE = new Beehoven();

    /**
     * Database connection pool
     */
    @Nullable
    private HikariDataSource dataSource;

    private Beehoven() {

        Properties properties = null;
        try {
            properties = readProperties();
        } catch (IOException e) {
            getLogger().error("Failed to read properties", e);
            System.exit(-1);
        }

        // Set what IP address and port Spark (embedded web server) should bind to
        Spark.ipAddress(properties.getProperty("bind-address", "localhost"));
        Spark.port(Integer.parseInt(properties.getProperty("bind-port", "4567")));

        getLogger().info("Setting up database...");

        try {

            // Set up the database connection pool information
            this.dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(properties.getProperty("jdbc-url"));
            dataSource.setUsername(properties.getProperty("username"));
            dataSource.setPassword(properties.getProperty("password"));
            dataSource.addDataSourceProperty("cachePrepStmts", Boolean.getBoolean(properties.getProperty("cachePrepStmts")));
            dataSource.addDataSourceProperty("prepStmtCacheSize", Integer.valueOf(properties.getProperty("prepStmtCacheSize")));
            dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", Integer.valueOf(properties.getProperty("prepStmtCacheSqlLimit")));

        } catch (Exception e) {
            getLogger().error("Failed to setup database connection pooling", e);
            System.exit(-1);
        }

        // Set the index page to be a simple Hello World message
        Spark.get("/", (request, response) -> "Hello from Beehoven");

        // Provide the client with a generic error page upon a server error
        Spark.internalServerError((request, response) -> {
            getLogger().error("Internal server upon request " + request.protocol()
                    + " " + request.requestMethod() + " " + request.uri());
            return "";
        });

        /* Below are routes (HTTP paths) */

        Spark.post("/register", new RegisterHandler());
        Spark.post("/login", new LoginHandler());

        Spark.path("/project", () -> {

            // Spark.before is here to check user authorization for the following routes (/list, /score, etc.)
            Spark.before("/*", new AccountAuthorizationHandler());

            Spark.get("/list", new ListProjectsMetaHandler());
            Spark.get("/score", new GetProjectScoreHandler());
            Spark.put("/create", new CreateProjectHandler());
            Spark.get("/meta", new GetProjectMetaHandler());
            Spark.post("/update/meta", new UpdateProjectMetaHandler());
            Spark.post("/update/score", new UpdateProjectScoreHandler());
            Spark.delete("/", new DeleteProjectHandler());

            Spark.get("/chat", new GetChatHandler());
            Spark.post("/chat", new PostChatHandler());

            Spark.path("/share", () -> {
                Spark.post("/add", new AddSharedUser());
                Spark.post("/remove", new RemoveSharedUser());
            });

        });

        getLogger().info("Setup complete");

    }

    /**
     * <p>Reads the properties stored at the beehoven.properties file in the current working directory (usually the parent directory of the jar)</p>
     * <p>If the properties file does not exist, a default version will be saved to the disk for editing by an administrator.</p>
     *
     * @return The Properties loaded from the configuration file.
     * @throws IOException If an IOException occurred during reading/writing beehoven.properties
     */
    @NotNull
    private Properties readProperties() throws IOException {
        getLogger().info("Loading configuration...");

        Path configFile = Paths.get("beehoven.properties");

        if (Files.notExists(configFile)) {

            getLogger().warn(configFile + " does not exist, saving default beehoven.properties...");

            try (InputStream in = Beehoven.class.getResourceAsStream("/beehoven.properties")) {
                Files.copy(in, configFile);
            }

        }

        getLogger().info("Reading properties...");

        Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(configFile)) {
            properties.load(in);
        }

        return properties;
    }

    /**
     * Returns an active connection from the database connection pool
     *
     * @return An active connection
     * @throws SQLException Database error
     */
    @NotNull
    public Connection getConnection() throws SQLException {
        // Assert statement and not a null check because
        // getConnection() should only be called when
        // the database is ready
        assert dataSource != null;

        return dataSource.getConnection();
    }

    /**
     * Returns the application logger
     *
     * @return The logger
     */
    @NotNull
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Returns the singleton instance of Beehoven
     *
     * @return The singleton instance
     */
    @NotNull
    public static Beehoven getInstance() {
        return INSTANCE;
    }

    /**
     * Shuts down Beehoven. The database connection pool is destroyed and spark (embedded web server) is closed.
     */
    public void shutdown() {
        getLogger().info("Shutting down Beehoven...");

        getLogger().info("Shutting down Spark...");
        Spark.stop();

        getLogger().info("Shutting down database pooling...");
        if (this.dataSource != null)
            this.dataSource.close();

    }

}
