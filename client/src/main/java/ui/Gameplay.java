package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client_web_socket.*;
import exception.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
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
        this.color = color;
        this.gameID = gameID;
        this.game = new ChessGame();
        this.serverUrl = serverUrl;
    }

    public void run() {
        System.out.print(help());
        System.out.print(this.drawBoard(color, null, 1000, 1000));
        var result = "";
        try {
            ws = new WebSocketFacade(serverUrl, this);
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(SET_TEXT_COLOR_RED + msg);
        }
        UserGameCommand command = new ConnectCommand(authToken, gameID);
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
                case "redraw" -> drawBoard(color, null, 1000, 1000);
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlightLegalMoves(params);
                case "leave" -> leave();
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
        int row = getRow(params[0]);
        int col = getColumn(params[0]);

        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = game.getBoard().getPiece(position);

        if (piece == null){
            return drawBoard(color, null, 1000, 1000);
        }

        Collection<ChessMove> validMoves = game.validMoves(position);
        Collection<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move:validMoves){
            endPositions.add(move.getEndPosition());
        }
        return drawBoard(color, endPositions, row, col);
    }

    String makeMove (String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tExpected: move <position 1> <position 2>");
        }
        int row1 = getRow(params[0]);
        int col1 = getColumn(params[0]);
        int row2 = getRow(params[1]);
        int col2 = getColumn(params[1]);
        ChessPosition start = new ChessPosition(row1, col1);
        ChessPosition end = new ChessPosition(row2, col2);
        ChessMove move = null;

        Collection<ChessMove> validMoves = game.validMoves(start);
        if (validMoves.contains(new ChessMove(start, end, ChessPiece.PieceType.QUEEN))){
            ChessPiece.PieceType promotion = null;
            while (promotion == null) {
                System.out.print(RESET_TEXT_COLOR + "What would you like to promote to? (QUEEN/KNIGHT/BISHOP/ROOK)");
                printPrompt();
                String line = scanner.nextLine();
                promotion = getPieceTypePromotion(line);
            }
            move = new ChessMove(start, end, promotion);
        } else {
            move = new ChessMove(start, end, null);
        }
        MakeMoveCommand command =  new MakeMoveCommand(authToken, gameID, move);
        try {
            ws.sendCommand(command);
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(SET_TEXT_COLOR_RED + msg);
        }
        return "Move: " + params[0] + " to " + params[1];
    }

    String resign () throws ResponseException {
        String output = "";
        System.out.print(RESET_TEXT_COLOR + "Are you sure you want to resign? (Y/N)");
        printPrompt();
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("yes") || line.equalsIgnoreCase("Y")){
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            try {
                ws.sendCommand(command);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }
        }
        return output;
    }

    String leave () throws ResponseException {

        return "leave";
    }

    String help () {
        String output = "\n\t" + SET_TEXT_COLOR_WHITE + SET_TEXT_UNDERLINE + SET_TEXT_BOLD;
        output = output + "COMMANDS" + RESET_TEXT_UNDERLINE + RESET_TEXT_BOLD_FAINT;
        output = output + SET_TEXT_COLOR_BLUE + "\n\tredraw" + SET_TEXT_COLOR_MAGENTA  + " - redraws chess board";
        output = output + SET_TEXT_COLOR_BLUE + "\n\thighlight <position>" + SET_TEXT_COLOR_MAGENTA  + " - highlight valid moves for piece at position 1";;
        output = output + SET_TEXT_COLOR_BLUE + "\n\tmove <position 1> <position 2>" + SET_TEXT_COLOR_MAGENTA  + " - make a move from position 1 to position 2";
        output = output + SET_TEXT_COLOR_BLUE + "\n\tleave" + SET_TEXT_COLOR_MAGENTA + " - leave game";
        output = output + SET_TEXT_COLOR_BLUE + "\n\thelp" + SET_TEXT_COLOR_MAGENTA + " - output this list again" + RESET_TEXT_COLOR + "\n";
        return output;
    }

    String drawBoard (String color, Collection<ChessPosition> endPositions, int row, int column){
        String chessBoard=  "\n" + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE;
        chessBoard = chessBoard + "    a  b  c  d  e  f  g  h    " + RESET_TEXT_COLOR + RESET_BG_COLOR + "\n";

        if (color.equals("black")){
            for (int i = 1; i <= 8; i++){
                chessBoard = outputColumn(endPositions, row, column, chessBoard, i);
            }
        } else if (color.equals("white") || color.equals("observe")){
            for (int i = 8; i >= 1; i--){
                chessBoard = outputColumn(endPositions, row, column, chessBoard, i);
            }
            chessBoard = chessBoard + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE;
        }
        chessBoard = chessBoard + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE;
        chessBoard = chessBoard + "    a  b  c  d  e  f  g  h    " + RESET_TEXT_COLOR + RESET_BG_COLOR;
        return chessBoard;
    }

    private String outputColumn(Collection<ChessPosition> endPositions, int row, int column, String chessBoard, int i) {
        chessBoard = chessBoard + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " " + i + " " + RESET_BG_COLOR + RESET_TEXT_COLOR;
        for (int j = 1; j <= 8; j++){
            chessBoard = getString(chessBoard, i, j, endPositions, row, column);
        }
        chessBoard = chessBoard + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " " + i + " " + RESET_BG_COLOR + RESET_TEXT_COLOR;
        chessBoard = chessBoard +  "\n";
        return chessBoard;
    }

    private String getString(String chessBoard, int i, int j, Collection<ChessPosition> endPositions, int row, int column) {
        if (i % 2 == 0 && j % 2 == 0 || i % 2 == 1 && j % 2 == 1){
            chessBoard = chessBoard + SET_BG_COLOR_WHITE;
        } else {
            chessBoard = chessBoard + SET_BG_COLOR_BLACK;
        }
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, j));
        if (endPositions != null && endPositions.contains(new ChessPosition(i, j))){
            chessBoard = chessBoard + SET_BG_COLOR_YELLOW;
        } if (row == i && column == j){
            chessBoard = chessBoard + SET_BG_COLOR_MAGENTA;
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

    private ChessPiece.PieceType getPieceTypePromotion(String piece) {
        return switch (piece) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            default -> null;
        };
    }

    int getRow (String input) throws ResponseException {
        int row = Integer.parseInt(String.valueOf(input.charAt(1)));
        if (row > 8 || row < 1){
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tInvalid position");
        }
        return row;
    }

    int getColumn (String input)  throws ResponseException {
        int column = getNumber(String.valueOf(input.charAt(0)));
        if (column == 1000) {
            throw new ResponseException(400, SET_TEXT_COLOR_RED + "\tInvalid position");
        }
        return column;
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
