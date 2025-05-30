package dataaccess;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;
import service.Service;

public class DataAccessTests {
    private static final TestUser TEST_USER = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");
    private static TestServerFacade serverFacade;
    private static Server server;
    private static Class<?> databaseManagerClass;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @BeforeEach
    public void setUp() {
        SharedDatabase db = new SharedDatabase();
        UserDAO userDAO = new MySqlUserDAO(db);
        AuthDAO authDAO = new MySqlAuthDAO(db);
        GameDAO gameDAO = new MySqlGameDAO(db);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //AuthDAO


    //UserDAO

    //GameDAO



}
