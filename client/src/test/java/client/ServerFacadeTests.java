package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.*;


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

    }

    @Test
    void loginPositive() throws Exception {

    }

    @Test
    void loginNegative() throws Exception {

    }

    @Test
    void logoutPositive() throws Exception {

    }

    @Test
    void logoutNegative() throws Exception {

    }

    @Test
    void listGamesPositive() throws Exception {

    }

    @Test
    void listGamesNegative() throws Exception {

    }

    @Test
    void createNewGamePositive() throws Exception {

    }

    @Test
    void createNewGameNegative() throws Exception {

    }

    @Test
    void joinGamePositive() throws Exception {

    }

    @Test
    void joinGameNegative() throws Exception {

    }

    @Test
    void observeGamePositive() throws Exception {

    }

    @Test
    void observeGameNegative() throws Exception {

    }

    @Test
    void clearApplicationPositive() throws Exception {

    }
}
