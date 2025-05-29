package dataaccess;

import java.sql.*;
import java.util.UUID;
import dataaccess.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlAuthDAO implements AuthDAO {
    SharedDatabase db;

    public MySqlAuthDAO(SharedDatabase db){
        this.db = db;
    }


    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        this.db.executeUpdate(statement, authToken, username);
        return authToken;
    }

    public String getUser(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        db.executeUpdate(statement, authToken);
    }

    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        db.executeUpdate(statement);
    }

}
