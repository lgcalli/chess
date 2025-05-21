package dataAccess;
import chess.ChessGame;
import exception.ResponseException;
import model.*;

import java.util.Collection;
import java.util.List;

public class MemoryDataAccess implements AuthDAO, GameDAO, UserDAO {

    public void createUser(String username, String password) throws ResponseException {

    }

    public UserData getUser(String username) throws ResponseException {
        return null;
    }

    public void updateUser(String username, String password, String newUsername, String newPassword) throws ResponseException {

    }

    public void deleteUser(String username, String password) throws ResponseException {

    }

    public void clearUser() throws ResponseException {

    }

    public void createAuth(String username) throws ResponseException {

    }

    public String getAuth(String authToken) throws ResponseException {
        return "";
    }

    public void updateAuth(String authToken, String newAuth) throws ResponseException {

    }

    public void deleteAuth(String authToken) throws ResponseException {

    }

    public void clearAuth() throws ResponseException {

    }

    public void createGame(String gameName, String username) throws ResponseException {

    }

    public GameData getGame(String gameName) throws ResponseException {
        return null;
    }

    public void updateGame(int gameID, String username, ChessGame.TeamColor color) throws ResponseException {

    }
    public void deleteGame(int gameID) throws ResponseException {

    }

    public Collection<GameData> listGames() throws ResponseException {
        return List.of();

    }
    public void clearGames() throws ResponseException {

    }
}
