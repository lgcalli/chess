package server_web_socket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
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

        switch (command.getCommandType()) {
            case CONNECT -> {
                String visitorName = command.getAuthToken();
                int gameID = command.getGameID();
                try {
                    if (authDAO.getUser(visitorName) == null) {
                        ErrorMessage newErrorMessage = new ErrorMessage("Error: user not found");
                        session.getRemote().sendString(gson.toJson(newErrorMessage));
                    } else if (gameDAO.getGame(gameID) == null) {
                        ErrorMessage newErrorMessage = new ErrorMessage("Error: game not found");
                        session.getRemote().sendString(gson.toJson(newErrorMessage));
                    } else {
                        ChessGame game = gameDAO.getGame(gameID);
                        connections.add(visitorName, session, gameID);
                        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
                        Gson gsonMessage = new Gson();
                        String loadGameJson = gsonMessage.toJson(loadGameMessage);
                        session.getRemote().sendString(loadGameJson);
                        NotificationMessage notification = new NotificationMessage(visitorName + " has joined the game.");
                        connections.broadcastToInGame(visitorName, notification, gameID);
                    }
                } catch (DataAccessException e){
                    ErrorMessage error = new ErrorMessage("Error: Server data access failure");
                    session.getRemote().sendString(gson.toJson(error));
                }
            }
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                String visitorName = moveCommand.getAuthToken();
                int gameID = moveCommand.getGameID();
                ChessMove move = moveCommand.getMove();
                ChessGame game = null;
                try {
                    game = gameDAO.getGame(gameID);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
                try {
                    game.makeMove(move);
                } catch (InvalidMoveException e) {
                    throw new RuntimeException(e);
                }
                try {
                    gameDAO.updateGameBoard(gameID, game);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
                LoadGameMessage loadGameMessage = new LoadGameMessage(game);
                Gson gsonMessage = new Gson();
                connections.broadcastToInGameNoExclusion(loadGameMessage, gameID); // pass `null` to include everyone
                NotificationMessage notification = new NotificationMessage(visitorName + " made the move:" + getUserInterfaceMove(move));
                connections.broadcastToInGame(visitorName, notification, gameID);
            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }
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


}