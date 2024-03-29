package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row_;
    private int col_;

    public ChessPosition(int row, int col) {
        row_ = row;
        col_ = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row_;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col_;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row_ == that.row_ && col_ == that.col_;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row_, col_);
    }

    @Override
    public String toString() {
        return "[" + (row_) +
                ", " + (col_) +
                ']';
    }
}
