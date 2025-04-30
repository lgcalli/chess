package chess;

import java.security.cert.CollectionCertStoreParameters;
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
                moves = surrounding(board, myPosition, moves);
                break;
            case QUEEN:
                moves = diagonals(board, myPosition, moves);
                moves = straights(board, myPosition, moves);
                break;
            case BISHOP:
                moves = diagonals(board, myPosition, moves);
                break;
            case KNIGHT:
                int row = myPosition.getRow();
                int column = myPosition.getColumn();
                moves = jumps(board, myPosition, moves, row + 2, column + 1);
                moves = jumps(board, myPosition, moves, row - 2, column - 1);
                moves = jumps(board, myPosition, moves, row + 2, column - 1);
                moves = jumps(board, myPosition, moves, row - 2, column + 1);
                moves = jumps(board, myPosition, moves, row + 1, column + 2);
                moves = jumps(board, myPosition, moves, row - 1, column - 2);
                moves = jumps(board, myPosition, moves, row + 1, column - 2);
                moves = jumps(board, myPosition, moves, row - 1, column + 2);
                break;
            case ROOK:
                moves = straights(board, myPosition, moves);
                break;
            case PAWN:
                moves = pawn(board, myPosition, moves);
                break;
        }
        return moves;
    }

    private Collection<ChessMove> surrounding (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves ) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        //check surrounding spaces above, below, and next to the current position
        for (int i = row - 1; i <= row + 1; i++){
            for (int j = column - 1; j <= column + 1; j++){
                if (i == row && j == column) continue;
                if (i < 1 || j < 1) continue;
                if (i > 8 || j > 8) continue;
                ChessPosition newPosition = new ChessPosition(i, j);
                if (board.getPiece(newPosition) == null){
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
                } else if (board.getPiece(newPosition).getTeamColor() != pieceColor) {
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    moves.add(newMove);
                }
            }
        }
        return moves;
    }


    private Collection<ChessMove> jumps (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int column) {
       //if the jump is within bounds, check if there is nothing in the spot or if it is a capturable piece
        if (row >= 1 && column >= 1 && row <= 8 && column <= 8){
            ChessPosition position = new ChessPosition (row, column);
            ChessPiece piece = board.getPiece(position);
            if (piece == null){
                //if there is nothing in the spot
                moves.add(new ChessMove(myPosition, position, null));
            } else if (piece.getTeamColor() != pieceColor) {
                //if the spot has an enemy piece that can be captured
                moves.add(new ChessMove(myPosition, position, null));
            }
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
        //define reference variables
        int i = myPosition.getRow() + 1;
        int j = myPosition.getColumn() + 1;
        int k = myPosition.getRow() - 1;
        int l = myPosition.getColumn() - 1;
        //check to the right
        while (i <= 8) {
            ChessPosition newPosition = new ChessPosition(i, myPosition.getColumn());
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
            i++;
        }
        //check above
        while (j <= 8) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), j);
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
            j++;
        }
        //check to the left
        while (k >= 1) {
            ChessPosition newPosition = new ChessPosition(k, myPosition.getColumn());
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
            k--;
        }
        //check below
        while (l >= 1) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow(), l);
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
            l--;
        }
        return moves;
    }

    private Collection<ChessMove> pawn (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();

        if (pieceColor == ChessGame.TeamColor.WHITE){
            if (row == 2){

            } else {

            }
        } else if (pieceColor == ChessGame.TeamColor.BLACK){
            if (row == 7){

            } else {

            }
        }

        return moves;
    }
}

