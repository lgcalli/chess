package dataaccess;

import model.UserData;
import dataaccess.DatabaseManager;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;
import service.Service;

import java.sql.SQLException;

public class DataAccessUnitTests {
    private static final UserData user = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private SharedDatabase db;

    @BeforeEach
    public void setUp() {
        db = new SharedDatabase();
        authDAO = new MySqlAuthDAO(db);
        userDAO = new MySqlUserDAO(db);
        gameDAO = new MySqlGameDAO(db);
    }

    /*
        AUTH DAO
    */

    //createAuth (Positive)
    @Test
    @DisplayName("CreateAuth")
    public void createAuthPositive () throws DataAccessException {
        String username = "helloThere";
        String authToken = authDAO.createAuth(username);
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        Assertions.assertEquals(username, "helloThere");
    }

    //createAuth (Negative)
    @Test
    public void createAuthNegative () throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(""));
    }

    //getUser (Positive)
    @Test
    public void getUserPositive () throws DataAccessException {
        String auth = authDAO.createAuth("username");
        String user = authDAO.getUser(auth);
        Assertions.assertEquals("username", user);
    }

    //getUser (Negative)
    @Test
    public void getUserNegative () throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getUser(""));
    }

    //deleteAuth (Positive)
    @Test
    public void deleteAuthPositive () throws DataAccessException {
        String auth = authDAO.createAuth("username");
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth(auth));
    }

    //deleteAuth (Negative)
    @Test
    public void deleteAuthNegative () throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.deleteAuth(""));
    }

    //clearAuth (Positive)



    /*
        USER DAO
    */

    //createUser (Positive)

    //createUser (Negative)

    //verifyUser (Positive)

    //verifyUser (Negative)

    //getUser (Positive)

    //getUser (Negative)

    //clearUser (Positive)

    /*
        GAME DAO
    */

    //createGame (Positive)

    //createGame (Negative)

    //updateGame (Positive)

    //updateGame (Negative)

    //listGames (Positive)

    //listGames (Negative)

    //clearGames (Positive)

}
