package dataAccess;
import java.util.HashMap;
import java.util.UUID;
import model.UserData;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> auth = new HashMap<>();

    public String createAuth(String username)  {
        String token = UUID.randomUUID().toString();
        auth.put(username, token);
        return token;
    }

    public String getAuth(String username)  {
        return auth.get(username);
    }

    public void updateAuth(String authToken, String newAuth) {

    }

    public void deleteAuth(String authToken) {

    }
    public void clearAuth()  {

    }
}
