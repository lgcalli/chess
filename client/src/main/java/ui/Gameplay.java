package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client_web_socket.*;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

import static ui.EscapeSequences.*;

public class Gameplay implements NotificationHandler {
    private final Scanner scanner;
    private final ServerFacade server;
    private final Integer gameID;
    private final String color;
    private final String serverUrl;
    private final String authToken;
    private ChessGame game;
    private WebSocketFacade ws;

    public Gameplay (Scanner scanner, ServerFacade server, String serverUrl, String authToken, int gameID, String color) {
        this.scanner = scanner;
        this.server = server;
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
        this.game = new ChessGame();
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.print(help());
        System.out.print(this.drawBoard(color, null));
        var result = "";
        try {
            ws = new WebSocketFacade(serverUrl, this);
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(SET_TEXT_COLOR_RED + msg);
        }
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        try {
            ws.sendCommand(command);
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(SET_TEXT_COLOR_RED + msg);
        }

        while (!result.equals("leave")){
            printPrompt();
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
                case "redraw chess board" -> drawBoard(color, null);
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
        if (params.length != 1 || params[0].isEmpty()){
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tExpected: highlight legal moves <position>\t Position example: 'a1'");
        }
        int positionA = getNumber(String.valueOf(params[0].charAt(0)));
        if (positionA == 1000) {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tInvalid position");
        }
        int positionB = Integer.parseInt(String.valueOf(params[0].charAt(1)));
        if (positionB > 8 || positionB < 1){
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tInvalid position");
        }


        ChessPosition position = new ChessPosition(positionA, positionB);
        if (game.getBoard().getPiece(position) == null){
            return drawBoard(color, null);
        }

        Collection<ChessMove> validMoves = game.validMoves(position);
        Collection<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move:validMoves){
            endPositions.add(move.getEndPosition());
        }
        return drawBoard(color, endPositions);
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
        output = output + SET_TEXT_COLOR_BLUE + "\n\tmakeMove <position 1> <position 2>" + SET_TEXT_COLOR_MAGENTA  + " - to make a move";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tleave" + SET_TEXT_COLOR_MAGENTA + " - leave game";
        output = output + SET_TEXT_COLOR_BLUE + "\n\thelp" + SET_TEXT_COLOR_MAGENTA + " - output this list again" + RESET_TEXT_COLOR;
        return output;
    }

    String drawBoard (String color, Collection<ChessPosition> endPositions){
        String chessBoard = "\n";
        if (color.equals("black")){
            for (int i = 1; i <= 8; i++){
                for (int j = 1; j <= 8; j++){
                    chessBoard = getString(chessBoard, i, j, endPositions);
                }
                chessBoard = chessBoard +  RESET_BG_COLOR + "\n";
            }
        } else if (color.equals("white") || color.equals("observe")){
            for (int i = 8; i >= 1; i--){
                for (int j = 8; j >= 1; j--){
                    chessBoard = getString(chessBoard, i, j, endPositions);
                }
                chessBoard = chessBoard + RESET_BG_COLOR + "\n";
            }
        }
        return chessBoard;
    }

    private String getString(String chessBoard, int i, int j, Collection<ChessPosition> endPositions) {
        if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1){
            chessBoard = chessBoard + SET_BG_COLOR_WHITE;
        } else {
            chessBoard = chessBoard + SET_BG_COLOR_BLACK;
        }
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, j));
        if (endPositions != null && endPositions.contains(new ChessPosition(i, j))){
            chessBoard = chessBoard + SET_BG_COLOR_YELLOW;
        }
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

    private int getNumber(String a){
        return switch (a) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> 1000;
        };
    }


    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_RED + notification);
        printPrompt();
    }
}
