package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;


public class MySqlGameDAO implements GameDAO {
    SharedDatabase db;

    public MySqlGameDAO(SharedDatabase db){
        this.db = db;
    }


    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(new ChessGame());
        return db.executeUpdate(statement, null, null, gameName, json);
    }

    public void updateGame(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {
        var statement = "";
        String whiteUsername = null;
        String blackUsername = null;

        try (var conn = DatabaseManager.getConnection()) {
            statement = "SELECT whiteUsername, blackUsername FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        whiteUsername = rs.getString("whiteUsername");
                        blackUsername = rs.getString("blackUsername");
                    } else {
                        throw new DataAccessException(400, "Error: game does not exist");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (color == ChessGame.TeamColor.WHITE){
            if (whiteUsername == null) {
                statement = "UPDATE game SET whiteUsername = ? WHERE gameID = ?";
            } else {
                throw new DataAccessException(403, "Error: already has user");
            }
        } else {
            if (blackUsername == null) {
                statement = "UPDATE game SET blackUsername = ? WHERE gameID = ?";
            } else {
                throw new DataAccessException(403, "Error: already has user");
            }
        }
        db.executeUpdate(statement, username, gameID);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var game = new Gson().fromJson(rs.getString("json"), ChessGame.class);
                        GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                        result.add(gameData);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE game";
        db.executeUpdate(statement);
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        var statement = "SELECT json FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Gson().fromJson(rs.getString("json"), ChessGame.class);
                } else {
                    return null;  // Game with that ID does not exist
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, "Unable to fetch game: " + e.getMessage());
        }
    }

    public void updateGameBoard(int gameID, ChessGame game) throws DataAccessException {
        if (game == null){
            throw new DataAccessException(500, "Error: empty game");
        }
        var statement = "UPDATE game SET json = ? WHERE gameID = ?";
        Gson gson = new Gson();
        String json = gson.toJson(game);
        db.executeUpdate(statement, json, gameID);
    }

    public ChessGame.TeamColor getPlayerColor (int gameID, String username) throws DataAccessException {
        var statement = "SELECT whiteUsername, blackUsername FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    if (username.equals(white)) {
                        return ChessGame.TeamColor.WHITE;
                    } else if (username.equals(black)) {
                        return ChessGame.TeamColor.BLACK;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, "Unable to fetch game: " + e.getMessage());
        }
    }

    public void leaveGame(int gameID, ChessGame.TeamColor color) throws DataAccessException {
        String statement = null;
        if (color == ChessGame.TeamColor.WHITE){
            statement = "UPDATE game SET whiteUsername = NULL WHERE gameID = ?";
        } else {
            statement = "UPDATE game SET blackUsername = NULL WHERE gameID = ?";

        }
        db.executeUpdate(statement, gameID);
    }

    public boolean getGameOver(int gameID) throws DataAccessException {
        ChessGame game = getGame(gameID);
        return game.getGameOver();
    }

    public void setGameOver (int gameID, boolean isGameOver) throws DataAccessException {
        ChessGame game = getGame(gameID);
        game.setGameOver(isGameOver);
        updateGameBoard(gameID, game);
    }

    public String getUserFromColor (int gameID, ChessGame.TeamColor color) throws DataAccessException {
        var statement = "SELECT whiteUsername, blackUsername FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    if (color.equals(ChessGame.TeamColor.WHITE)) {
                        return white;
                    } else if (color.equals(ChessGame.TeamColor.BLACK)) {
                        return black;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, "Unable to fetch game: " + e.getMessage());
        }
    }


}
