package ui;

import chess.ChessGame;
import server.ServerFacade;

import java.util.Scanner;

public class Gameplay {
    private final Scanner scanner;
    private final ServerFacade server;

    public Gameplay (Scanner scanner, ServerFacade server){
        this.scanner = scanner;
        this.server = server;

    }

    public void run() {

    }

    String drawBoard (ChessGame.TeamColor color){


        return "";
    }
}
