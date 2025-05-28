package service;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import dataaccess.*;
import service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    }

    //login negative

    //logout positive

    //logout negative

    //list games positive

    //list games negative

    //create game positive

    //create game negative

    //join game positive

    //join game negative

    //clear application positive

}
