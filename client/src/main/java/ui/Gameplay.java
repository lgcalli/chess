package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Gameplay {
    private final Scanner scanner;
    private final ServerFacade server;
    private final Integer gameID;
    private final String color;
    private ChessGame game;

    public Gameplay (Scanner scanner, ServerFacade server, int gameID, String color) {
        this.scanner = scanner;
        this.server = server;
        this.gameID = gameID;
        this.color = color;
        this.game = new ChessGame();
    }

    public void run() {
        System.out.print(help());
        System.out.print(this.drawBoard(color));
        var result = "";
        while (!result.equals("leave")){
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
                case "redraw chess board" -> drawBoard(color);
                case "make move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight legal moves" -> highlightLegalMoves(params);
                case "leave" -> "leave";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    String highlightLegalMoves (String... params) throws ResponseException {
        String output = "";

        return output;
    }

    String makeMove (String... params) throws ResponseException {
        String output = "";

        return output;
    }

    String resign () throws ResponseException {
        String output = "";
        System.out.print(RESET_TEXT_COLOR + "Are you sure you want to resign? (Y/N)");
        String line = scanner.nextLine();


        return output;
    }

    String help () {
        String output = "\n\t" + SET_TEXT_COLOR_WHITE + SET_TEXT_UNDERLINE + SET_TEXT_BOLD;
        output = output + "COMMANDS" + RESET_TEXT_UNDERLINE + RESET_TEXT_BOLD_FAINT;
        output = output + SET_TEXT_COLOR_BLUE + "\n\thighlight legal moves <position>";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tmakeMove <position 1> <position 2>" + SET_TEXT_COLOR_MAGENTA  + " - to login to an existing account";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tleave" + SET_TEXT_COLOR_MAGENTA + " - leave game";
        output = output + SET_TEXT_COLOR_BLUE + "\n\thelp" + SET_TEXT_COLOR_MAGENTA + " - output this list again" + RESET_TEXT_COLOR;
        return output;
    }

    String drawBoard (String color){
        String chessBoard = "\n";
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
        return SET_TEXT_COLOR_BLUE + getPiece(piece, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN);
    }

    String getPieceTypeBlack (ChessPiece piece){
        return SET_TEXT_COLOR_RED +  getPiece(piece, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN);
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
