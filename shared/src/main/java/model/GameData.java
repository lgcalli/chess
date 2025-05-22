package model;
import chess.ChessGame;

public record GameData(int gameID,
                       String whiteUsername,
                       String blackUsername,
                       String gameName,
                       ChessGame game) {

    public void setWhiteUser (String username) {
        new GameData(this.gameID, username, this.blackUsername, this.gameName, this.game);
    }
    public void setBlackUser (String username) {
        new GameData(this.gameID, this.whiteUsername, username, this.gameName, this.game);
    }

}