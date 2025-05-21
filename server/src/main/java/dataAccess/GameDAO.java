package dataAccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame (String gameName, String username) throws ResponseException;

    GameData getGame (String gameName) throws ResponseException;

    void updateGame (int gameID, String username, ChessGame.TeamColor color) throws ResponseException;

    void deleteGame (int gameID) throws ResponseException;

    Collection<GameData> listGames () throws ResponseException;

    void clearGames () throws ResponseException;
}
