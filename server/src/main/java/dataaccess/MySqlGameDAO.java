package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlGameDAO implements GameDAO {

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, json) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(new ChessGame());
        return executeUpdate(statement, null, null, gameName, json);
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
        executeUpdate(statement, username, gameID);
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
        executeUpdate(statement);
    }

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
