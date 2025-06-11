package server_web_socket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
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


            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }

    }
}