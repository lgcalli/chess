package ui;

import static ui.EscapeSequences.*;

import client_web_socket.NotificationHandler;
import client_web_socket.WebSocketFacade;
import exception.ResponseException;
import model.GameData;

import java.lang.reflect.Array;
import java.util.*;

public class PostLogin {
    private final Scanner scanner;
    private final ServerFacade server;
    private final String serverUrl;
    private final String username;


    public PostLogin(Scanner scanner, ServerFacade server, String serverUrl, String username) {
        this.scanner = scanner;
        this.server = server;
        this.serverUrl = serverUrl;
        this.username = username;
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_YELLOW + "Welcome " + username + "!");
        System.out.print(help());

        var result = "";
        while (!result.equals("logout")) {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(RESET_TEXT_COLOR + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }
        }
    }

    private String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                server.createNewGame(params[0]);
            } catch (ResponseException e) {
                return SET_TEXT_COLOR_RED + "\tFailed to create game";
            }
            return "Game created";
        } else {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tExpected: create <NAME>");
        }
    }

    public String listGames() throws ResponseException {
        String output = String.format("%-5s %-20s %-20s %-20s\n", "ID", "NAME", "WHITE USER", "BLACK USER");
        GameData[] games = server.listGames();
        for (GameData game : games) {
            output = output + String.format(
                    "%-5d %-20s %-20s %-20s\n",
                    game.gameID(),
                    game.gameName(),
                    game.whiteUsername() != null ? game.whiteUsername() : "-",
                    game.blackUsername() != null ? game.blackUsername() : "-"
            );
        }
        return output;
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            try {
                server.joinGame(params[1], Integer.parseInt(params[0]));
            } catch (ResponseException e) {
                return SET_TEXT_COLOR_RED + "\tFailed to join game";
            }
            Gameplay gameplay = new Gameplay(this.scanner, this.server, this.serverUrl, Integer.parseInt(params[0]), params[1]);
            gameplay.run();
            return help();
        } else {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tExpected: join <ID> [WHITE/BLACK]");
        }
    }

    public String observeGame(String... params) throws ResponseException {
        GameData[] games = server.listGames();
        ArrayList <Integer> gameIDs = new ArrayList<>();
        for (GameData game:games){
            gameIDs.add(game.gameID());
        }
        if (params.length != 1) {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tExpected: join <ID> [WHITE/BLACK]");
        } else {
            int gameId;
            try {
                gameId = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tInvalid game ID");
            }
            if (!gameIDs.contains(gameId)){
                throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tInvalid game ID");
            }
            Gameplay gameplay = new Gameplay(this.scanner, this.server, this.serverUrl, Integer.parseInt(params[0]), "observe");
            gameplay.run();
            return help();
        }
    }

    public String logout() throws ResponseException {
        try {
            server.logout();
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "\tFailed to logout";
        }
        return "logout";
    }

    private String help() {
        String output = "\n\t" + SET_TEXT_COLOR_WHITE + SET_TEXT_UNDERLINE;
        output = output + SET_TEXT_BOLD + "COMMANDS" + RESET_TEXT_UNDERLINE + RESET_TEXT_BOLD_FAINT;
        output = output + SET_TEXT_COLOR_BLUE + "\n\tcreate <NAME>" + SET_TEXT_COLOR_MAGENTA + " - creates a game";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tlist" + SET_TEXT_COLOR_MAGENTA + " - lists games";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tjoin <ID> [WHITE/BLACK]" + SET_TEXT_COLOR_MAGENTA + " - join game";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tobserve <ID>" + SET_TEXT_COLOR_MAGENTA + " - observe game";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tlogout" + SET_TEXT_COLOR_MAGENTA + " - logout user";
        output = output + SET_TEXT_COLOR_BLUE + "\n\thelp" + SET_TEXT_COLOR_MAGENTA + " - output possible commands" + RESET_TEXT_COLOR;
        return output;
    }

}
