package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import model.GameData;
import com.google.gson.JsonObject;

import exception.ResponseException;

import java.io.*;
import java.net.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register (String username, String password, String email) throws ResponseException {
        var path = "/user";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("email", email);
        var response =  this.makeRequest("POST", path, jsonObject, null, AuthData.class);
        this.authToken = response.authToken();
        return response;
    }

    public AuthData login (String username, String password) throws ResponseException {
        var path = "/session";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        var response = this.makeRequest("POST", path, jsonObject, null, AuthData.class);
        this.authToken = response.authToken();
        return response;
    }

    public void logout () throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, this.authToken, Object.class);
        this.authToken = null;
    }

    public GameData[] listGames () throws ResponseException {
        var path = "/game";
        record listGameResponse(Collection<GameData> games) { }
        var response = this.makeRequest("GET", path, null, authToken, listGameResponse.class);
        return response.games().toArray(new GameData[0]);
    }

    public void createNewGame (String gameName) throws ResponseException {
        var path = "/game";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gameName", gameName);
        this.makeRequest("POST", path, jsonObject, authToken, Object.class);
    }


    public void joinGame (String playerColor, int gameID) throws ResponseException {
        var path = "/game";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerColor", playerColor);
        jsonObject.addProperty("gameID", gameID);
        this.makeRequest("PUT", path, jsonObject, authToken, Object.class);
    }

    public void observeGame (int gameID) throws ResponseException {
        var path = "/game";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerColor", "OBSERVE");
        jsonObject.addProperty("gameID", gameID);
        this.makeRequest("PUT", path, jsonObject, authToken, Object.class);
    }

    public void clearApplication () throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, Object.class);
    }

    private <T> T makeRequest(String method, String path, Object request, String authorization, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authorization != null){
                http.addRequestProperty("authorization", authorization);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
