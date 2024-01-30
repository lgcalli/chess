package chess;

import java.util.HashSet;
import java.util.Collection;

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
        return pieceColor_;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type_;
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
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        switch (type_) {
            case KING:
                validMoves = surroundingMove(myPosition, board, validMoves, row, column);
                break;
            case QUEEN:
                validMoves = surroundingMove(myPosition, board, validMoves, row, column);
                validMoves = diagonalMove(myPosition, board, validMoves, row, column);
                validMoves = straightMove(myPosition, board, validMoves, row, column);
                break;
            case BISHOP:
                validMoves = diagonalMove(myPosition, board, validMoves, row, column);
                break;
            case KNIGHT:
                jumpCheck(myPosition, board, validMoves, row + 1, column + 2);
                jumpCheck(myPosition, board, validMoves, row - 1, column + 2);
                jumpCheck(myPosition, board, validMoves, row + 1, column - 2);
                jumpCheck(myPosition, board, validMoves, row - 1, column - 2);

                jumpCheck(myPosition, board, validMoves, row + 2, column + 1);
                jumpCheck(myPosition, board, validMoves, row + 2, column - 1);
                jumpCheck(myPosition, board, validMoves, row - 2, column + 1);
                jumpCheck(myPosition, board, validMoves, row - 2, column - 1);
                break;
            case ROOK:
                validMoves = straightMove(myPosition, board, validMoves, row, column);
                break;
            case PAWN:
                if (pieceColor_ == ChessGame.TeamColor.WHITE) {
                    if (row == 2){
                        for (int offset = 1; offset <= 2; offset++) {
                            ChessPosition y = new ChessPosition(row + offset, column);
                            ChessPiece x = board.getPiece(y);
                            if (x == null) {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            } else {
                                break;
                            }
                        }
                    } else {
                        ChessPosition y = new ChessPosition(row + 1, column);
                        ChessPiece x = board.getPiece(y);
                        if (x == null) {
                            if (row + 1 == 8) {
                                validMoves.add(new ChessMove(myPosition, y, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            }
                        }
                    }
                    if (column + 1 <= 8) {
                        ChessPosition y = new ChessPosition(row + 1, column + 1);
                        ChessPiece x = board.getPiece(y);
                        if (x != null && x.pieceColor_ != this.pieceColor_) {
                            if (row + 1 == 8) {
                                validMoves.add(new ChessMove(myPosition, y, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            }
                        }
                    }
                    if (column - 1 > 1) {
                        ChessPosition y = new ChessPosition(row + 1, column - 1);
                        ChessPiece x = board.getPiece(y);
                        if (x != null && x.pieceColor_ != this.pieceColor_) {
                            if (row + 1 == 8) {
                                validMoves.add(new ChessMove(myPosition, y, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            }
                        }
                    }
                } else if (pieceColor_ == ChessGame.TeamColor.BLACK){
                    if (row == 7){
                        for (int offset = 1; offset <= 2; offset++) {
                            ChessPosition y = new ChessPosition(row - offset, column);
                            ChessPiece x = board.getPiece(y);
                            if (x == null) {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            } else {
                                break;
                            }
                        }
                    } else {
                        ChessPosition y = new ChessPosition(row - 1, column);
                        ChessPiece x = board.getPiece(y);
                        if (x == null) {
                            if (row - 1 == 1)  {
                                validMoves.add(new ChessMove(myPosition, y, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            }
                        }
                    }
                    if (column + 1 <= 8) {
                        ChessPosition y = new ChessPosition(row - 1, column + 1);
                        ChessPiece x = board.getPiece(y);

                        if (x != null && x.pieceColor_ != this.pieceColor_) {
                            if (row - 1 == 1) {
                                validMoves.add(new ChessMove(myPosition, y, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            }
                        }
                    }
                    if (column - 1 > 0) {
                        ChessPosition y = new ChessPosition(row - 1, column - 1);
                        ChessPiece x = board.getPiece(y);
                        if (x != null && x.pieceColor_ != this.pieceColor_) {
                            if (row - 1 == 1) {
                                validMoves.add(new ChessMove(myPosition, y, PieceType.BISHOP));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.QUEEN));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.ROOK));
                                validMoves.add(new ChessMove(myPosition, y, PieceType.KNIGHT));
                            } else {
                                validMoves.add(new ChessMove(myPosition, y, null));
                            }
                        }
                    }
                }
            default:
                break;
        }
        return validMoves;
    }
    public Collection <ChessMove> jumpCheck (ChessPosition orig, ChessBoard cb, Collection<ChessMove> list, int r, int c) {
        if (r > 8 || c > 8 || r < 1 || c < 1) return list;
        ChessPosition y = new ChessPosition(r, c);
        ChessPiece x = cb.getPiece(y);
        ChessPiece a = cb.getPiece(orig);
        if (x == null) {
            list.add(new ChessMove(orig, y, null));
        } if (x != null && x.pieceColor_ != pieceColor_){
            list.add(new ChessMove(orig, y, null));
        }
        return list;
    }

    public Collection <ChessMove> surroundingMove (ChessPosition orig, ChessBoard cb, Collection<ChessMove> list, int row, int column) {
        for (int r = row - 1; r <= row + 1 && r <= 8; r++){
            if (r <= 0) break;
            for (int c = column - 1; c <= column + 1 && c <= 8; c++){
                if (c <= 0 || c > 8) break;
                ChessPosition y = new ChessPosition(r, c);
                ChessPiece x = cb.getPiece(y);
                if (x == null){
                    list.add(new ChessMove(orig, y, null));
                } else if (x.pieceColor_ != this.pieceColor_){
                    list.add(new ChessMove(orig, y, null));
                }
            }
        }
        return list;
    }

    public Collection<ChessMove> diagonalMove(ChessPosition orig, ChessBoard cb, Collection<ChessMove> list, int row, int column) {
        for (int offset = 1; offset <= 8; offset++){
            if (row + offset > 8 || column + offset > 8) break;
            ChessPosition y = new ChessPosition(row + offset, column + offset);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }

        for (int offset = 1; offset <= 8; offset++){
            if (row + offset > 8 || column - offset < 1) break;
            ChessPosition y = new ChessPosition(row + offset, column - offset);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }

        for (int offset = 1; offset <= 8; offset++){
            if (row - offset < 1 || column + offset > 8) break;
            ChessPosition y = new ChessPosition(row - offset, column + offset);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }

        for (int offset = 1; offset <= 8; offset++){
            if (row - offset < 1 || column - offset < 1) break;
            ChessPosition y = new ChessPosition(row - offset, column - offset);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }

        return list;
    }

    public Collection<ChessMove> straightMove(ChessPosition orig, ChessBoard cb, Collection<ChessMove> list, int row, int column) {
        for (int r = row + 1; r <= 8; r++){
            ChessPosition y = new ChessPosition(r, column);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }
        for (int r = row - 1; r > 0; r--){
            ChessPosition y = new ChessPosition(r, column);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }
        for (int c = column + 1; c <= 8; c++){
            ChessPosition y = new ChessPosition(row, c);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }
        for (int c = column - 1; c > 0; c--){
            ChessPosition y = new ChessPosition(row, c);
            ChessPiece x = cb.getPiece(y);
            if (x == null){
                list.add(new ChessMove(orig, y, null));
            } else if (x.pieceColor_ != this.pieceColor_){
                list.add(new ChessMove(orig, y, null));
                break;
            } else {
                break;
            }
        }
        return list;
    }


}



