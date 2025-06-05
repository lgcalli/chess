package ui;

import static ui.EscapeSequences.*;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PostLogin {
    private final Scanner scanner;
    private final ServerFacade server;

    public PostLogin (Scanner scanner, ServerFacade server){
        this.scanner = scanner;
        this.server = server;

    }

    public void run() {
        System.out.print(this.help());

        var result = "";
        while (!result.equals("quit")) {
            System.out.print(help());
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame (String... params) throws ResponseException {
        return "";
    }

    public String listGames (String... params) throws ResponseException {
        return "";
    }

    public String joinGame (String... params) throws ResponseException {
        return "";
    }

    public String observeGame (String... params) throws ResponseException {
        return "";
    }

    public String logout (String... params) throws ResponseException {
        return "";
    }

    private String help () {
        String output = SET_TEXT_COLOR_BLUE + "\ncreate <NAME>" + SET_TEXT_COLOR_MAGENTA + " - creates a game";
        output = output + SET_TEXT_COLOR_BLUE + "\nlist" + SET_TEXT_COLOR_MAGENTA + " - lists games";
        output = output + SET_TEXT_COLOR_BLUE + "\njoin <ID> [WHITE/BLACK]" + SET_TEXT_COLOR_MAGENTA + " - join game";
        output = output + SET_TEXT_COLOR_BLUE + "\nobserve <ID>" + SET_TEXT_COLOR_MAGENTA + " - observe game";
        output = output + SET_TEXT_COLOR_BLUE + "\nlogout" + SET_TEXT_COLOR_MAGENTA + " - logout user";
        output = output + SET_TEXT_COLOR_BLUE + "\nhelp" + SET_TEXT_COLOR_MAGENTA + " - output possible commands";
        return output;
    }




}
