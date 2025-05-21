package server;

import com.google.gson.Gson;
import model.UserData;
import spark.*;
import service.*;
import dataAccess.DataAccessException;

import javax.xml.crypto.Data;

public class Server {
    private UserService userService;
    private AuthService authService;
    private GameService gameService;

    record LoginRequest(
            String username,
            String password){
    }
    record LoginResponse(
            String authToken,
            String username){
    }

    record auth(
            String authorization){
    }


    public Server() {
    }

    public Server(UserService userService, AuthService authService, GameService gameService) {
        this.userService = userService;
        this.authService = authService;
        this.gameService = gameService;
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
        //Spark.get("/game", this::listGames);
        //CREATE GAME

        //JOIN GAME

        //CLEAR APPLICATION
        //Spark.delete("/db", this::)
        //EXCEPTION
        Spark.exception(DataAccessException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        String authToken = userService.register(user);
        return new Gson().toJson(authToken);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        String authToken = userService.login(loginRequest.username, loginRequest.password);
        LoginResponse loginResponse = new LoginResponse(authToken, loginRequest.username);
        return new Gson().toJson(loginResponse);
    }

    private Object logout (Request req, Response res) throws DataAccessException {
        var authToken = new Gson().fromJson(req.body(), auth.class);

        return "";
    }




}
