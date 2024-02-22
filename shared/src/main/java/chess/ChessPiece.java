package chess;

import java.util.Collection;
import java.util.HashSet;
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
    private boolean pieceStatus;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.pieceStatus = false;
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

    boolean getPieceStatus () {
        return this.pieceStatus;
    }

    void setPieceStatus (boolean x) {
        this.pieceStatus = x;
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
        Collection<ChessMove> validMoves = new HashSet<ChessMove>();
        ChessPiece x = board.getPiece(myPosition);

        switch (x.getPieceType()) {
            case KING -> {
                validMoves = surroundingMoves(validMoves, board, myPosition);
            }
            case QUEEN -> {
                validMoves = surroundingMoves(validMoves, board, myPosition);
                validMoves = diagonalMoves(validMoves, board, myPosition);
                validMoves = straightMoves(validMoves, board, myPosition);
            }
            case BISHOP -> {
                validMoves = diagonalMoves(validMoves, board, myPosition);
            }
            case KNIGHT -> {
                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() + 2, myPosition.getColumn() + 1);
                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() + 2, myPosition.getColumn() - 1);
                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() - 2, myPosition.getColumn() + 1);
                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() - 2, myPosition.getColumn() - 1);

                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() + 1, myPosition.getColumn() + 2);
                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() + 1, myPosition.getColumn() - 2);
                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() - 1, myPosition.getColumn() + 2);
                validMoves = jumpMoves(validMoves, board, myPosition, myPosition.getRow() - 1, myPosition.getColumn() - 2);
            }
            case ROOK -> {
                validMoves = straightMoves(validMoves, board, myPosition);
            }
            case PAWN -> {
                validMoves = pawnMoves(validMoves, board, myPosition);
            }
        }
        return validMoves;
    }

    public Collection<ChessMove> jumpMoves (Collection<ChessMove>validMoves, ChessBoard board, ChessPosition myPosition, int row, int col){
        if (row >= 1 && col >= 1 && row <= 8 && col <= 8){
            ChessPosition position = new ChessPosition (row, col);
            ChessPiece piece = board.getPiece(position);
            if (piece == null){
                //if there is nothing in the spot
                validMoves.add(new ChessMove(myPosition, position, null));
            } else if (piece.getTeamColor() != pieceColor) {
                //if the spot has an enemy piece that can be captured
                validMoves.add(new ChessMove(myPosition, position, null));
            }
        }
        return validMoves;
    }

    public Collection<ChessMove> surroundingMoves (Collection<ChessMove>validMoves, ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        /*
        __________
        |__|__|__|
        |__|__|__|
        |__|__|__|
         */

        for (int i = row - 1; i <= row + 1; i++){
            for (int j = col - 1; j <= col + 1; j++){
                if (i == row && j == col) continue;
                if (i < 1 || j < 1) continue;
                if (i > 8 || j > 8) break;
                ChessPosition position = new ChessPosition (i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece == null){
                    //if there is nothing in the spot
                    validMoves.add(new ChessMove(myPosition, position, null));
                } else if (piece.getTeamColor() != pieceColor) {
                    //if the spot has an enemy piece that can be captured
                    validMoves.add(new ChessMove(myPosition, position, null));
                }
            }
        }
        return validMoves;
    }
    public Collection<ChessMove> straightMoves (Collection<ChessMove>validMoves, ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        //above
        for (int i = row + 1; i <= 8 && i > 0; i++){
            ChessPosition position = new ChessPosition (i, col);
            ChessPiece piece = board.getPiece(position);
            if (piece == null){
                //if there is nothing in the spot
                validMoves.add(new ChessMove(myPosition, position, null));
            } else if (piece.getTeamColor() != pieceColor) {
                //if the spot has an enemy piece that can be captured
                validMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else {
                //if the spot has your piece in it
                break;
            }
        }
        //below
        for (int i = row - 1; i <= 8 && i > 0; i--){
            ChessPosition position = new ChessPosition (i, col);
            ChessPiece piece = board.getPiece(position);
            if (piece == null){
                //if there is nothing in the spot
                validMoves.add(new ChessMove(myPosition, position, null));
            } else if (piece.getTeamColor() != pieceColor) {
                //if the spot has an enemy piece that can be captured
                validMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else {
                //if the spot has your piece in it
                break;
            }
        }
        //to the right
        for (int i = col + 1; i <= 8 && i > 0; i++){
            ChessPosition position = new ChessPosition (row, i);
            ChessPiece piece = board.getPiece(position);
            if (piece == null){
                //if there is nothing in the spot
                validMoves.add(new ChessMove(myPosition, position, null));
            } else if (piece.getTeamColor() != pieceColor) {
                //if the spot has an enemy piece that can be captured
                validMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else {
                //if the spot has your piece in it
                break;
            }
        }
        //to the left
        for (int i = col - 1; i <= 8 && i > 0; i--){
            ChessPosition position = new ChessPosition (row, i);
            ChessPiece piece = board.getPiece(position);
            if (piece == null){
                //if there is nothing in the spot
                validMoves.add(new ChessMove(myPosition, position, null));
            } else if (piece.getTeamColor() != pieceColor) {
                //if the spot has an enemy piece that can be captured
                validMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else {
                //if the spot has your piece in it
                break;
            }
        }
        return validMoves;
    }
    public Collection<ChessMove> diagonalMoves (Collection<ChessMove>validMoves, ChessBoard board, ChessPosition myPosition){
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        /*
        both positions incremented or decremented by same value
        ex if coords are (5,5) the closest diagonals are (6,6),(4,6),(6,4)(4,4)

         _____________
       6 |___|___|___|
       5 |___|_x_|___|
       4 |___|___|___|
           4   5   6

        4 directions: ne, nw, se, sw
        */

        boolean ne = true;
        boolean nw = true;
        boolean se = true;
        boolean sw = true;

        for (int i = 1; i < 8; i++){
            if (ne) {
                if (row + i > 8 || col + i > 8) ne = false;
                else {
                    ChessPosition position = new ChessPosition (row + i, col + i);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        //if there is nothing in the spot
                        validMoves.add(new ChessMove(myPosition, position, null));
                    } else if (piece.getTeamColor() != pieceColor) {
                        //if the spot has an enemy piece that can be captured
                        validMoves.add(new ChessMove(myPosition, position, null));
                        ne = false;
                    } else {
                        //if the spot has your piece in it
                        ne = false;
                    }
                }
            }
            if (nw) {
                if (row + i > 8 || col - i < 1) nw = false;
                else {
                    ChessPosition position = new ChessPosition (row + i, col - i);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        //if there is nothing in the spot
                        validMoves.add(new ChessMove(myPosition, position, null));
                    } else if (piece.getTeamColor() != pieceColor) {
                        //if the spot has an enemy piece that can be captured
                        validMoves.add(new ChessMove(myPosition, position, null));
                        nw = false;
                    } else {
                        //if the spot has your piece in it
                        nw = false;
                    }
                }

            }
            if (se) {
                if (row - i < 1 || col + i > 8) se = false;
                else {
                    ChessPosition position = new ChessPosition (row - i, col + i);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        //if there is nothing in the spot
                        validMoves.add(new ChessMove(myPosition, position, null));
                    } else if (piece.getTeamColor() != pieceColor) {
                        //if the spot has an enemy piece that can be captured
                        validMoves.add(new ChessMove(myPosition, position, null));
                        se = false;
                    } else {
                        //if the spot has your piece in it
                        se = false;
                    }
                }
            }
            if (sw) {
                if (row - i < 1 || col - i < 1) sw = false;
                else {
                    ChessPosition position = new ChessPosition (row - i, col - i);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        //if there is nothing in the spot
                        validMoves.add(new ChessMove(myPosition, position, null));
                    } else if (piece.getTeamColor() != pieceColor) {
                        //if the spot has an enemy piece that can be captured
                        validMoves.add(new ChessMove(myPosition, position, null));
                        sw = false;
                    } else {
                        //if the spot has your piece in it
                        sw = false;
                    }
                }
            }

        }
        return validMoves;
    }

    public Collection<ChessMove> pawnMoves (Collection<ChessMove>validMoves, ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        if (row < 1 || row > 8 || col < 1 || row > 8) return validMoves;
        /*

            WHITE

        */
        if (pieceColor == ChessGame.TeamColor.WHITE){
            //System.out.println("1");
            if (row + 1 > 8) return validMoves;
            //System.out.println("2");
            //if the piece is in its starting position
            /*
             ** Adding moves for the piece to go 1 - 2 forward depending on whether  it's blocked
             */
            if (row == 2){
                //System.out.println("3");
                for (int i = 1; i <= 2; i++){
                    //System.out.println("4");
                    ChessPosition position = new ChessPosition (row + i, col);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        //System.out.println("5");
                        //if there is nothing in the spot
                        validMoves.add(new ChessMove(myPosition, position, null));
                    } else {
                        //System.out.println("6");
                        //if the spot has a piece in it
                        break;
                    }
                }
            }
            //otherwise
            /*
             ** Adding move for the piece to go 1 forward depending on whether it's blocked
             */
            else {
                //System.out.println("7");
                ChessPosition position = new ChessPosition(row + 1, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) {
                    //System.out.println("8");
                    if (row + 1 == 8) {
                        //System.out.println("9");
                        validMoves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, position, PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
                    } else {
                        //System.out.println("10");
                        validMoves.add(new ChessMove(myPosition, position, null));
                    }
                }
            }
            /*
             ** Adding moves for diagonal captures
             */
            int col_left = col - 1;
            int col_right = col + 1;
            if (col_left >= 1){
                //System.out.println("11");
                ChessPosition position = new ChessPosition(row + 1, col_left);
                ChessPiece piece = board.getPiece(position);
                if (piece != null){
                    //System.out.println("12");
                    if (piece.pieceColor == ChessGame.TeamColor.BLACK){
                        //System.out.println("13");
                        if (row + 1 == 8) {
                            //System.out.println("14");
                            validMoves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
                        } else {
                            //System.out.println("15");
                            validMoves.add(new ChessMove(myPosition, position, null));
                        }
                    }
                }
            }
            if (col_right <= 8){
                //System.out.println("16");
                ChessPosition position = new ChessPosition(row + 1, col_right);
                ChessPiece piece = board.getPiece(position);
                if (piece != null){
                    //System.out.println("17");
                    if (piece.pieceColor == ChessGame.TeamColor.BLACK){
                        if (row + 1 == 8) {
                            //System.out.println("18");
                            validMoves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
                        } else {
                            //System.out.println("19");
                            validMoves.add(new ChessMove(myPosition, position, null));
                        }
                    }
                }
            }

        }


        /*

            BLACK

        */
        if (pieceColor == ChessGame.TeamColor.BLACK){
            if (row - 1 < 1) return validMoves;
            //if the piece is in its starting position
            /*
             ** Adding moves for the piece to go 1 - 2 forward depending on whether  it's blocked
             */
            if (row == 7){
                for (int i = 1; i <= 2; i++){
                    ChessPosition position = new ChessPosition (row - i, col);
                    ChessPiece piece = board.getPiece(position);
                    if (piece == null){
                        //if there is nothing in the spot
                        validMoves.add(new ChessMove(myPosition, position, null));
                    } else {
                        //if the spot has a piece in it
                        break;
                    }
                }
            }
            /*
             ** Adding move for the piece to go 1 forward depending on whether it's blocked
             */
            else {
                ChessPosition position = new ChessPosition(row - 1, col);
                ChessPiece piece = board.getPiece(position);
                //if there is nothing in the spot
                if (piece == null) {
                    if (row - 1 == 1) {
                        validMoves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
                        validMoves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
                        validMoves.add(new ChessMove(myPosition, position, PieceType.ROOK));
                        validMoves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
                    } else {
                        validMoves.add(new ChessMove(myPosition, position, null));
                    }
                }
            }
            /*
             ** Adding moves for diagonal captures
             */
            int col_left = col - 1;
            int col_right = col + 1;
            if (col_left >= 1){
                ChessPosition position = new ChessPosition(row - 1, col_left);
                ChessPiece piece = board.getPiece(position);
                if (piece != null){
                    if (piece.pieceColor == ChessGame.TeamColor.WHITE){
                        if (row - 1 == 1) {
                            validMoves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
                        } else {
                            validMoves.add(new ChessMove(myPosition, position, null));
                        }
                    }
                }
            }
            if (col_right <= 8){
                ChessPosition position = new ChessPosition(row - 1, col_right);
                ChessPiece piece = board.getPiece(position);
                if (piece != null){
                    if (piece.pieceColor == ChessGame.TeamColor.WHITE){
                        if (row - 1 == 1) {
                            validMoves.add(new ChessMove(myPosition, position, PieceType.BISHOP));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.QUEEN));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.ROOK));
                            validMoves.add(new ChessMove(myPosition, position, PieceType.KNIGHT));
                        } else {
                            validMoves.add(new ChessMove(myPosition, position, null));
                        }
                    }
                }
            }
        }

        return validMoves;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
