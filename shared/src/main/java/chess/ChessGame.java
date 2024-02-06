package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

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
        Collection <ChessMove> ifNullMoves = new HashSet <ChessMove>();
        ChessPiece x = getBoard().getPiece(startPosition);
        if (x == null) return ifNullMoves;
        Collection <ChessMove> moves = x.pieceMoves(getBoard(), startPosition);
        if (x.getPieceType() == ChessPiece.PieceType.KING) {
            if (x.getTeamColor() == TeamColor.WHITE){
                Collection <ChessPosition> blackEndPositions = calculateTeamEndPositions(TeamColor.BLACK);
                blackEndPositions.forEach(ChessPosition -> {
                    ChessMove chessMove = new ChessMove(startPosition, ChessPosition, null);
                    if (moves.contains(chessMove)){
                        moves.remove(chessMove);
                    }
                });
            } else if (x.getTeamColor() == TeamColor.BLACK){
                Collection <ChessPosition> whiteEndPositions = calculateTeamEndPositions(TeamColor.WHITE);
                whiteEndPositions.forEach(ChessPosition -> {
                    ChessMove chessMove = new ChessMove(startPosition, ChessPosition, null);
                    if (moves.contains(chessMove)){
                        moves.remove(chessMove);
                    }
                });
            }

        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection <ChessMove> moves = validMoves(move.getStartPosition());
        System.out.println(moves.toString());
        ChessBoard boardUpdate = getBoard();
        ChessPiece x = boardUpdate.getPiece(move.getStartPosition());
        if (x.getTeamColor() != getTeamTurn()) {
            System.out.println("TeamTurn = " + getTeamTurn().toString() + "\nPieceTeam = " + x.getTeamColor());
            throw new InvalidMoveException("Invalid Move: Not your piece");
        } else if (moves.contains(move)){
            ChessPiece y = boardUpdate.getPiece(move.getEndPosition());
            boardUpdate.deletePiece(move.getStartPosition());
            if (y != null){
                boardUpdate.deletePiece(move.getEndPosition());
            }
            if (move.getPromotionPiece() != null){
                boardUpdate.addPiece(move.getEndPosition(), new ChessPiece(x.getTeamColor(), move.getPromotionPiece()));
            } else {
                boardUpdate.addPiece(move.getEndPosition(), x);
            }
            setBoard(boardUpdate);
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
        System.out.println(getBoard().toString());
        if (teamColor == TeamColor.WHITE){
            Collection <ChessPosition> blackEndPositions = calculateTeamEndPositions(TeamColor.BLACK);
            ChessPosition king = getBoard().getAllPieceWhite().get(ChessPiece.PieceType.KING);
            System.out.println("WHITE KING POSITION: " + king.toString());
            System.out.println("BLACK END POSITIONS: \n" + blackEndPositions.toString());
            if (blackEndPositions.contains(getBoard().getAllPieceWhite().get(ChessPiece.PieceType.KING))){

                return true;
            } else {
                return false;
            }
        } else if (teamColor == TeamColor.BLACK){
            Collection <ChessPosition> whiteEndPositions = calculateTeamEndPositions(TeamColor.WHITE);
            ChessPosition king = getBoard().getAllPieceBlack().get(ChessPiece.PieceType.KING);
            System.out.println("BLACK KING POSITION: " + king.toString());
            System.out.println("WHITE END POSITIONS: \n" + whiteEndPositions.toString());
            if (whiteEndPositions.contains(getBoard().getAllPieceBlack().get(ChessPiece.PieceType.KING))){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;
        AtomicBoolean inCheckMate = new AtomicBoolean(true);
        if (teamColor == TeamColor.WHITE){
            ChessPosition x = getBoard().getAllPieceWhite().get(ChessPiece.PieceType.KING);
            Collection<ChessMove> kingMoves = validMoves (x);
            Collection <ChessPosition> blackEndPositions = calculateTeamEndPositions(TeamColor.BLACK);
            if (blackEndPositions.containsAll(kingMoves)) {
                inCheckMate.set(true);
            }
        } else if (teamColor == TeamColor.BLACK){
            ChessPosition x = getBoard().getAllPieceBlack().get(ChessPiece.PieceType.KING);
            Collection<ChessMove> kingMoves = validMoves (x);
            Collection <ChessPosition> whiteEndPositions = calculateTeamEndPositions(TeamColor.WHITE);
            if (whiteEndPositions.containsAll(kingMoves)) {
                inCheckMate.set(true);
            }
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
        System.out.println(teamColor.toString());
        AtomicBoolean x = new AtomicBoolean(true);
        if (teamColor == TeamColor.WHITE){
            Collection <ChessPosition> whiteStartPositions = calculateTeamStartPositions(TeamColor.WHITE);
            whiteStartPositions.forEach(position -> {
                if (!validMoves(position).isEmpty()){
                    x.set(false);
                }
            });
        } else if (teamColor == TeamColor.BLACK){
            Collection <ChessPosition> blackStartPositions = calculateTeamStartPositions(TeamColor.BLACK);
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

    public Collection <ChessPosition> calculateTeamStartPositions (ChessGame.TeamColor x){
        Collection<ChessPosition> positions = new HashSet<>();
        HashMap <ChessPosition, Collection <ChessMove>> y = calculateTeamMovesMap (x);
        y.forEach((ChessPosition, ChessMoveDataSet) -> {
            ChessMoveDataSet.forEach ((ChessMove) -> {
                positions.add(ChessMove.getStartPosition());
            });
        });
        return positions;
    }

    public Collection<ChessPosition> calculateTeamEndPositions (ChessGame.TeamColor x) {
        Collection<ChessPosition> positions = new HashSet<>();
        HashMap <ChessPosition, Collection <ChessMove>> y = calculateTeamMovesMap (x);
        y.forEach((ChessPosition, ChessMoveDataSet) -> {
            ChessMoveDataSet.forEach ((ChessMove) -> {
                positions.add(ChessMove.getEndPosition());
            });
        });
        return positions;
    }

    public HashMap <ChessPosition, Collection <ChessMove>> calculateTeamMovesMap (ChessGame.TeamColor x) {
        HashMap <ChessPosition, Collection <ChessMove>> moves = new HashMap<>();
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                ChessPosition position = new ChessPosition (i, j);
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == x){
                    Collection <ChessMove> validChessMoves = board.getPiece(position).pieceMoves(board, position);
                    moves.put(position, validChessMoves);
                }
            }
        }
        return moves;
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
