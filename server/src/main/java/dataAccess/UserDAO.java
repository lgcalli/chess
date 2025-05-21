package dataAccess;

import exception.ResponseException;
import model.UserData;


public interface UserDAO {
    void createUser (String username, String password) throws ResponseException;

    UserData getUser (String username) throws ResponseException;

    void updateUser (String username, String password, String newUsername, String newPassword) throws ResponseException;

    void deleteUser (String username, String password) throws ResponseException;

    void clearUser () throws ResponseException;

}
