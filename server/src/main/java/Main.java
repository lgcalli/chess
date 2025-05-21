import chess.*;
import dataAccess.*;
import org.eclipse.jetty.server.Server;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece);

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        var UserService = new UserService(userDAO, authDAO);
        var AuthService = new AuthService();
        var GameService = new GameService();

        var server = new server.Server(UserService, AuthService, GameService);
        server.run(8080);
    }


}