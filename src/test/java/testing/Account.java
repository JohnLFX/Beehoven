package testing;

import dev.roundtable.beehoven.objects.Project;

import java.util.ArrayList;
import java.util.List;

public class Account {

    private int accountID;
    private List<Project> projects;
    private String username, email, password, token;

    public Account(int accountID, String username, String email, String password) {
        this.accountID = accountID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.projects = new ArrayList<>();
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
