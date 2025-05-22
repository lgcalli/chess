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
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

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
                surrounding(board, myPosition, moves);
                break;
            case QUEEN:
                diagonals(board, myPosition, moves);
                straights(board, myPosition, moves);
                break;
            case BISHOP:
                diagonals(board, myPosition, moves);
                break;
            case KNIGHT:
                int row = myPosition.getRow();
                int column = myPosition.getColumn();
                jumps(board, myPosition, moves, row + 2, column + 1);
                jumps(board, myPosition, moves, row - 2, column - 1);
                jumps(board, myPosition, moves, row + 2, column - 1);
                jumps(board, myPosition, moves, row - 2, column + 1);
                jumps(board, myPosition, moves, row + 1, column + 2);
                jumps(board, myPosition, moves, row - 1, column - 2);
                jumps(board, myPosition, moves, row + 1, column - 2);
                jumps(board, myPosition, moves, row - 1, column + 2);
                break;
            case ROOK:
                straights(board, myPosition, moves);
                break;
            case PAWN:
                pawn(board, myPosition, moves);
                break;
        }
        return moves;
    }

    private void surrounding (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves ) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        //check surrounding spaces above, below, and next to the current position
        for (int i = row - 1; i <= row + 1; i++){
            for (int j = column - 1; j <= column + 1; j++){
                if (i == row && j == column) {
                    continue;
                }
                if (i < 1 || j < 1) {
                    continue;
                }
                if (i > 8 || j > 8) {
                    continue;
                }
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
    }

    private void jumps (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int column) {
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
    }

    private void diagonals (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
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
        i = myPosition.getRow() + 1;
        j = myPosition.getColumn() + 1;
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
        k = myPosition.getRow() - 1;
        l = myPosition.getColumn() - 1;
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
    }

    private void straights (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int i = myPosition.getRow() + 1;
        int j = myPosition.getColumn() + 1;
        int k = myPosition.getRow() - 1;
        int l = myPosition.getColumn() - 1;
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
    }

    private void pawn (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        if (pieceColor == ChessGame.TeamColor.WHITE){
            if (row + 1 > 8) {
                return;
            }
            if (row == 2){
                for (int i = 1; i <= 2; i++){
                    ChessPosition position = new ChessPosition (row + i, column);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        moves.add(new ChessMove(myPosition, position, null));
                    } else {
                        break;
                    }
                }
            } else {
                ChessPosition position = new ChessPosition(row + 1, column);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    addMovesIfEqualEight(myPosition, moves, row, position);
                }
            }
            int colLeft = column - 1;
            int colRight = column + 1;
            if (colLeft >= 1){
                ChessPosition position = new ChessPosition(row + 1, colLeft);
                ChessPiece piece = board.getPiece(position);
                if (piece != null){
                    if (piece.pieceColor == ChessGame.TeamColor.BLACK){
                        addMovesIfEqualEight(myPosition, moves, row, position);
                    }
                }
            }
            if (colRight <= 8){
                ChessPosition position = new ChessPosition(row + 1, colRight);
                ChessPiece piece = board.getPiece(position);
                if (piece != null){
                    if (piece.pieceColor == ChessGame.TeamColor.BLACK){
                        addMovesIfEqualEight(myPosition, moves, row, position);
                    }
                }
            }

        }
        if (pieceColor == ChessGame.TeamColor.BLACK){
            if (row - 1 < 1) {
                return;
            }
            if (row == 7){
                for (int i = 1; i <= 2; i++){
                    ChessPosition position = new ChessPosition (row - i, column);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        moves.add(new ChessMove(myPosition, position, null));
                    } else {
                        break;
                    }
                }
            } else {
                ChessPosition position = new ChessPosition(row - 1, column);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    addMovesIfEqualsOne(myPosition, moves, row, position);
                }
            }
            int colLeft = column - 1;
            int colRight = column + 1;
            if (colLeft >= 1){
                duplicate(board, myPosition, moves, row, colLeft);
            }
            if (colRight <= 8){
                duplicate(board, myPosition, moves, row, colRight);
            }
        }
    }



    private void addPromotionMoves (ChessPosition myPosition, Collection<ChessMove> moves, ChessPosition position){
        moves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, position, PieceType.ROOK));
        moves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
    }

    private void duplicate (ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int colRight) {
        ChessPosition position = new ChessPosition(row - 1, colRight);
        ChessPiece piece = board.getPiece(position);
        if (piece != null){
            if (piece.pieceColor == ChessGame.TeamColor.WHITE){
                addMovesIfEqualsOne(myPosition, moves, row, position);
            }
        }
    }

    private void addMovesIfEqualsOne (ChessPosition myPosition, Collection<ChessMove> moves, int row, ChessPosition position) {
        if (row - 1 == 1) {
            addPromotionMoves(myPosition, moves, position);
        } else {
            moves.add(new ChessMove(myPosition, position, null));
        }
    }

    private void addMovesIfEqualEight (ChessPosition myPosition, Collection<ChessMove> moves, int row, ChessPosition position) {
        if (row + 1 == 8) {
            addPromotionMoves(myPosition, moves, position);
        } else {
            moves.add(new ChessMove(myPosition, position, null));
        }
    }
}

