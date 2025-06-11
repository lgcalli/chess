package client_web_socket;
import websocket.messages.*;
import websocket.commands.*;

public interface NotificationHandler {
   void notify(NotificationMessage notification);
}

