package testing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestState {

    public static final int ACCOUNTS_TO_GENERATE = 3;
    public static final Map<Integer, Account> ACCOUNTS = new ConcurrentHashMap<>();
    public static final String BASE_URL = "http://localhost:4567";

}

