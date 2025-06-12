package websocket.messages;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;

public class NotificationMessage extends ServerMessage{
    public String message;

    public NotificationMessage (String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    @Override
    public String toString() {
        return "Notification: " + message;
    }
}
