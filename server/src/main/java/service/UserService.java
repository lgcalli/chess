package service;
import model.*;
import dataAccess.*;

public class UserService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public String register(UserData user) throws DataAccessException {
        if (userDataAccess.getUser(user.username()) != null){
            throw new DataAccessException(403, "Error: already taken");
        }
        userDataAccess.createUser(user);
        return authDataAccess.createAuth(user.username());
    }

    public String login(String username, String password) throws DataAccessException {
        if (userDataAccess.getUser(username) == null){
            throw new DataAccessException(500, "Error: aser does not exist");
        } else if (!authDataAccess.getAuth(username).isEmpty()){
            throw new DataAccessException(500, "Error: already logged in");
        } else if (userDataAccess.getUser(username).password().equals(password)){
            return authDataAccess.createAuth(username);
        } else {
            throw new DataAccessException(401, "Error: unauthorized");
        }
    }
    public void logout(String authtoken) throws DataAccessException {

    }
}
