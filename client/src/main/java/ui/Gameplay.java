package ui;

import chess.ChessGame;
import server.ServerFacade;

import java.util.Objects;
import java.util.Scanner;

public class Gameplay {
    private final Scanner scanner;
    private final ServerFacade server;
    private final Integer gameID;
    private final String color;

    public Gameplay (Scanner scanner, ServerFacade server, int gameID, String color){
        this.scanner = scanner;
        this.server = server;
        this.gameID = gameID;
        this.color = color;
    }

    public void run() {

        System.out.print(this.drawBoard(color));
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

    String drawBoard (String color){
        String chessBoard;
        if (color.equals("WHITE") || color.equals("white") || color.equals("White") || color == null){

        } else if (color.equals("BLACK") || color.equals("black") || color.equals("Black")){

        }
        return "";
    }
}
