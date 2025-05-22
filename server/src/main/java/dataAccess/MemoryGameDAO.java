package dataAccess;

import model.GameData;
import chess.ChessGame;

import java.util.*;

public class MemoryGameDAO implements GameDAO{
    final private List <GameData> games = new ArrayList<>();
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
    public GameData getGame(int gameID) {
        return gamesByGameID.get(gameID);
    }

    public void updateGame (int gameID, String username, ChessGame.TeamColor color) {
        GameData gameData = gamesByGameID.get(gameID);
        games.remove(gameData);
        gamesByGameID.remove(gameID, gameData);
        if (color == ChessGame.TeamColor.WHITE){
            gameData.setWhiteUser(username);
        } else if (color == ChessGame.TeamColor.BLACK){
            gameData.setBlackUser(username);
        }
        gamesByGameID.put(gameID, gameData);
        games.add(gameData);
    }
    public void deleteGame(int gameID) {

    }
    public List<GameData> listGames() {


        return games;
    }
    public void clearGames() {
        gamesByGameID.clear();
        games.clear();
    }
}
