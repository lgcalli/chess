package dataAccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO{
    final private Collection <GameData> games = new ArrayList<>();
    final private HashMap <Integer, GameData> gamesByGameID = new HashMap<Integer, GameData>();

    public int createGame(String gameName, String username) {
        var game = new ChessGame();
        Random random = new Random();
        int gameID = random.nextInt(10000);
        while (gamesByGameID.get(gameID) != null){
            gameID = random.nextInt(10000);
        }
        GameData gameData = new GameData(gameID, username, null, gameName, game);
        games.add(gameData);
        gamesByGameID.put(gameID, gameData);
        return gameID;
    }
    public GameData getGame(String gameName) {

        //game = serializer.fromJson(json, ChessGame.class);
        return null;
    }
    public void updateGame (int gameID, String username, ChessGame.TeamColor color) {

    }
    public void deleteGame(int gameID) {

    }
    public Collection<GameData> listGames() {
        return games;
    }
    public void clearGames() {
        gamesByGameID.clear();
        games.clear();
    }
}
