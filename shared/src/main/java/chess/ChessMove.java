package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition startPosition_;
    private ChessPosition endPosition_;
    private ChessPiece.PieceType promotionPiece_;
    boolean isSwap;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        startPosition_ = startPosition;
        endPosition_ = endPosition;
        promotionPiece_ = promotionPiece;
        isSwap = false;
    }

    public void setIsSwap (boolean x){
        this.isSwap = x;
    }

    public boolean getIsSwap () {
        return this.isSwap;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() { return startPosition_; }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition_;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece_;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition_, chessMove.startPosition_) && Objects.equals(endPosition_, chessMove.endPosition_) && promotionPiece_ == chessMove.promotionPiece_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition_, endPosition_, promotionPiece_);
    }

    @Override
    public String toString() {
        return "Move (" +
                startPosition_.toString() +
                ", " + endPosition_.toString() +
                ')';
    }
}
