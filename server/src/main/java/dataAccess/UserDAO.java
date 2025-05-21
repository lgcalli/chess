package dataAccess;

import model.UserData;


public interface UserDAO {
    void createUser (UserData user) throws DataAccessException;

    UserData getUser (String username) throws DataAccessException;

    void updateUser (String username, String password, String newUsername, String newPassword) throws DataAccessException;

    void deleteUser (String username, String password) throws DataAccessException;

    void clearUser () throws DataAccessException;

}
