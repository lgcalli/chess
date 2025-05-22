package dataAccess;

public interface AuthDAO {
    String createAuth (String username) throws DataAccessException;

    String getUser (String auth) throws DataAccessException;

    void deleteAuth (String authToken) throws DataAccessException;

    void clearAuth () throws DataAccessException;
}
