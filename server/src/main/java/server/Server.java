package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.UserData;
import service.Service;
import spark.*;
import service.*;

public class Server {
    private final Service service;

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
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

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
        String authToken = service.register(user);
        return new Gson().toJson(authToken);
    }

    private Object login(Request req, Response res) throws DataAccessException {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        String authToken = service.login(loginRequest.username, loginRequest.password);
        LoginResponse loginResponse = new LoginResponse(authToken, loginRequest.username);
        return new Gson().toJson(loginResponse);
    }

    private Object logout (Request req, Response res) throws DataAccessException {
        var authToken = new Gson().fromJson(req.body(), auth.class);
        service.logout(authToken.authorization);
        return "";
    }




}
