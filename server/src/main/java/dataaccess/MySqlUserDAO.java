package dataaccess;

import model.UserData;

import java.sql.*;

import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO {

    public void createUser(UserData user) throws DataAccessException {
        String username = user.username();
        String password = user.password();
        String email = user.email();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?,?)";
        executeUpdate(statement, username, hashedPassword, email);
    }

    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        String hashedPassword = null;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hashedPassword = rs.getString("password");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }

        if (hashedPassword == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public UserData getUser(String username) throws DataAccessException {
        String password = "";
        String email = "";

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                       password = rs.getString("password");
                       email = rs.getString("password");
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return new UserData(username, password, email);
    }

    public void clearUser() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
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
