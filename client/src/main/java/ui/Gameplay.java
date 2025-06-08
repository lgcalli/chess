package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import server.ServerFacade;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Gameplay {
    private final Scanner scanner;
    private final ServerFacade server;
    private final Integer gameID;
    private final String color;
    private ChessGame game;

    public Gameplay (Scanner scanner, ServerFacade server, int gameID, String color){
        this.scanner = scanner;
        this.server = server;
        this.gameID = gameID;
        this.color = color;
        this.game = new ChessGame();
    }

    public void run() {

        System.out.print(this.drawBoard(color));
        var result = "";
        while (!result.equals("quit")){
            String line = scanner.nextLine();
            System.out.print(this.drawBoard(color));
        }


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
        String chessBoard = "";
        if (color.equals("BLACK") || color.equals("black") || color.equals("Black") || color == null){
            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1){
                        chessBoard = chessBoard + SET_BG_COLOR_WHITE;
                    } else {
                        chessBoard = chessBoard + SET_BG_COLOR_BLACK;
                    }
                    ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, j));
                    if (piece == null){
                        chessBoard = chessBoard + "   ";
                    } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        chessBoard = chessBoard + this.getPieceTypeWhite(piece);
                    } else {
                        chessBoard = chessBoard + this.getPieceTypeBlack(piece);
                    }
                }
                chessBoard = chessBoard +  RESET_BG_COLOR + "\n";
            }
        } else if (color.equals("WHITE") || color.equals("white") || color.equals("White")){
            for (int i = 8; i >= 1; i--){
                for (int j = 8; j >= 1; j--){
                    if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1){
                        chessBoard = chessBoard + SET_BG_COLOR_WHITE;
                    } else {
                        chessBoard = chessBoard + SET_BG_COLOR_BLACK;
                    }
                    ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, j));
                    if (piece == null){
                        chessBoard = chessBoard + "   ";
                    } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        chessBoard = chessBoard + this.getPieceTypeWhite(piece);
                    } else {
                        chessBoard = chessBoard + this.getPieceTypeBlack(piece);
                    }
                }
                chessBoard = chessBoard + RESET_BG_COLOR + "\n";
            }
        }
        return chessBoard;
    }

    String getPieceTypeWhite (ChessPiece piece){
        return switch (piece.getPieceType()) {
            case ChessPiece.PieceType.KING -> WHITE_KING;
            case ChessPiece.PieceType.QUEEN -> WHITE_QUEEN;
            case ChessPiece.PieceType.BISHOP -> WHITE_BISHOP;
            case ChessPiece.PieceType.KNIGHT -> WHITE_KNIGHT;
            case ChessPiece.PieceType.ROOK -> WHITE_ROOK;
            case ChessPiece.PieceType.PAWN -> WHITE_PAWN;
            default -> null;
        };
    }

    String getPieceTypeBlack (ChessPiece piece){
        return switch (piece.getPieceType()) {
            case ChessPiece.PieceType.KING -> BLACK_KING;
            case ChessPiece.PieceType.QUEEN -> BLACK_QUEEN;
            case ChessPiece.PieceType.BISHOP -> BLACK_BISHOP;
            case ChessPiece.PieceType.KNIGHT -> BLACK_KNIGHT;
            case ChessPiece.PieceType.ROOK -> BLACK_ROOK;
            case ChessPiece.PieceType.PAWN -> BLACK_PAWN;
            default -> null;
        };
    }
}
