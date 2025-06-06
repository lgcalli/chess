package dataaccess;

import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO {
    SharedDatabase db;

    public MySqlAuthDAO(SharedDatabase db){
        this.db = db;
    }

    public String createAuth(String username) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException(400, "Username empty");
        }
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        this.db.executeUpdate(statement, authToken, username);
        return authToken;
    }

    public String getUser(String auth) throws DataAccessException {
        if (auth == null || auth.isEmpty()) {
            throw new DataAccessException(400, "Auth empty");
        }
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
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException(400, "Auth empty");
        }
        var statement = "DELETE FROM auth WHERE authToken=?";
        db.executeUpdate(statement, authToken);
    }

    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        db.executeUpdate(statement);
    }

}
