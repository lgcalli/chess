package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;


public interface UserDAO {
    void createUser (UserData user) throws DataAccessException;

    UserData getUser (String username) throws DataAccessException;

    boolean verifyUser (String username, String password) throws DataAccessException;

    void clearUser () throws DataAccessException;

}
