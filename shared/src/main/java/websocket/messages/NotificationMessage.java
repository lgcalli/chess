package websocket.messages;

import websocket.commands.UserGameCommand;

public class NotificationMessage extends ServerMessage{
    private String message;

    public NotificationMessage (String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

}
