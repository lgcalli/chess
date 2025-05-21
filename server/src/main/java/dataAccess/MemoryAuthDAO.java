package dataAccess;
import java.util.HashMap;
import java.util.UUID;
import model.UserData;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, String> auth_to_user = new HashMap<>();
    final private HashMap<String, String> user_to_auth = new HashMap<>();

    public String createAuth(String username)  {
        String token = UUID.randomUUID().toString();
        auth_to_user.put(token, username);
        user_to_auth.put(username, token);
        return token;
    }

    public String getAuth(String username)  {
        return user_to_auth.get(username);
    }

    public String getUser(String auth){
        return auth_to_user.get(auth);
    }

    public void updateAuth(String authToken, String newAuth) {

    }

    public void deleteAuth(String authToken) {
        String username = auth_to_user.get(authToken);
        auth_to_user.remove(authToken, username);
        user_to_auth.remove(username, authToken);
    }
    public void clearAuth()  {

    }
}
