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
        System.out.println("Welcome to Chess. Sign in to start.");
        System.out.print(help());
        var result = "";
        while (!result.equals("quit")) {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg);
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
        if (params.length == 3){
            try {
                server.register(params[0], params[1], params[2]);
            } catch (ResponseException e) {
                return SET_TEXT_COLOR_RED + "\tRegister failed: " + e.getMessage();
            }
            PostLogin login = new PostLogin(this.scanner, this.server);
            login.run();
            return help();
        } else {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tExpected: register <USERNAME> <PASSWORD> <EMAIL>");
        }
    }

    public String login (String... params) throws ResponseException {
        if (params.length == 2){
            try {
                server.login(params[0], params[1]);
            } catch (ResponseException e) {
                return "Login failed: " + e.getMessage();
            }
            PostLogin login = new PostLogin(this.scanner, this.server);
            login.run();
            return help();
        } else {
            throw new ResponseException(400, "Expected: " + SET_TEXT_COLOR_RED + "login <USERNAME> <PASSWORD>");
        }
    }

    private String help () {
        String output = "\t" + SET_TEXT_COLOR_WHITE + SET_TEXT_UNDERLINE + SET_TEXT_BOLD + "COMMANDS" + RESET_TEXT_UNDERLINE + RESET_TEXT_BOLD_FAINT;
        output = output + SET_TEXT_COLOR_BLUE + "\n\tregister <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_MAGENTA  + " - to create a new account";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tlogin <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_MAGENTA  + " - to login to an existing account";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tquit" + SET_TEXT_COLOR_MAGENTA + " - exit application";
        output = output + SET_TEXT_COLOR_BLUE + "\n\thelp" + SET_TEXT_COLOR_MAGENTA + " - output this list again";
        return output;
    }
}
