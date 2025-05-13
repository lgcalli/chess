package chess;

import java.util.*;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] squares = new ChessPiece[8][8];
    private HashMap <ChessPiece.PieceType, HashSet <ChessPosition>> allPieceBlack;
    private HashMap <ChessPiece.PieceType, HashSet <ChessPosition>> allPieceWhite;


    public ChessBoard() {
        allPieceBlack = new HashMap<>();
        allPieceWhite = new HashMap<>();

        updateColorMaps();
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

    void undoMoveAction (ChessPiece x, ChessPiece y, ChessMove move) {
        deletePiece(move.getEndPosition());
        if (y != null){
            addPiece(move.getEndPosition(), y);
        }
        addPiece(move.getStartPosition(), x);
    }

    public void deletePiece(ChessPosition position) {
        ChessPiece piece = getPiece(position);
        if (piece == null) return;
        addPiece(position, null);
    }

    void swapAction (ChessMove move) {
        ChessPiece x = getPiece(move.getStartPosition());
        ChessPiece y = this.getPiece(move.getEndPosition());
        deletePiece(move.getStartPosition());
        if (y != null){
            deletePiece(move.getEndPosition());
            deletePiece(move.getStartPosition());
            addPiece(move.getEndPosition(), x);
            addPiece(move.getStartPosition(), y);
        } else return;
    }

    boolean movePutsBoardInCheck (ChessMove move, ChessGame.TeamColor teamColor) {
        boolean returnValue;
        ChessPiece x = getPiece(move.getStartPosition());
        ChessPiece y = this.getPiece(move.getEndPosition());
        moveAction(move);
        if (boardInCheck(teamColor)) {
            returnValue = true;
        } else {
            returnValue = false;
        }
        undoMoveAction(x, y, move);
        return returnValue;
    }


    boolean boardInCheck (ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor opposingColor = null;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            opposingColor = ChessGame.TeamColor.BLACK;
        }
        if (teamColor == ChessGame.TeamColor.BLACK) {
            opposingColor = ChessGame.TeamColor.WHITE;
        }
        Collection<ChessPosition> opposingEndPositions = this.calculateTeamEndPositions(opposingColor);
        if (opposingEndPositions.contains(getAllPieceColorIndividualByType(ChessPiece.PieceType.KING, teamColor))){
            return true;
        } else {
            return false;
        }
    }

    public Collection <ChessPosition> calculateTeamStartPositions (ChessGame.TeamColor x){
        Collection<ChessPosition> positions = new HashSet<>();
        HashSet <ChessMove> teamMovesSet = calculateTeamMovesSet (x);
        teamMovesSet.forEach(chessMove -> {
            positions.add(chessMove.getStartPosition());
        });
        return positions;
    }

    public Collection<ChessPosition> calculateTeamEndPositions (ChessGame.TeamColor x) {
        Collection<ChessPosition> positions = new HashSet<>();
        HashSet <ChessMove> teamMovesSet = calculateTeamMovesSet (x);
        teamMovesSet.forEach(chessMove -> {
            positions.add(chessMove.getEndPosition());
        });
        return positions;
    }

    public HashSet <ChessMove> calculateTeamMovesSet (ChessGame.TeamColor x) {
        HashSet <ChessMove> moves = new HashSet<>();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition (i, j);
                if (this.getPiece(position) != null && this.getPiece(position).getTeamColor() == x){
                    Collection <ChessMove> validChessMoves = this.getPiece(position).pieceMoves(this, position);
                    validChessMoves.forEach(chessMove -> {
                        moves.add(chessMove);
                    });
                }
            }
        }
        return moves;
    }

    public HashSet<ChessPosition> getAllPieceColorListByType (ChessPiece.PieceType pieceType, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE){
            return allPieceWhite.get(pieceType);
        } else {
            return allPieceBlack.get(pieceType);
        }
    }

    public ChessPosition getAllPieceColorIndividualByType (ChessPiece.PieceType pieceType, ChessGame.TeamColor color) {
        HashSet<ChessPosition> newSet = getAllPieceColorListByType(pieceType, color);
        if (newSet == null) return null;
        if (newSet.isEmpty()) return null;
        return newSet.stream().toList().getFirst();
    }

    public void addAllPieceColorListByType (ChessPiece piece, ChessPosition position, ChessGame.TeamColor color) {
        HashSet <ChessPosition> newSet = this.getAllPieceColorListByType(piece.getPieceType(),color);
        if (newSet == null) newSet = new HashSet<>();
        else newSet.add(position);
        if (color == ChessGame.TeamColor.WHITE){
            allPieceWhite.put(piece.getPieceType(), newSet);
        } else {
            allPieceBlack.put(piece.getPieceType(), newSet);
        }
    }

    public void deleteAllPieceColorListByType (ChessPiece piece, ChessPosition position, ChessGame.TeamColor color) {
        HashSet <ChessPosition> newSet = this.getAllPieceColorListByType(piece.getPieceType(),color);
        if (newSet == null) return;
        else newSet.remove(position);
        if (color == ChessGame.TeamColor.WHITE){
            allPieceWhite.put(piece.getPieceType(), newSet);
        } else {
            allPieceBlack.put(piece.getPieceType(), newSet);
        }
    }

    public void updateColorMaps () {
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (squares[i][j] != null){
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.WHITE){
                        addAllPieceColorListByType(squares[i][j], new ChessPosition(i + 1, j + 1), ChessGame.TeamColor.WHITE);
                    }
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.BLACK){
                        addAllPieceColorListByType(squares[i][j], new ChessPosition(i + 1, j + 1), ChessGame.TeamColor.BLACK);

                    }
                }
            }
        }
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares [position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            squares[2][i] = null;
            squares[3][i] = null;
            squares[4][i] = null;
            squares[5][i] = null;
        }

        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        updateColorMaps();
    }

    @Override
    public String toString() {
        return printChessBoard ();
    }

    public String printChessBoard () {
        StringBuilder s = new StringBuilder("\nCHESSBOARD\n");
        for (int i = 7; i >= 0; i--){
            for (int j = 0; j < 7; j++){
                if (squares[i][j] == null){
                    s.append("[ | ]");
                } else {
                    if (squares[i][j].getTeamColor() == ChessGame.TeamColor.WHITE) {
                        s.append("[w|");
                    } else if (squares[i][j].getTeamColor() == ChessGame.TeamColor.BLACK){
                        s.append("[b|");
                    }
                    if (squares[i][j].getPieceType() == ChessPiece.PieceType.KING){
                        s.append("K]");
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.QUEEN){
                        s.append("Q]");
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.KNIGHT){
                        s.append("k]");
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.ROOK){
                        s.append("r]");
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.BISHOP){
                        s.append("B]");
                    }
                    else if (squares[i][j].getPieceType() == ChessPiece.PieceType.PAWN){
                        s.append("P]");
                    }
                }
            }
            s.append('\n');
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
