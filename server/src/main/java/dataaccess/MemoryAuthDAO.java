package dataaccess;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> authToUser = new HashMap<>();

    public String createAuth(String username)  {
        String token = UUID.randomUUID().toString();
        authToUser.put(token, username);
        return token;
    }

    public String getUser(String auth){
        return authToUser.get(auth);
    }

    public void deleteAuth(String authToken) {
        String username = authToUser.get(authToken);
        authToUser.remove(authToken, username);
    }
    public void clearAuth()  {
        authToUser.clear();
    }
}
