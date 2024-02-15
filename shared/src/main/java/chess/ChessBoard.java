package chess;

import java.lang.Cloneable;
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
        updateColorMaps();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (piece == null) return;
        squares[position.getRow()][position.getColumn()] = piece;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            allPieceWhite.put(piece.getPieceType(), position);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            allPieceBlack.put(piece.getPieceType(), position);
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     */
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

    void moveAction (ChessMove move)  {
        ChessPiece x = getPiece(move.getStartPosition());
        ChessPiece y = this.getPiece(move.getEndPosition());
        deletePiece(move.getStartPosition());
        if (y != null){
            deletePiece(move.getEndPosition());
        }
        if (move.getPromotionPiece() != null){
            addPiece(move.getEndPosition(), new ChessPiece(x.getTeamColor(), move.getPromotionPiece()));
        } else {
            addPiece(move.getEndPosition(), x);
        }
    }


    boolean movePutsBoardInCheck (ChessMove move, ChessGame.TeamColor teamColor) {
        //System.out.println("Before Move");
        //System.out.println(this.toString());

        boolean returnValue;
        ChessPiece x = getPiece(move.getStartPosition());
        ChessPiece y = this.getPiece(move.getEndPosition());
        deletePiece(move.getStartPosition());
        if (y != null){
            deletePiece(move.getEndPosition());
        }
        if (move.getPromotionPiece() != null){
            addPiece(move.getEndPosition(), new ChessPiece(x.getTeamColor(), move.getPromotionPiece()));
        } else {
            addPiece(move.getEndPosition(), x);
        }
        if (boardInCheck(teamColor)) {
            //System.out.println("No, in check...removing move: " + move.toString());
            //moves.remove(chessMove);
            //System.out.println(this.toString());
            returnValue = true;
        } else {
            //System.out.println("Yes");
            //System.out.println(this.toString());
            returnValue = false;
        }

        //System.out.println("After Move");
        //System.out.println(this.toString());

        deletePiece(move.getEndPosition());
        if (y != null){
            addPiece(move.getEndPosition(), y);
        }
        addPiece(move.getStartPosition(), x);

        //System.out.println("After Reset");
        //System.out.println(this.toString());

        return returnValue;
    }

    boolean boardInCheck (ChessGame.TeamColor teamColor) {
        //System.out.println("\n----- In Check?");
        ChessGame.TeamColor opposingColor = null;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            opposingColor = ChessGame.TeamColor.BLACK;
        }
        if (teamColor == ChessGame.TeamColor.BLACK) {
            opposingColor = ChessGame.TeamColor.WHITE;
        }
        //System.out.println(this.toString());
        Collection<ChessPosition> opposingEndPositions = this.calculateTeamEndPositions(opposingColor);
        if (opposingEndPositions.contains(this.getAllPieceColor(teamColor).get(ChessPiece.PieceType.KING))) {
            System.out.println("yes");
            //System.out.println(opposingEndPositions.toString());
            //System.out.println("King position = " + this.getAllPieceColor(teamColor).get(ChessPiece.PieceType.KING).toString());
            //System.out.println("\n-----");
            return true;
        } else {
            System.out.println("nope");
            //System.out.println("\n-----");
            return false;
        }
    }

    public Collection <ChessPosition> calculateTeamStartPositions (ChessGame.TeamColor x){
        Collection<ChessPosition> positions = new HashSet<>();
        HashMap <ChessPosition, Collection <ChessMove>> y = calculateTeamMovesMap (x);
        y.forEach((ChessPosition, ChessMoveDataSet) -> {
            ChessMoveDataSet.forEach ((ChessMove) -> {
                positions.add(ChessMove.getStartPosition());
            });
        });
        return positions;
    }

    public Collection<ChessPosition> calculateTeamEndPositions (ChessGame.TeamColor x) {
        Collection<ChessPosition> positions = new HashSet<>();
        HashMap <ChessPosition, Collection <ChessMove>> y = calculateTeamMovesMap (x);
        y.forEach((ChessPosition, ChessMoveDataSet) -> {
            ChessMoveDataSet.forEach ((ChessMove) -> {
                positions.add(ChessMove.getEndPosition());
            });
        });
        return positions;
    }

    public HashMap <ChessPosition, Collection <ChessMove>> calculateTeamMovesMap (ChessGame.TeamColor x) {
        HashMap <ChessPosition, Collection <ChessMove>> moves = new HashMap<>();
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                ChessPosition position = new ChessPosition (i, j);
                if (this.getPiece(position) != null && this.getPiece(position).getTeamColor() == x){
                    Collection <ChessMove> validChessMoves = this.getPiece(position).pieceMoves(this, position);
                    moves.put(position, validChessMoves);
                }
            }
        }
        return moves;
    }

    public HashMap<ChessPiece.PieceType, ChessPosition> getAllPieceColor (ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE){
            return allPieceWhite;
        } else {
            return allPieceBlack;
        }
    }

    public void updateColorMaps () {
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

    /*
    public boolean moveEndangersKing (ChessGame.TeamColor color, ChessMove move){

    }


     */
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
                    s = s + "[ | ]";
                } else {
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.WHITE) {
                        s = s + "[w|";
                    } else if (squares[i][j].getTeamColor() == ChessGame.TeamColor.BLACK){
                        s = s + "[b|";
                    }
                    if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING){
                        s = s + "K]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.QUEEN){
                        s = s + "Q]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.KNIGHT){
                        s = s + "k]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.ROOK){
                        s = s + "r]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.BISHOP){
                        s = s + "B]";
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.PAWN){
                        s = s + "P]";
                    }
                }
            }
            s = s + '\n';
        }
        return s;
    }
}
