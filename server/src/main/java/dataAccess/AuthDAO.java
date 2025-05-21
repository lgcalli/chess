package dataAccess;

public interface AuthDAO {
    String createAuth (String username) throws DataAccessException;

    String getAuth (String username) throws DataAccessException;

    String getUser (String auth) throws DataAccessException;

    void updateAuth (String authToken, String newAuth) throws DataAccessException;

    void deleteAuth (String authToken) throws DataAccessException;

    void clearAuth () throws DataAccessException;
}
