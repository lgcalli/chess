package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.Service;
import spark.*;
import service.*;

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
        Spark.delete("/db", this::clearApplication);
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
        if (authToken.isEmpty()){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        service.logout(authToken);
        return "";
    }

    private Object clearApplication (Request req, Response res) throws DataAccessException {
        service.clearApplication();
        return "";
    }




}
