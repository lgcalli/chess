package dataAccess;

import exception.ResponseException;

public interface AuthDAO {
    String createAuth (String username) throws ResponseException;

    String getAuth (String authToken) throws ResponseException;

    void updateAuth (String authToken, String newAuth) throws ResponseException;

    void deleteAuth (String authToken) throws ResponseException;

    void clearAuth () throws ResponseException;
}
