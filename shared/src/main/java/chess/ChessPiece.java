package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor_;
    private ChessPiece.PieceType type_;
    private boolean inOrigPosition;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        pieceColor_ = pieceColor;
        type_ = type;
        inOrigPosition = true;
    }

    public boolean getIsOrigPosition() {
        return this.inOrigPosition;
    }

    public void setPieceAsMoved() {
        this.inOrigPosition = false;
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
        return this.pieceColor_;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type_;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessPosition> validPositions = new ArrayList<ChessPosition>();
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        switch(type_){
            case KING:
                validPositions = surroundingSpacesMove(board, validPositions, row, column);
                break;
            case QUEEN:
                validPositions = surroundingSpacesMove(board, validPositions, row, column);
                validPositions = diagonalMove(board, validPositions, row, column);
                validPositions = straightMove(board, validPositions, row, column);
                break;
            case BISHOP:
                validPositions = diagonalMove(board, validPositions, row, column);
                break;
            case KNIGHT:
                jumpCheck(board, validPositions, row + 1, column + 2);
                jumpCheck(board, validPositions, row - 1, column + 2);
                jumpCheck(board, validPositions, row + 1, column - 2);
                jumpCheck(board, validPositions, row - 1, column - 2);

                jumpCheck(board, validPositions, row + 2, column + 1);
                jumpCheck(board, validPositions, row + 2, column - 1);
                jumpCheck(board, validPositions, row - 2, column + 1);
                jumpCheck(board, validPositions, row - 2, column - 1);
                break;
            case ROOK:
                validPositions = straightMove(board, validPositions, row, column);
                break;
            case PAWN:
                ChessPosition y = new ChessPosition(row + 1, column);
                ChessPiece x = board.getPiece(y);
                if (x == null){
                    validPositions.add(y);
                }
                if (getIsOrigPosition()) {
                    y = new ChessPosition(row + 2, column);
                    x = board.getPiece(y);
                    if (x == null){
                        validPositions.add(y);
                    }
                }
                y = new ChessPosition(row + 1, column + 1);
                x = board.getPiece(y);
                if (x != null && x.pieceColor_ != getTeamColor()){
                    validPositions.add(y);
                }
                y = new ChessPosition(row + 1, column - 1);
                x = board.getPiece(y);
                if (x != null && x.pieceColor_ != getTeamColor()){
                    validPositions.add(y);
                }
                break;
            default:
                break;
        }
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        for (int i = 0; i < validPositions.size(); i++){
            if (type_ == PieceType.PAWN){
                //**check if there is a pawn promotion???
                validMoves.add(new ChessMove(myPosition, validPositions.get(i), null));
            } else {
                validMoves.add(new ChessMove(myPosition, validPositions.get(i), null));
            }
        }
        return validMoves;
    }

    public List <ChessPosition> jumpCheck (ChessBoard cb, List<ChessPosition> list, int r, int c) {
        ChessPosition y = new ChessPosition(r, c);
        ChessPiece x = cb.getPiece(y);
        if (x == null || x.pieceColor_ != getTeamColor()) list.add(y);
        return list;
    }

    public List <ChessPosition> surroundingSpacesMove (ChessBoard cb, List<ChessPosition> list, int row, int column){
        for (int r = row - 1; r <= row + 1 && r <= 8; r++){
            if (r <= 0) break;
            for (int c = column - 1; c <= column + 1 && c <= 8; c++){
                if (c <= 0) break;
                if (r == row && c == column) continue;
                ChessPosition y = new ChessPosition(r, c);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(r, c));
                } else if (x.pieceColor_ != getTeamColor()){
                    list.add(new ChessPosition(r, c));
                }
            }
        }
        return list;
    }

    public List <ChessPosition> straightMove (ChessBoard cb, List<ChessPosition> list, int row, int column) {
        /*        |
                  |
          - - - - + - - - -
                  |
                  |         */
        //checking the columns farther than the column index on the same row
        //column + 1, increasing till we get to the end of the board
        for (int c = column + 1; c <= 8; c++) {
            //making a new chess position and subsequently finding whether
            //there is a piece at the index
            ChessPosition y = new ChessPosition(row, c);
            ChessPiece x = cb.getPiece(y);
            //if there is no piece, add the position to the valid list
            if (x == null){
                list.add(new ChessPosition(row, c));
            }
            //if there is a piece and that piece is on the opposite team, add the position
            //also break (no position past this point can be valid because there aren't jumps for a straight line move)
            else if (x.pieceColor_ != getTeamColor()) {
                list.add(new ChessPosition(row, c));
                break;
            }
            //else, the piece is yours, break the loop
            //(no position past this point can be valid because there aren't jumps for a straight line move)
            else {
                break;
            }
        }
        //checking the columns less than the column index on the same row
        //column - 1, decreasing till we get to the end of the board
        for (int c = column - 1; c > 0; c--){
            //making a new chess position and subsequently finding whether
            //there is a piece at the index
            ChessPosition y = new ChessPosition(row, c);
            ChessPiece x = cb.getPiece(y);
            //if there is no piece, add the position to the valid list
            if (x == null){
                list.add(new ChessPosition(row, c));
            }
            //if there is a piece and that piece is on the opposite team, add the position
            else if (x.pieceColor_ != getTeamColor()) {
                list.add(new ChessPosition(row, c));
                break;
            }
            //else, the piece is yours, break the loop
            //(no position past this point can be valid because there aren't jumps for a straight line move)
            else {
                break;
            }
        }
        //checking the rows greater than the row index on the same column
        //r + 1, increasing till we get to the end of the board
        for (int r = row + 1; r <= 8; r++){
            //making a new chess position and subsequently finding whether
            //there is a piece at the index
            ChessPosition y = new ChessPosition(r, column);
            ChessPiece x = cb.getPiece(y);
            //if there is no piece, add the position to the valid list
            if (x == null){
                list.add(new ChessPosition(r, column));
            }
            //if there is a piece and that piece is on the opposite team, add the position
            else if (x.pieceColor_ != getTeamColor()) {
                list.add(new ChessPosition(r, column));
                break;
            }
            //else, the piece is yours, break the loop
            //(no position past this point can be valid because there aren't jumps for a straight line move)
            else {
                break;
            }
        }
        //checking the rows less than the row index on the same column
        //r - 1, decreasing till we get to the end of the board
        for (int r = row - 1; r > 0; r--){
            //making a new chess position and subsequently finding whether
            //there is a piece at the index
            ChessPosition y = new ChessPosition(row, column);
            ChessPiece x = cb.getPiece(y);
            //if there is no piece, add the position to the valid list
            if (x == null){
                list.add(new ChessPosition(row, column));
            }
            //if there is a piece and that piece is on the opposite team, add the position
            else if (x.pieceColor_ != getTeamColor()) {
                list.add(new ChessPosition(row, column));
                break;
            }
            //else, the piece is yours, break the loop
            //(no position past this point can be valid because there aren't jumps for a straight line move)
            else {
                break;
            }
        }
        //return the resulting list of values for valid moves
        return list;
    }

    public List <ChessPosition> diagonalMove (ChessBoard cb, List<ChessPosition> list, int row, int column) {
         /*        \   /
                    \ /
                     x
                    / \
                   /   \         */
        //passthroughs is identified for purposes of increasing every column/row to keep diagonal
        int passthroughs = 0;
        //row starts at current row and increases till we get to the end of the board
        for (int r = row + 1; r <= 8; r++){
            passthroughs++;
            //rows increase as columns increase to get diagonal 'x' pattern
            if (column - passthroughs > 0){
                ChessPosition y = new ChessPosition(r, column - passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
                    break;
                } else {
                    break;
                }
                list.add(y);
            }
            if (column + passthroughs <= 8) {
                ChessPosition y = new ChessPosition(r, column + passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
                    break;
                } else {
                    break;
                }
                list.add(y);
            }
        }
        passthroughs = 0;
        for (int r = row; row > 0; r--){
            passthroughs++;
            if (column - passthroughs > 0){
                ChessPosition y = new ChessPosition(r, column - passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
                    break;
                } else {
                    break;
                }
                list.add(y);
            }
            if (column + passthroughs <= 8) {
                ChessPosition y = new ChessPosition(r, column + passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
                    break;
                } else {
                    break;
                }
                list.add(y);
                list.add(new ChessPosition(r, column + passthroughs));
            }
        }
        return list;
    }
}
