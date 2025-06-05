package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PreLogin {
    private final Scanner scanner;
    private final ServerFacade server;

    public PreLogin (Scanner scanner, ServerFacade server) {
        this.scanner = scanner;
        this.server = server;
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(this.help());


        var result = "";
        while (!result.equals("quit")) {
            System.out.print(help());
            String line = scanner.nextLine();

            try {
                //result = client.eval(line);
                // System.out.print(BLUE + result);
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
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register (String... params) throws ResponseException {
        return "";
    }

    public String login (String... params) throws ResponseException {
        PostLogin login = new PostLogin(this.scanner, this.server);
        //login.run();
        return "";
    }

    private String help () {
        String output = SET_TEXT_COLOR_BLUE + "\nregister <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_MAGENTA  + " - to create a new account";
        output = output + SET_TEXT_COLOR_BLUE + "\nlogin <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_MAGENTA  + " - to login to an existing account";
        output = output + SET_TEXT_COLOR_BLUE + "\nquit" + SET_TEXT_COLOR_MAGENTA + " - exit application";
        output = output + SET_TEXT_COLOR_BLUE + "\nhelp" + SET_TEXT_COLOR_MAGENTA + " - output possible commands";
        return output;
    }
}
