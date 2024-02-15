package chess;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() { }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    public TeamColor getOppositeTeam(TeamColor color) {
        if (color == TeamColor.WHITE){
            return TeamColor.BLACK;
        } else if (color == TeamColor.BLACK){
            return TeamColor.WHITE;
        } else {
            return null;
        }
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        System.out.println("Checking Valid Moves for Board:");
        System.out.println(board.toString());
        if (board.getPiece(startPosition) == null) return null;
        TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        Collection <ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection <ChessMove> returnMoves = moves;
        moves.removeIf(chessMove -> {
            System.out.println("Is  --- " + chessMove.toString() + " ---  a valid Move?");
            return board.movePutsBoardInCheck(chessMove, teamColor);
        });

        //System.out.println(board.toString());
        return moves;

        //Check for Castling
            /*
            This is a special move where the King and a Rook move simultaneously. The castling move can only be taken when 4 conditions are met:
            1. Neither the King nor Rook have moved since the game started
            2. There are no pieces between the King and the Rook
            3. The King is not in Check
            4. Both your Rook and King will be safe after making the move (cannot be captured by any enemy pieces).
            To Castle, the King moves 2 spaces towards the Rook, and the Rook "jumps" the king moving to the position next to and on the other side of the King. This is represented in a ChessMove as the king moving 2 spaces to the side.
            */


        //Check for En Passant
            /*
            This is a special move taken by a Pawn in response to your opponent double moving a Pawn.
            If your opponent double moves a pawn so it ends next to yours (skipping the position where your pawn could have captured their pawn),
                then on your immediately following turn your pawn may capture their pawn as if their pawn had only moved 1 square.
            This is as if your pawn is capturing their pawn mid motion, or In Passing.
            */



    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection <ChessMove> moves = validMoves(move.getStartPosition());
        //System.out.println(moves.toString());
        ChessPiece x = board.getPiece(move.getStartPosition());
        if (x != null && x.getTeamColor() != getTeamTurn()) {
            //System.out.println("TeamTurn = " + getTeamTurn().toString() + "\nPieceTeam = " + x.getTeamColor());
            throw new InvalidMoveException("Invalid Move: Not your piece");
        } else if (moves.contains(move)){
            ChessBoard updateBoard = board;
            updateBoard.moveAction(move);
            setBoard(updateBoard);
        } else {
           throw new InvalidMoveException("Invalid Move: Piece not able to move to chosen position");
        }
        if (getTeamTurn() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return board.boardInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        TeamColor opposingColor = null;
        if (teamColor == TeamColor.WHITE) {
            opposingColor = TeamColor.BLACK;
        }
        if (teamColor == TeamColor.BLACK) {
            opposingColor = TeamColor.WHITE;
        }
        if (!isInCheck(teamColor)) return false;
        AtomicBoolean inCheckMate = new AtomicBoolean(true);
        ChessPosition x = getBoard().getAllPieceColor(teamColor).get(ChessPiece.PieceType.KING);
        Collection<ChessMove> kingMoves = validMoves(x);
        Collection <ChessPosition> opposingEndPositions = board.calculateTeamEndPositions(opposingColor);
        if (kingMoves.containsAll(opposingEndPositions)) {
            inCheckMate.set(true);
        }
        return inCheckMate.get();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //System.out.println(teamColor.toString());
        AtomicBoolean x = new AtomicBoolean(true);
        if (teamColor == TeamColor.WHITE){
            Collection <ChessPosition> whiteStartPositions = board.calculateTeamStartPositions(TeamColor.WHITE);
            whiteStartPositions.forEach(position -> {
                if (!validMoves(position).isEmpty()){
                    x.set(false);
                }
            });
        } else if (teamColor == TeamColor.BLACK){
            Collection <ChessPosition> blackStartPositions = board.calculateTeamStartPositions(TeamColor.BLACK);
            blackStartPositions.forEach(position -> {
                if (!validMoves(position).isEmpty()){
                    x.set(false);
                }
            });
        }
        return x.get();
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessGame chessGame)) return false;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turn=" + turn +
                ", board=" + board +
                '}';
    }
}
