package dataAccess;

import exception.ResponseException;

public interface AuthDAO {
    void createAuth (String username) throws ResponseException;

    String getAuth (String authToken) throws ResponseException;

    void updateAuth (String authToken, String newAuth) throws ResponseException;

    void deleteAuth (String authToken) throws ResponseException;

    void clearAuth () throws ResponseException;
}
