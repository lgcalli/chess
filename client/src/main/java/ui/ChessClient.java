package ui;
import server.ServerFacade;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        Scanner scanner = new Scanner(System.in);
        PreLogin preLogin = new PreLogin(scanner, server);
        preLogin.run();
    }

}
