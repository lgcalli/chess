package dataAccess;
import java.util.HashMap;
import java.util.UUID;
import exception.ResponseException;
import model.UserData;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> auth = new HashMap<>();

    public String createAuth(String username) throws ResponseException {
        String token = UUID.randomUUID().toString();
        auth.put(token, username);
        return token;
    }

    public String getAuth(String authToken) throws ResponseException {
        return auth.get(authToken);
    }

    public void updateAuth(String authToken, String newAuth) throws ResponseException {

    }

    public void deleteAuth(String authToken) throws ResponseException {

    }
    public void clearAuth() throws ResponseException {

    }
}
