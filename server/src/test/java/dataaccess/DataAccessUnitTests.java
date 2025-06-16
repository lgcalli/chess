package dataaccess;
import chess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class DataAccessUnitTests {
    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static SharedDatabase db;

    @BeforeAll
    public static void setUpAll() {
        db = new SharedDatabase();
        authDAO = new MySqlAuthDAO(db);
        userDAO = new MySqlUserDAO(db);
        gameDAO = new MySqlGameDAO(db);
    }

    @BeforeEach
    public void setUp() throws DataAccessException {
        db.configureDatabase();
        authDAO.clearAuth();
        userDAO.clearUser();
        gameDAO.clearGames();
    }

    /*
        AUTH DAO
    */

    //createAuth (Positive)
    @Test
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
    public void getAuthUserPositive () throws DataAccessException {
        String auth = authDAO.createAuth("username");
        String user = authDAO.getUser(auth);
        Assertions.assertEquals("username", user);
    }

    //getUser (Negative)
    @Test
    public void getAuthUserNegative () throws DataAccessException {
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

    //clearAuth
    @Test
    public void clearAuth () throws DataAccessException {
        String authToken = authDAO.createAuth("username");
        authDAO.clearAuth();
        Assertions.assertNull(authDAO.getUser(authToken));
    }

    /*
        USER DAO
    */

    //createUser (Positive)
    @Test
    public void createUserPositive () throws DataAccessException {
        UserData userData = new UserData("createUserPositive", "password", "email");
        userDAO.createUser(userData);
        String username = "";
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.username());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        Assertions.assertEquals("createUserPositive", username);
    }

    //createUser (Negative)
    @Test
    public void createUserNegative () throws DataAccessException {
        UserData userData = new UserData("", "", "");
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(userData));
    }

    //verifyUser (Positive)
    @Test
    public void verifyUserPositive () throws DataAccessException {
        UserData userData = new UserData("verifyUserPositiveUser", "password", "newEmail");
        userDAO.createUser(userData);
        Assertions.assertDoesNotThrow(() -> userDAO.verifyUser("verifyUserPositiveUser", "password"));
        Assertions.assertTrue(userDAO.verifyUser("verifyUserPositiveUser", "password"));
    }

    //verifyUser (Negative)
    @Test
    public void verifyUserNegative () throws DataAccessException {
        UserData userData = new UserData("verifyUserNegativeUser", "password", "newEmail");
        userDAO.createUser(userData);
        Assertions.assertFalse(userDAO.verifyUser("verifyUserNegativeUser", "incorrect_password"));
    }

    //getUser (Positive)
    @Test
    public void getUserPositive () throws DataAccessException {
        UserData userData = new UserData("getUserPositiveUser", "password", "newEmail");
        userDAO.createUser(userData);
        UserData user = userDAO.getUser("getUserPositiveUser");
        Assertions.assertEquals(userData.username(), user.username());
    }

    //getUser (Negative)
    @Test
    public void getUserNegative () throws DataAccessException {
        Assertions.assertNull(userDAO.getUser(""));
    }


    //clearUser
    @Test
    public void clearUser () throws DataAccessException {
        UserData userData = new UserData("clearUser", "password", "newEmail");
        userDAO.createUser(userData);
        userDAO.clearUser();
        Assertions.assertNull(userDAO.getUser("clearUser"));
    }

    /*
        GAME DAO
    */

    //createGame (Positive)
    @Test
    public void createGamePositive () throws DataAccessException {
        gameDAO.createGame("game");
        int gameID = 1000;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM game WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, "game");
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        gameID = rs.getInt("gameID");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        Assertions.assertEquals(1, gameID);
    }

    //createGame (Negative)
    @Test
    public void createGameNegative () throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->gameDAO.createGame(""));
    }

    //updateGame (Positive)
    @Test
    public void updateGamePositive () throws DataAccessException {
        UserData userData = new UserData("updateGameUser", "password", "newEmail");
        userDAO.createUser(userData);
        String whiteUsername = "";
        int gameID = gameDAO.createGame("game");
        gameDAO.updateGame(gameID, "updateGameUser", ChessGame.TeamColor.WHITE);
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername FROM game WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, "game");
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        whiteUsername = rs.getString("whiteUsername");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        Assertions.assertEquals("updateGameUser", whiteUsername);
    }

    //updateGame (Negative)
    @Test
    public void updateGameNegative () throws DataAccessException {
        UserData userData = new UserData("updateGameUserWhite", "password", "newEmail");
        userDAO.createUser(userData);
        UserData userData2 = new UserData("updateGameUserWhiteError", "password", "newEmail");
        userDAO.createUser(userData2);
        int gameID = gameDAO.createGame("game");
        gameDAO.updateGame(gameID, userData.username(), ChessGame.TeamColor.WHITE);
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.updateGame(gameID, userData2.username(), ChessGame.TeamColor.WHITE));
    }

    //listGames (Positive)
    @Test
    public void listGamesPositive () throws DataAccessException {
        String gameName = "game";
        ChessGame newGame = new ChessGame();
        int gameID = gameDAO.createGame(gameName);
        GameData gameData = new GameData(gameID, null, null, gameName, newGame);
        Collection<GameData> newCollection = new ArrayList<>();
        newCollection.add(gameData);
        Assertions.assertEquals(gameDAO.listGames(), newCollection);
    }

    //listGames (Negative)
    @Test
    public void listGamesNegative () throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DROP TABLE IF EXISTS game";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.listGames());
        db.configureDatabase();
    }

    //clearGames (Positive)
    @Test
    public void clearGames () throws DataAccessException {
        int gameID = gameDAO.createGame("game");
        gameDAO.clearGames();
        Assertions.assertNull(gameDAO.getGame(gameID));
    }

    //getGame (Positive)
    @Test
    public void getGamePositive () throws DataAccessException {
        int gameID = gameDAO.createGame("game");
        ChessGame newGame = new ChessGame();
        ChessGame game = gameDAO.getGame(gameID);
        Assertions.assertEquals(newGame, game);
    }

    //getGame (Negative)
    @Test
    public void getGameNegative () throws DataAccessException {
        Assertions.assertNull(gameDAO.getGame(1500));
    }

    //updateGameBoard (Positive)
    @Test
    public void updateGameBoardPositive () throws DataAccessException {
        int gameID = gameDAO.createGame("game");
        ChessGame newGame = new ChessGame();
        ChessMove move = new ChessMove(new ChessPosition(2, 1), new ChessPosition(4, 1), null);
        try {
            newGame.makeMove(move);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        gameDAO.updateGameBoard(gameID, newGame);
        ChessGame game = gameDAO.getGame(gameID);
        Assertions.assertEquals(newGame, game);
    }

    //updateGameBoard (Negative)
    @Test
    public void updateGameBoardNegative () throws DataAccessException {
        int gameID = gameDAO.createGame("game");
        Assertions.assertThrows(DataAccessException.class, () ->gameDAO.updateGameBoard(gameID, null));
    }

    //getPlayerColor (Positive)
    @Test
    public void getPlayerColorPositive () throws DataAccessException {
        UserData userData = new UserData("whiteUser", "password", "newEmail");
        userDAO.createUser(userData);
        int gameID = gameDAO.createGame("game");
        gameDAO.updateGame(gameID, "whiteUser", ChessGame.TeamColor.WHITE);
        Assertions.assertEquals(ChessGame.TeamColor.WHITE, gameDAO.getPlayerColor(gameID, "whiteUser"));
    }

    //getPlayerColor (Negative)
    @Test
    public void getPlayerColorNegative () throws DataAccessException {
        int gameID = gameDAO.createGame("game");
        Assertions.assertNull(gameDAO.getPlayerColor(gameID, "non-existent"));
    }

    //leaveGame (Positive)
    @Test
    public void leaveGamePositive () throws DataAccessException {
        UserData userData = new UserData("updateGameUser", "password", "newEmail");
        userDAO.createUser(userData);
        String whiteUsername = "";
        int gameID = gameDAO.createGame("game");
        gameDAO.updateGame(gameID, "updateGameUser", ChessGame.TeamColor.WHITE);
        gameDAO.leaveGame(gameID, ChessGame.TeamColor.WHITE);
        Assertions.assertNull(gameDAO.getUserFromColor(gameID, ChessGame.TeamColor.WHITE));
    }

    //leaveGame (Negative)
    @Test
    public void leaveGameNegative () throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->gameDAO.leaveGame(1500, ChessGame.TeamColor.WHITE));
    }

    //getGameOver (Positive)
    @Test
    public void getGameOverPositive () throws DataAccessException {
        int gameID = gameDAO.createGame("game");
        Assertions.assertFalse(gameDAO.getGameOver(gameID));
    }

    //getGameOver (Negative)
    @Test
    public void getGameOverNegative () throws DataAccessException {
        Assertions.assertThrows(NullPointerException.class, () ->gameDAO.getGameOver(1500));
    }

    //setGameOver (Positive)
    @Test
    public void setGameOverPositive () throws DataAccessException {
        int gameID = gameDAO.createGame("game");
        gameDAO.setGameOver(gameID, true);
        Assertions.assertTrue(gameDAO.getGameOver(gameID));
    }

    //setGameOver (Negative)
    @Test
    public void setGameOverNegative () throws DataAccessException {

    }

    //getUserFromColor (Positive)
    @Test
    public void getUserFromColorPositive () throws DataAccessException {
        UserData userData = new UserData("getUserColor", "password", "newEmail");
        userDAO.createUser(userData);
        int gameID = gameDAO.createGame("game");
        gameDAO.updateGame(gameID, "getUserColor", ChessGame.TeamColor.WHITE);
        String username = gameDAO.getUserFromColor(gameID, ChessGame.TeamColor.WHITE);
        Assertions.assertEquals("getUserColor", username);
    }

    //getUserFromColor (Negative)
    @Test
    public void getUserFromColorNegative () throws DataAccessException {
        Assertions.assertNull(gameDAO.getUserFromColor(1500, ChessGame.TeamColor.WHITE));
    }
}
