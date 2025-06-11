package server_web_socket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import dataaccess.*;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final WebSocketConnectionManager connections = new WebSocketConnectionManager();


    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
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

                connections.add(visitorName, session, gameID);
                LoadGameMessage loadGameMessage = new LoadGameMessage(new ChessGame());
                Gson gsonMessage = new Gson();
                String loadGameJson = gsonMessage.toJson(loadGameMessage);
                session.getRemote().sendString(loadGameJson);
                NotificationMessage notification = new NotificationMessage(visitorName + " has joined the game.");
                connections.broadcastToInGame(visitorName, notification, gameID);
            }
        }

    }
}