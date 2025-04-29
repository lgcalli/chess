package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection <ChessMove> moves = new ArrayList<>();
        switch (type) {
            case KING:
            case QUEEN:
            case BISHOP:
                moves = diagonals(board, myPosition, moves);
            case KNIGHT:
            case ROOK:
            case PAWN:

        }
        return moves;
    }

    private Collection<ChessMove> diagonals (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        //define reference variables
        int i = myPosition.getRow() + 1;
        int j = myPosition.getColumn() + 1;
        int k = myPosition.getRow() - 1;
        int l = myPosition.getColumn() - 1;
        //check for moves in the top right direction
        while (i <= 8 && j <= 8){
            ChessPosition newPosition = new ChessPosition(i, j);
            if (board.getPiece(newPosition) == null){
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
            } else if (board.getPiece(newPosition).getTeamColor() != pieceColor) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                break;
            } else if (board.getPiece(newPosition).getTeamColor() == pieceColor) {
                break;
            }
            i++; j++;
        }
        //reset reference variables to one row and column above the starting point
        i = myPosition.getRow() + 1;
        j = myPosition.getColumn() + 1;
        //check for moves in the bottom left direction
        while (k >= 1 && l >= 1){
            ChessPosition newPosition = new ChessPosition(k, l);
            if (board.getPiece(newPosition) == null){
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
            } else if (board.getPiece(newPosition).getTeamColor() != pieceColor) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                break;
            } else if (board.getPiece(newPosition).getTeamColor() == pieceColor) {
                break;
            }
            k--; l--;
        }
        //reset reference variables to one row and column above the starting point
        k = myPosition.getRow() - 1;
        l = myPosition.getColumn() - 1;
        //check for moves in the top left direction
        while (i <= 8 && l >= 1){
            ChessPosition newPosition = new ChessPosition(i, l);
            if (board.getPiece(newPosition) == null){
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
            } else if (board.getPiece(newPosition).getTeamColor() != pieceColor) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                break;
            } else if (board.getPiece(newPosition).getTeamColor() == pieceColor) {
                break;
            }
            i++; l--;
        }
        //check for moves in the bottom right direction
        while (k >= 1 &&  j <= 8){
            ChessPosition newPosition = new ChessPosition(k, j);
            if (board.getPiece(newPosition) == null){
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
            } else if (board.getPiece(newPosition).getTeamColor() != pieceColor) {
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                moves.add(newMove);
                break;
            } else if (board.getPiece(newPosition).getTeamColor() == pieceColor) {
                break;
            }
            k--; j++;
        }
        return moves;
    }

    private Collection<ChessMove> straights (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {

        return moves;
    }

}

