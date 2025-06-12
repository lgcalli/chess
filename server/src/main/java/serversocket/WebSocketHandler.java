package serversocket;

import chess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import dataaccess.*;

import java.io.IOException;
import java.util.Collection;


@WebSocket
public class WebSocketHandler {

    private final WebSocketConnectionManager connections = new WebSocketConnectionManager();
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Received: " + message);
        Gson gson = new Gson();

        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        String visitorName = command.getAuthToken();
        int gameID = command.getGameID();
        String username = null;
        ChessGame game = null;
        try {
            username = authDAO.getUser(visitorName);
            game = gameDAO.getGame(gameID);
            if (username == null) {
                ErrorMessage newErrorMessage = new ErrorMessage("user not found");
                session.getRemote().sendString(gson.toJson(newErrorMessage));
                return;
            } else if (game == null) {
                ErrorMessage newErrorMessage = new ErrorMessage("game not found");
                session.getRemote().sendString(gson.toJson(newErrorMessage));
                return;
            }
        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage("unable to execute the command");
            session.getRemote().sendString(gson.toJson(error));
            return;
        }

        switch (command.getCommandType()) {
            case CONNECT -> {
                connections.add(visitorName, session, gameID);
                LoadGameMessage loadGameMessage = new LoadGameMessage(game);
                Gson gsonMessage = new Gson();
                String loadGameJson = gsonMessage.toJson(loadGameMessage);
                session.getRemote().sendString(loadGameJson);
                ChessGame.TeamColor color = null;
                try {
                    color = gameDAO.getPlayerColor(gameID, username);
                } catch (DataAccessException e) {
                    ErrorMessage error = new ErrorMessage("could not join game");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                NotificationMessage notification = null;
                if (color == null){
                    notification = new NotificationMessage(username + " is observing the game");
                } else {
                    notification = new NotificationMessage(username + " has joined the game");
                }
                connections.broadcastToInGame(visitorName, notification, gameID);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                ChessMove move = moveCommand.getMove();
                makeMoveParameters parameters = new makeMoveParameters(gameID, session, move, gson, game, username, visitorName);
                makeMove (parameters);
            }
            case LEAVE -> {
                try {
                    ChessGame.TeamColor color = gameDAO.getPlayerColor(gameID, username);
                    if (color == ChessGame.TeamColor.WHITE){
                        gameDAO.leaveGame(gameID, ChessGame.TeamColor.WHITE);
                    } else if (color == ChessGame.TeamColor.BLACK){
                        gameDAO.leaveGame(gameID, ChessGame.TeamColor.BLACK);
                    }
                } catch (DataAccessException e) {
                    ErrorMessage error = new ErrorMessage("could not leave game");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                connections.remove(visitorName);
                NotificationMessage notification = new NotificationMessage(username + " has left the game");
                connections.broadcastToInGame(visitorName, notification, gameID);
            }
            case RESIGN -> {
                ChessGame.TeamColor color = null;
                try {
                    boolean gameOver = gameDAO.getGameOver(gameID);
                    if (gameOver){
                        ErrorMessage error = new ErrorMessage("already resigned");
                        session.getRemote().sendString(gson.toJson(error));
                        return;
                    }
                    color = gameDAO.getPlayerColor(gameID, username);
                    if (color == null){
                        ErrorMessage error = new ErrorMessage("observers cannot resign");
                        session.getRemote().sendString(gson.toJson(error));
                        return;
                    }
                    gameDAO.setGameOver(gameID, true);
                } catch (DataAccessException e) {
                    ErrorMessage error = new ErrorMessage("could not resign game");
                    session.getRemote().sendString(gson.toJson(error));
                    return;
                }
                NotificationMessage notification = new NotificationMessage(username + " has resigned");
                connections.broadcastToInGameNoExclusion(notification, gameID);
            }
        }
    }

    private void makeMove (makeMoveParameters parameters) throws IOException{
        try {
            boolean gameOver = gameDAO.getGameOver(parameters.gameID);
            if (gameOver){
                ErrorMessage newErrorMessage = new ErrorMessage("game is over, cannot make any more moves");
                parameters.session.getRemote().sendString(parameters.gson.toJson(newErrorMessage));
                return;
            }
        } catch (DataAccessException e) {
            ErrorMessage newErrorMessage = new ErrorMessage("unable to move piece");
            parameters.session.getRemote().sendString(parameters.gson.toJson(newErrorMessage));
            return;
        }

        try {
            ChessGame.TeamColor color = gameDAO.getPlayerColor(parameters.gameID, parameters.username);
            if (parameters.game.getBoard().getPiece(parameters.move.getStartPosition()).getTeamColor() != color){
                ErrorMessage newErrorMessage = new ErrorMessage("unable to move piece");
                parameters.session.getRemote().sendString(parameters.gson.toJson(newErrorMessage));
                return;
            }
            Collection<ChessMove> validMoves = parameters.game.validMoves(parameters.move.getStartPosition());
            if (!validMoves.contains(parameters.move)){
                ErrorMessage error = new ErrorMessage("invalid move");
                parameters.session.getRemote().sendString(parameters.gson.toJson(error));
                return;
            }
        } catch (DataAccessException e) {
            ErrorMessage error = new ErrorMessage("could not make move");
            parameters.session.getRemote().sendString(parameters.gson.toJson(error));
            return;
        }
        try {
            parameters.game.makeMove(parameters.move);
            gameDAO.updateGameBoard(parameters.gameID, parameters.game);
        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage("not your turn");
            parameters.session.getRemote().sendString(parameters.gson.toJson(error));
            return;
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage(parameters.game);
        connections.broadcastToInGameNoExclusion(loadGameMessage, parameters.gameID);
        String s = parameters.username + " made the move:" + getUserInterfaceMove(parameters.move);

        NotificationMessage notification = new NotificationMessage(s);
        connections.broadcastToInGame(parameters.visitorName, notification, parameters.gameID);
    }

    private String getUserInterfaceMove(ChessMove move){
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        int row1 = start.getRow();
        int col1 = start.getColumn();
        int row2 = end.getRow();
        int col2 = end.getColumn();

        String col1String = getLetter(col1);
        String col2String = getLetter(col2);

        return col1String + row1 + " -> " + col2String + row2;
    }

    private String getLetter(int a){
        return switch (a) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> null;
        };
    }

    public class makeMoveParameters {
        public int gameID;
        public Session session;
        public ChessMove move;
        public Gson gson;
        public ChessGame game;
        public String username;
        public String visitorName;

        public makeMoveParameters(int gameID, Session session, ChessMove move, Gson gson,
                           ChessGame game, String username, String visitorName) {
            this.gameID = gameID;
            this.session = session;
            this.move = move;
            this.gson = gson;
            this.game = game;
            this.username = username;
            this.visitorName = visitorName;
        }
    }


}