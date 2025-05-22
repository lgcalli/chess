package service;
import model.*;
import dataaccess.*;
import java.util.List;

import chess.ChessGame;

public class Service {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;
    private final GameDAO gameDataAccess;


    public Service(UserDAO userDataAccess, AuthDAO authDataAccess, GameDAO gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
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
            throw new DataAccessException(401, "Error: unauthorized");
        } else if (userDataAccess.getUser(username).password().equals(password)){
            return authDataAccess.createAuth(username);
        } else {
            throw new DataAccessException(401, "Error: unauthorized");
        }
    }
    public void logout(String authToken) throws DataAccessException {
        String username = authDataAccess.getUser(authToken);
        if (username == null || username.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        authDataAccess.deleteAuth(authToken);
    }

    public List<GameData> listGames (String authToken) throws DataAccessException {
        String username = authDataAccess.getUser(authToken);
        if (username == null || username.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        return gameDataAccess.listGames();
    }

    public int createGame (String authToken, String gameName) throws DataAccessException {
        String username = authDataAccess.getUser(authToken);
        if (username == null || username.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        } else {
            return gameDataAccess.createGame(gameName);
        }
    }

    public void joinGame (Integer gameID, String authToken, ChessGame.TeamColor clientColor) throws DataAccessException{
        String username = authDataAccess.getUser(authToken);
        if (username == null || username.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        } else {
            gameDataAccess.updateGame(gameID, username, clientColor);
        }
    }

    public void clearApplication () throws DataAccessException {
        userDataAccess.clearUser();
        authDataAccess.clearAuth();
        gameDataAccess.clearGames();
    }
}
