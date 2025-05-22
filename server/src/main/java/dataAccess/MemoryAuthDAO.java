package dataAccess;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> auth_to_user = new HashMap<>();

    public String createAuth(String username)  {
        String token = UUID.randomUUID().toString();
        auth_to_user.put(token, username);
        return token;
    }

    public String getUser(String auth){
        return auth_to_user.get(auth);
    }

    public void deleteAuth(String authToken) {
        String username = auth_to_user.get(authToken);
        auth_to_user.remove(authToken, username);
    }
    public void clearAuth()  {
        auth_to_user.clear();
    }
}
