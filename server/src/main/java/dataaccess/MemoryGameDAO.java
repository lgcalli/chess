package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;

import java.util.*;

public class MemoryGameDAO implements GameDAO{
    final private Collection <GameData> games = new ArrayList<>();
    final private HashMap <Integer, GameData> gamesByGameID = new HashMap<>();

    public int createGame(String gameName) {
        var game = new ChessGame();

        Random random = new Random();
        int gameID = random.nextInt(10000);
        while (gamesByGameID.get(gameID) != null){
            gameID = random.nextInt(10000);
        }
        GameData gameData = new GameData(gameID, null, null, gameName, game);
        games.add(gameData);
        gamesByGameID.put(gameID, gameData);
        return gameID;
    }

    public void updateGame (int gameID, String username, ChessGame.TeamColor color)  throws DataAccessException {
        GameData oldGameData = gamesByGameID.get(gameID);
        String white = oldGameData.whiteUsername();
        String black = oldGameData.blackUsername();
        if (oldGameData == null) {
            throw new DataAccessException(400, "Error: game does not exist");
        }

        if (color == ChessGame.TeamColor.WHITE) {
            if (white != null) {
                throw new DataAccessException(403, "Error: already has user");
            }
            white = username;
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (black != null) {
                throw new DataAccessException(403, "Error: already has user");
            }
            black = username;
        }

        GameData newGameData = new GameData(gameID, white, black, oldGameData.gameName(), oldGameData.game());
        games.remove(oldGameData);
        gamesByGameID.remove(gameID, oldGameData);
        games.add(newGameData);
        gamesByGameID.put(gameID, newGameData);
    }

    public Collection<GameData> listGames() {
        return games;
    }

    public void clearGames() {
        gamesByGameID.clear();
        games.clear();
    }

    public ChessGame getGame (int gameID) {
        return gamesByGameID.get(gameID).game();
    }

    public void updateGameBoard(int gameID, ChessGame game) {
        GameData oldGameData = gamesByGameID.get(gameID);
        GameData newGameData = new GameData(gameID, oldGameData.whiteUsername(), oldGameData.blackUsername(), oldGameData.gameName(), game);
        games.remove(oldGameData);
        gamesByGameID.remove(gameID, oldGameData);
        games.add(newGameData);
        gamesByGameID.put(gameID, newGameData);
    }

    public ChessGame.TeamColor getPlayerColor (int gameID, String username) throws DataAccessException {
        GameData gameData = gamesByGameID.get(gameID);
        if (gameData.whiteUsername().equals(username)){
            return ChessGame.TeamColor.WHITE;
        } else if (gameData.blackUsername().equals(username)){
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

}
