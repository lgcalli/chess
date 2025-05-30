package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlUserDAO implements UserDAO {
    SharedDatabase db;

    public MySqlUserDAO(SharedDatabase db){
        this.db = db;
    }


    public void createUser(UserData user) throws DataAccessException {
        String username = user.username();
        String password = user.password();
        String email = user.email();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?,?)";
        db.executeUpdate(statement, username, hashedPassword, email);
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
        db.executeUpdate(statement);
    }

}
