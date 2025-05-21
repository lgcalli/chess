package dataAccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO{

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
