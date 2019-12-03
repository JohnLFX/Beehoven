package dev.roundtable.beehoven.handlers.chat;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ChatMessages {

    private static final LoadingCache<Integer, List<Message>> MESSAGE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<Integer, List<Message>>() {
                @Override
                public List<Message> load(@NotNull Integer account) {
                    return new ArrayList<>();
                }
            });

    public static List<Message> pollMessages(int projectID) {
        try {
            return MESSAGE_CACHE.get(projectID);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static void addMessage(int accountID, int projectID, String username, String message) {
        try {
            MESSAGE_CACHE.get(projectID).add(new Message(accountID, username, message, System.currentTimeMillis()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}

class Message implements Serializable {

    private static int NEXT_ID = 0;

    private final int id;
    private final int accountID;
    private final String username;
    private final String message;
    private final long time;

    Message(int accountID, String username, String message, long time) {
        this.id = NEXT_ID++;
        this.accountID = accountID;
        this.username = username;
        this.message = message;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getAccountID() {
        return accountID;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

}