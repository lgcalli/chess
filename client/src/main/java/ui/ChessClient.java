package ui;
import server.ServerFacade;

import static ui.EscapeSequences.*;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;

    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        PreLogin preLogin = new PreLogin(scanner, server);
        preLogin.run();
    }
}
