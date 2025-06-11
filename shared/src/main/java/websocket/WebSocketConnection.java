package websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;

public class WebSocketConnection {
    public String visitorName;
    public Session session;

    public WebSocketConnection(String visitorName, Session session) {
        this.visitorName = visitorName;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}