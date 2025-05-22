package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame (String gameName, String username) throws DataAccessException;

    GameData getGame (String gameName) throws DataAccessException;

    void updateGame (int gameID, String username, ChessGame.TeamColor color) throws DataAccessException;

    void deleteGame (int gameID) throws DataAccessException;

    Collection<GameData> listGames () throws DataAccessException;

    void clearGames () throws DataAccessException;
}
