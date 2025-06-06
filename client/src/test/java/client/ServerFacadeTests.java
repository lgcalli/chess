package client;
import dataaccess.DataAccessException;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.*;

import javax.xml.crypto.Data;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws InterruptedException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void reset() throws Exception {
        facade.clearApplication();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(ResponseException.class, () ->
                facade.register("player1", "password2", "p1@email2.com"));
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.logout();
        var authData =  facade.login("player1", "password");
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginNegative() throws Exception {
        Assertions.assertThrows(ResponseException.class, () ->
                facade.login("player2", "password"));
    }

    @Test
    void logoutPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertDoesNotThrow(() -> facade.logout());
    }

    @Test
    void logoutNegative() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    void listGamesPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.createNewGame("gameName");
        Assertions.assertNotNull(facade.listGames());
    }

    @Test
    void listGamesNegative() throws Exception {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames());
    }

    @Test
    void createNewGamePositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertDoesNotThrow(() -> facade.createNewGame("gameName"));
    }

    @Test
    void createNewGameNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(ResponseException.class, () -> facade.createNewGame(null));
    }

    @Test
    void joinGamePositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.createNewGame("gameName");
        Assertions.assertDoesNotThrow(() -> facade.joinGame("WHITE", 1));
    }

    @Test
    void joinGameNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.createNewGame("gameName");
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame("WHITE", 3));
    }

    @Test
    void observeGamePositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.createNewGame("gameName");
        Assertions.assertDoesNotThrow(() -> facade.observeGame(1));
    }

    @Test
    void observeGameNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(ResponseException.class, () ->  facade.observeGame(3));
    }

    @Test
    void clearApplicationPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.logout();
        facade.clearApplication();
        Assertions.assertDoesNotThrow(() -> facade.register("player1", "password", "p1@email.com"));
    }
}
