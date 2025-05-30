package service;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import dataaccess.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class ServiceUnitTests {
    private Service service;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        service = new Service(userDAO, authDAO, gameDAO);
    }

    //register positive
    @Test
    @DisplayName("Register New User")
    public void registerNewUser () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        Assertions.assertNotNull(authToken);
    }

    //register negative
    @Test
    @DisplayName("Register Same Username")
    public void registerTakenUsername () throws DataAccessException {
        UserData user = new UserData("bob123", "newPassword", "bobNewEmail@hotmail.com");
        service.register(user);
        Assertions.assertThrows(DataAccessException.class, () ->
                service.register(user));
    }

    //login positive
    @Test
    @DisplayName("Login User")
    public void loginUser () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        service.logout(authToken);
        authToken = service.login("bob123", "bob's_password");
        Assertions.assertNotNull(authToken);
    }

    //login negative
    @Test
    @DisplayName("Login Invalid Credentials")
    public void loginInvalidCredentials () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        service.logout(authToken);
        Assertions.assertThrows(DataAccessException.class, () ->
                service.login("bob123", "bob's_wrong_password"));
    }

    //logout positive
    @Test
    @DisplayName("Logout User")
    public void LogoutUser () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        Assertions.assertDoesNotThrow(() -> service.logout(authToken));
    }

    //logout negative
    @Test
    @DisplayName("Logout Already Logged Out")
    public void LogoutUserInvalid () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        service.logout(authToken);
        Assertions.assertThrows(DataAccessException.class, () ->
                service.logout(authToken));
    }

    //list games positive
    @Test
    @DisplayName("List Games")
    public void ListGames () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        service.createGame(authToken, "newGame");
        Assertions.assertNotNull(service.listGames(authToken));
    }

    //list games negative
    @Test
    @DisplayName("List Games Invalid Auth")
    public void ListGamesInvalidAuth () throws DataAccessException {
        var authToken = "myInvalidAuthToken";
        Assertions.assertThrows(DataAccessException.class, () ->
                service.listGames(authToken));
    }

    //create game positive
    @Test
    @DisplayName("Create Game")
    public void CreateGame () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        service.createGame(authToken, "newGame");

    }

    //create game negative
    @Test
    @DisplayName("Create Game Invalid Auth")
    public void CreateGameInvalidAuth () throws DataAccessException {
        var authToken = "myInvalidAuthToken";
        Assertions.assertThrows(DataAccessException.class, () ->
                service.createGame(authToken, "newGame"));
    }

    //join game positive
    @Test
    @DisplayName("Join Game")
    public void JoinGame () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        var id = service.createGame(authToken, "newGame");
        service.joinGame(id, authToken, ChessGame.TeamColor.WHITE);
        GameData expectedGame = new GameData(id, "bob123", null, "newGame", new ChessGame());
        Collection<GameData> expectedGames = new ArrayList<>();
        expectedGames.add(expectedGame);
        Collection<GameData> games = service.listGames(authToken);
        Assertions.assertEquals(expectedGames, games);
    }

    //join game negative
    @Test
    @DisplayName("Join Game Color Taken")
    public void JoinGameColorTaken () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        newUser = new UserData("jessie1345", "jessie's_password", "jessie@hotmail.com");
        var authToken2 = service.register(newUser);
        var id = service.createGame(authToken, "newGame");
        service.joinGame(id, authToken, ChessGame.TeamColor.WHITE);
        Assertions.assertThrows(DataAccessException.class, () ->
                service.joinGame(id, authToken2, ChessGame.TeamColor.WHITE));
    }

    //clear application positive
    @Test
    @DisplayName("Clear Application")
    public void ClearApplication () throws DataAccessException {
        UserData newUser = new UserData("bob123", "bob's_password", "bob@hotmail.com");
        var authToken = service.register(newUser);
        service.createGame(authToken, "newGame");
        service.clearApplication();

        Assertions.assertThrows(DataAccessException.class, () -> service.login("bob123", "bob's_password"));
        Assertions.assertThrows(DataAccessException.class, () -> service.listGames(authToken));
    }
}
