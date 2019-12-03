package dev.roundtable.beehoven.handlers.project;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.roundtable.beehoven.Beehoven;
import org.jetbrains.annotations.NotNull;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

public class AccountAuthorizationHandler implements Filter {

    /**
     * Token -> Account ID cache
     */
    private final LoadingCache<String, Integer> accountTokenCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(@NotNull String token) throws Exception {
                    try (Connection connection = Beehoven.getInstance().getConnection()) {

                        try (PreparedStatement tokenQuery = connection.prepareStatement("SELECT id FROM accounts WHERE token = ?")) {

                            tokenQuery.setString(1, token);

                            try (ResultSet rs = tokenQuery.executeQuery()) {

                                if (rs.next()) {
                                    return rs.getInt(1);
                                } else {
                                    return null;
                                }

                            }

                        }

                    }
                }
            });

    @Override
    public void handle(Request request, Response response) throws Exception {

        if (!"application/json".equals(request.contentType()))
            Spark.halt(406, "Content-Type in payload is not supported");

        String authorization = request.headers("Authorization");

        if (authorization == null)
            Spark.halt(401, "Not logged in");

        int delimiter = authorization.indexOf(' ');

        String authType = authorization.substring(0, delimiter);

        if (!"Bearer".equals(authType))
            Spark.halt(400, "Unsupported authorization");

        String token = authorization.substring(delimiter + 1);

        if (token.length() != 32)
            Spark.halt(401, "Invalid token");

        Integer accountID = accountTokenCache.get(token);

        if (accountID == null)
            Spark.halt(401, "Unauthorized token");

        request.attribute("accountID", accountID);

    }

}
