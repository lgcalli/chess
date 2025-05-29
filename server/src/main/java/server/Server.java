package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import service.Service;
import spark.*;
import chess.ChessGame;


public class Server {
    private final Service service;

    record RegisterRequest(
            String username,
            String password,
            String email){
    }

    record RegisterResponse(
            String username,
            String authToken){
    }

    record LoginRequest(
            String username,
            String password){
    }
    record LoginResponse(
            String authToken,
            String username){
    }
    record CreateGameRequest (
            String gameName){
    }
    record CreateGameResponse (
            Integer gameID){
    }

    record JoinGameRequest (
            ChessGame.TeamColor playerColor,
            Integer gameID){
    }

    public static class ListGamesResponse {
        public Collection<GameData> games;

        public ListGamesResponse(Collection<GameData> games) {
            this.games = games;
        }
    }

    public Server() {
        SharedDatabase database = new SharedDatabase();
        try {
            database.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        UserDAO userDAO = new MySqlUserDAO(database);
        AuthDAO authDAO = new MySqlAuthDAO(database);
        GameDAO gameDAO = new MySqlGameDAO(database);


        this.service = new Service(userDAO, authDAO, gameDAO);

    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //REGISTRATION
        Spark.post("/user", this::register);
        //LOGIN
        Spark.post("/session", this::login);
        //LOGOUT
        Spark.delete("/session", this::logout);
        //LIST GAMES
        Spark.get("/game", this::listGames);
        //CREATE GAME
        Spark.post("/game", this::createGame);
        //JOIN GAME
        Spark.put("/game", this::joinGame);
        //CLEAR APPLICATION
        Spark.delete("/db", this::clearApplication);
        //EXCEPTION
        Spark.exception(DataAccessException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint
        // Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), RegisterRequest.class);
        if (user.username == null || user.password == null || user.email == null){
            throw new DataAccessException(400, "Error: bad request");
        }
        UserData userData = new UserData (user.username, user.password, user.email);
        String authToken = service.register(userData);
        RegisterResponse registerResponse = new RegisterResponse(user.username, authToken);
        return new Gson().toJson(registerResponse);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        if (loginRequest.username == null || loginRequest.password == null){
            throw new DataAccessException(400, "Error: bad request");
        }
        String authToken = service.login(loginRequest.username, loginRequest.password);
        LoginResponse loginResponse = new LoginResponse(authToken, loginRequest.username);
        return new Gson().toJson(loginResponse);
    }

    private Object logout (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        if (authToken == null || authToken.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        service.logout(authToken);
        return "";
    }

    private Object listGames (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        if (authToken.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        return new Gson().toJson(new ListGamesResponse(service.listGames(authToken)));
    }

    private Object createGame (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        if (authToken == null || authToken.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        var createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        if (createGameRequest.gameName == null){
            throw new DataAccessException(400, "Error: bad request");
        }
        int gameID = service.createGame(authToken, createGameRequest.gameName);
        CreateGameResponse createGameResponse = new CreateGameResponse(gameID);
        return new Gson().toJson(createGameResponse);
    }

    private Object joinGame (Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        if (authToken == null || authToken.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        var joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        if (joinGameRequest.playerColor == null || joinGameRequest.gameID == null){
            throw new DataAccessException(400, "Error: bad request");
        }

        service.joinGame(joinGameRequest.gameID, authToken, joinGameRequest.playerColor);
        return "";
    }

    private Object clearApplication (Request req, Response res) throws DataAccessException {
        service.clearApplication();
        return "";
    }





}
