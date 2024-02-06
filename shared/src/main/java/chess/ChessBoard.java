package chess;

import java.sql.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[9][9];
    private HashMap <ChessPiece.PieceType, ChessPosition> allPieceBlack;
    private HashMap <ChessPiece.PieceType, ChessPosition> allPieceWhite;

    public ChessBoard() {
        allPieceBlack = new HashMap<>();
        allPieceWhite = new HashMap<>();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            allPieceWhite.put(piece.getPieceType(), position);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            allPieceBlack.put(piece.getPieceType(), position);
        }
    }

    public void deletePiece(ChessPosition position) {
        ChessPiece piece = squares[position.getRow()][position.getColumn()];
        if (piece == null) return;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            allPieceWhite.remove(piece.getPieceType(), position);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            allPieceBlack.remove(piece.getPieceType(), position);
        }
        squares[position.getRow()][position.getColumn()] = null;
    }

    public HashMap<ChessPiece.PieceType, ChessPosition> getAllPieceWhite () {
        return allPieceWhite;
    }

    public HashMap<ChessPiece.PieceType, ChessPosition> getAllPieceBlack () {
        return allPieceBlack;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //set 'squares' array to equal default board
        for (int i = 1; i <= 8; i++){
            squares[2][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        for (int i = 0; i <= 8; i++) {
            squares[0][i] = null;
            squares[i][0] = null;
            squares[3][i] = null;
            squares[4][i] = null;
            squares[5][i] = null;
            squares[6][i] = null;
        }
        //WHITE
        squares[1][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[1][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[1][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[1][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[1][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[1][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[1][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[1][8] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        //BLACK
        squares[8][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[8][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[8][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[8][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[8][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[8][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[8][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[8][8] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if (squares[i][j] != null){
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.WHITE){
                        allPieceWhite.put(squares[i][j].getPieceType(), new ChessPosition(i, j));
                    }
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.BLACK){
                        allPieceBlack.put(squares[i][j].getPieceType(), new ChessPosition(i, j));
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessBoard board)) return false;
        return Arrays.deepEquals(squares, board.squares);
    }

    @Override
    public String toString() {
        /*
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}'; */
        return printChessBoard ();
    }

    public String printChessBoard () {
        String s = "CHESSBOARD \n";
        for (int i = 8; i >= 1; i--){
            for (int j = 1; j < 9; j++){
                if (squares[i][j] == null){
                    s = s + "[ ]";
                } else {
                    if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING){
                        s = s + "[K]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.QUEEN){
                        s = s + "[Q]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.KNIGHT){
                        s = s + "[k]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.ROOK){
                        s = s + "[r]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.BISHOP){
                        s = s + "[B]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.PAWN){
                        s = s + "[P]";
                    }
                }
            }
            s = s + '\n';
        }
        return s;
    }
}
