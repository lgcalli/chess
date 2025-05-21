package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import spark.*;
import service.*;
import dataAccess.DataAccessException;

import javax.xml.crypto.Data;

public class Server {
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

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
        //Spark.post("/session", this::login);
        //LOGOUT
        //Spark.delete("/session", this::logout);
        //LIST GAMES
        //Spark.get("/game", this::listGames);
        //CREATE GAME

        //JOIN GAME

        //CLEAR APPLICATION


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) throws ResponseException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        String authToken = userService.register(user);
        return new Gson().toJson(authToken);
    }


}
