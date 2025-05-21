package dataAccess;

import exception.ResponseException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData user) throws ResponseException {
        users.put(user.username(), user);
    }
    public UserData getUser(String username) throws ResponseException {
        return users.get(username);
    }
    public void updateUser(String username, String password, String newUsername, String newPassword) throws ResponseException {

    }
    public void deleteUser(String username, String password) throws ResponseException {

    }
    public void clearUser() throws ResponseException {

    }
}
