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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        pieceColor_ = pieceColor;
        type_ = type;
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
                for (int r = row - 1; r <= row + 1 && r <= 8; r++){
                        if (r <= 0) break;
                    for (int c = column - 1; c <= column + 1 && c <= 8; c++){
                        if (c <= 0) break;
                        if (r == row && c == column) continue;
                        ChessPosition y = new ChessPosition(r, c);
                        ChessPiece x = board.getPiece(y);
                        if (x == null){
                            validPositions.add(new ChessPosition(r, c));
                        }
                    }
                }
                break;
            case QUEEN:
                validPositions = diagonalMove(board, validPositions, row, column);
                validPositions = straightMove(board, validPositions, row, column);
                break;
            case BISHOP:
                validPositions = diagonalMove(board, validPositions, row, column);
                break;
            case KNIGHT:
                //L shape
                //if null add
                //if not null check color
                break;
            case ROOK:
                validPositions = straightMove(board, validPositions, row, column);
                break;
            case PAWN:
                //move forward 1 square unless occupied (unless at beginning and it hasn't been moved yet)
                //can move diagonally to capture
                //when they get to other side, they can get promoted to any other type (can't staying as pawn)
                break;
            default:
                break;
        }
        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();
        for (int i = 0; i < validPositions.size(); i++){
            //**check if there is a rook promotion

            //validMoves.add(new ChessMove(myPosition, validPosition[i], ))
        }
        return validMoves;
    }
    public List straightMove (ChessBoard cb, List<ChessPosition> list, int row, int column) {
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
            else if (x.pieceColor_ != getTeamColor()) {
                list.add(new ChessPosition(row, c));
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
                //taking opponent
                list.add(new ChessPosition(r, column));
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
                //taking opponent
                list.add(new ChessPosition(row, column));
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

    public List diagonalMove (ChessBoard cb, List<ChessPosition> list, int row, int column) {
        //need to check for people in squares

        int passthroughs = 0;
        for (int r = row; r <= 8; r++){
            passthroughs++;
            if ((column + passthroughs) > 8 | (column - passthroughs) < 0) {
                break;
            }
            if (column - passthroughs != column | r != row){
                ChessPosition y = new ChessPosition(r, column - passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
                } else {
                    break;
                }
                list.add(y);
            }
            if (column + passthroughs != column | r != row) {
                ChessPosition y = new ChessPosition(r, column + passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
                } else {
                    break;
                }
                list.add(y);
            }
        }
        passthroughs = 0;
        for (int r = row; row > 0; r--){
            passthroughs++;
            if ((column + passthroughs) > 8 | (column - passthroughs) < 0) {
                break;
            }
            if (column - passthroughs != column && r != row){
                ChessPosition y = new ChessPosition(r, column - passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
                } else {
                    break;
                }
                list.add(y);
            }
            if (column + passthroughs != column && r != row) {
                ChessPosition y = new ChessPosition(r, column + passthroughs);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessPosition(row, column));
                } else if (x.pieceColor_ != getTeamColor()) {
                    list.add(new ChessPosition(row, column));
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
