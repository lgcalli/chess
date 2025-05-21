package service;
import model.*;
import dataAccess.*;
import exception.*;

public class UserService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public String register(UserData user) throws ResponseException {
        if (userDataAccess.getUser(user.username()) != null){
            throw new ResponseException(403, "Error: Already taken");
        }
        userDataAccess.createUser(user);
        return authDataAccess.createAuth(user.username());
    }

    public String login(String username, String password) throws ResponseException {
        return null;
    }
    public void logout(String authtoken) throws ResponseException {

    }
}
