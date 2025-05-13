package chess;

import javax.xml.transform.stax.StAXResult;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
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
        if (board.getPiece(startPosition) == null) return null;
        TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        Collection <ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        moves.removeIf(chessMove -> {
            return board.movePutsBoardInCheck(chessMove, teamColor);
        });
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        move.getStartPosition();
        move.getEndPosition();

        Collection <ChessMove> moves = validMoves(move.getStartPosition());
        ChessPiece x = board.getPiece(move.getStartPosition());
        if (x != null && x.getTeamColor() != getTeamTurn()) {
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
        board.updateColorMaps();
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


        ChessPosition kingPos = getBoard().getAllPieceColorIndividualByType(ChessPiece.PieceType.KING, teamColor);
        Collection<ChessMove> kingMoves = validMoves(kingPos);
        Collection<ChessPosition> kingEndPositions = new HashSet<>();
        if (!kingMoves.isEmpty()) {
            kingMoves.forEach(chessMove -> {
                kingEndPositions.add(chessMove.getEndPosition());
            });
        }
        Collection <ChessPosition> opposingEndPositions = board.calculateTeamEndPositions(opposingColor);
        if (kingEndPositions == null || kingEndPositions.isEmpty()) {
            if (isInCheck(teamColor)) inCheckMate.set(true);
            else inCheckMate.set(false);
        }  else if (kingEndPositions.containsAll(opposingEndPositions)) {
            inCheckMate.set(true);
        }
        if (inCheckMate.get()) return inCheckMate.get();
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
        this.board.updateColorMaps();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
