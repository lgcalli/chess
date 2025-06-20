package clientsocket;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson gson = new Gson();
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
                    ServerMessage notification = null;
                    switch (serverMessageType) {
                        case LOAD_GAME -> notification  = gson.fromJson(message, LoadGameMessage.class);
                        case NOTIFICATION -> notification  = gson.fromJson(message, NotificationMessage.class);
                        case ERROR -> notification  = gson.fromJson(message, ErrorMessage.class);
                    };
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendCommand(UserGameCommand command) throws Exception {
        String message = new Gson().toJson(command);
        session.getBasicRemote().sendText(message);

    }

    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

