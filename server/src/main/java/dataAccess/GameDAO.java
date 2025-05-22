package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    int createGame (String gameName) throws DataAccessException;

    GameData getGame (int gameID) throws DataAccessException;

    void updateGame (int gameID, String username, ChessGame.TeamColor color) throws DataAccessException;

    void deleteGame (int gameID) throws DataAccessException;

    List<GameData> listGames () throws DataAccessException;

    void clearGames () throws DataAccessException;
}
