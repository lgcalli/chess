package model;
import chess.ChessGame;

public record GameData(int gameID,
                       String whiteUsername,
                       String blackUsername,
                       String gameName,
                       ChessGame game) {

    public GameData setWhiteUser (String username) {
        return new GameData(this.gameID, username, this.blackUsername, this.gameName, this.game);
    }
    public GameData setBlackUser (String username) {
        return new GameData(this.gameID, this.whiteUsername, username, this.gameName, this.game);
    }

}