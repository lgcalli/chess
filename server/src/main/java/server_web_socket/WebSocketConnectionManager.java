package server_web_socket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketConnectionManager {
    public final ConcurrentHashMap<String, WebSocketConnection> connections = new ConcurrentHashMap<>();

    public void add(String visitorName, Session session, int gameID) {
        var connection = new WebSocketConnection(visitorName, session, gameID);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcastToInGame (String excludeVisitorName, ServerMessage notification, int gameID) throws IOException {
        var removeList = new ArrayList<WebSocketConnection>();
        Gson gson = new Gson();
        for (var c : connections.values()) {
            if (c.session.isOpen() && c.gameID == gameID) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    c.send(gson.toJson(notification));
                }
            } else {
                if (!c.session.isOpen()) {
                    removeList.add(c);
                }
            }
        }
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void broadcastToInGameNoExclusion (ServerMessage notification, int gameID) throws IOException {
        var removeList = new ArrayList<WebSocketConnection>();
        Gson gson = new Gson();
        for (var c : connections.values()) {
            if (c.session.isOpen() && c.gameID == gameID) {
                c.send(gson.toJson(notification));
            } else {
                if (!c.session.isOpen()) {
                    removeList.add(c);
                }
            }
        }
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }

    public void broadcast(String excludeVisitorName, NotificationMessage notification) throws IOException {
        var removeList = new ArrayList<WebSocketConnection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.visitorName);
        }
    }
}
