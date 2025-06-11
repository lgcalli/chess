package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;

    void updateGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void clearGames() throws DataAccessException;

    ChessGame getGame(int gameID) throws DataAccessException;

    void updateGameBoard(int gameID, ChessGame game) throws DataAccessException;

    ChessGame.TeamColor getPlayerColor (int gameID, String username) throws DataAccessException;
}
