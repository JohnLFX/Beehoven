package testing;

import dev.roundtable.beehoven.Beehoven;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    @Test(priority = -1, groups = {"DatabaseSetup"})
    public void test() throws Exception {
        try (Connection connection = Beehoven.getInstance().getConnection()) {
            Assert.assertNotNull(connection, "Database connection is null");
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP TABLE IF EXISTS accounts");
                statement.executeUpdate("DROP TABLE IF EXISTS projects");
                initializeDB(statement);
            }
        }
    }

    private static void initializeDB(Statement statement) throws SQLException {

        statement.executeUpdate("create table if not exists accounts " +
                "                ( " +
                "                        id integer primary key autoincrement, " +
                "        email varchar(128) not null, " +
                "                username varchar(16) not null, " +
                "                display_name varchar(255) not null, " +
                "                token char(32) null, " +
                "                password char(64) not null, " +
                "                salt char(32) not null, " +
                "                last_login datetime null, " +
                "                constraint accounts_email_uindex " +
                "        unique (email), " +
                "                constraint accounts_token_uindex " +
                "        unique (token), " +
                "                constraint accounts_username_uindex " +
                "        unique (username) " +
                ");");

        statement.executeUpdate("create table if not exists projects " +
                "( " +
                " id integer primary key autoincrement, " +
                " owner int not null, " +
                " name varchar(255) null, " +
                " title varchar(255) null, " +
                " subtitle varchar(255) null, " +
                " artist varchar(255) null, " +
                " album varchar(255) null, " +
                " wordsBy varchar(255) null, " +
                " musicBy varchar(255) null, " +
                " score blob null, " +
                " constraint projects_accounts_id_fk " +
                "  foreign key (owner) references accounts (id) " +
                "   on delete cascade " +
                ");");

        statement.executeUpdate("create index if not exists projects_owner_index on projects (owner);");

        statement.executeUpdate("create table if not exists permissions " +
                "(" +
                "pid int not null," +
                "accountID int not null" +
                ");");

        statement.executeUpdate("create index if not exists permissions_accounts_id_fk on permissions (accountID);");

        statement.executeUpdate("create index if not exists permissions_projects_id_fk on permissions (pid);");

    }

}
