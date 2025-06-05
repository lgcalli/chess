package ui;

import chess.ChessGame;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class Gameplay {
    private final Scanner scanner;
    private final ServerFacade server;

    public Gameplay (Scanner scanner, ServerFacade server){
        this.scanner = scanner;
        this.server = server;

    }

    public void run() {
        /*
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
        */
    }

    String drawBoard (ChessGame.TeamColor color){


        return "";
    }
}
