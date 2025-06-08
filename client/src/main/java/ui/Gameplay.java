package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

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

    }

    String drawBoard (String color){
        String chessBoard = "";
        if (color.equals("black")){
            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    chessBoard = getString(chessBoard, i, j);
                }
                chessBoard = chessBoard +  RESET_BG_COLOR + "\n";
            }
        } else if (color.equals("white") || color.equals("observe")){
            for (int i = 8; i >= 1; i--){
                for (int j = 8; j >= 1; j--){
                    chessBoard = getString(chessBoard, i, j);
                }
                chessBoard = chessBoard + RESET_BG_COLOR + "\n";
            }
        }
        return chessBoard;
    }

    private String getString(String chessBoard, int i, int j) {
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
        return chessBoard;
    }

    String getPieceTypeWhite (ChessPiece piece){
        return getPiece(piece, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN);
    }

    String getPieceTypeBlack (ChessPiece piece){
        return getPiece(piece, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN);
    }

    private String getPiece(ChessPiece piece, String king, String queen, String bishop, String knight, String rook, String pawn) {
        return switch (piece.getPieceType()) {
            case ChessPiece.PieceType.KING -> king;
            case ChessPiece.PieceType.QUEEN -> queen;
            case ChessPiece.PieceType.BISHOP -> bishop;
            case ChessPiece.PieceType.KNIGHT -> knight;
            case ChessPiece.PieceType.ROOK -> rook;
            case ChessPiece.PieceType.PAWN -> pawn;
            default -> null;
        };
    }
}
