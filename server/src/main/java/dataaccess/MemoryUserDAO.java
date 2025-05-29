package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    public boolean verifyUser(String username, String password) {
        return getUser(username).password().equals(password);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }
    public void clearUser() {
        users.clear();
    }
}
