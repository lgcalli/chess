package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    final private Collection <ChessGame> games = new ArrayList<>();

    public void createGame(String gameName, String username) {

    }
    public GameData getGame(String gameName) {
        return null;
    }
    public void updateGame(int gameID, String username, ChessGame.TeamColor color) {

    }
    public void deleteGame(int gameID) {

    }
    public Collection<GameData> listGames() {
        return List.of();
    }
    public void clearGames() {
        games.clear();
    }
}
