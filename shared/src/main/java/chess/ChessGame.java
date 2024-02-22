package chess;
import javax.xml.transform.stax.StAXResult;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() { }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    public TeamColor getOppositeTeam(TeamColor color) {
        if (color == TeamColor.WHITE){
            return TeamColor.BLACK;
        } else if (color == TeamColor.BLACK){
            return TeamColor.WHITE;
        } else {
            return null;
        }
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public boolean castlingCheckMove (ChessMove move) {
        boolean hasMoved = board.getPiece(move.getStartPosition()).getPieceStatus();
        if (hasMoved) return true;
        if (move.getEndPosition() == null || move.getStartPosition() == null) return true;
        ChessPiece piece = board.getPiece(move.getEndPosition());
        if (piece == null) return true;
        hasMoved = piece.getPieceStatus();
        if (hasMoved) return true;
        ChessPosition chessPosition = move.getEndPosition();
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition kingPos = board.getAllPieceColorIndividualByType(ChessPiece.PieceType.KING, board.getPiece(move.getStartPosition()).getTeamColor());
        if (board.getPiece(chessPosition).getPieceStatus()) return true;
        ChessMove x = new ChessMove(startPosition, chessPosition, null);
        x.setIsSwap(true);
        if (board.movePutsBoardInCheck(x, board.getPiece(startPosition).getTeamColor())) return true;
        if (board.movePutsPieceInDanger(x, board.getPiece(startPosition).getTeamColor())) return true;
        if (kingPos.getColumn() == chessPosition.getColumn()) {
            if (kingPos.getRow() < chessPosition.getRow()) {
                for (int i = kingPos.getRow() + 1; i < chessPosition.getRow(); i++){
                    if (board.getPiece(new ChessPosition(i, kingPos.getColumn())) != null) return true;
                }
            } else if (kingPos.getRow() > chessPosition.getRow()) {
                for (int i = chessPosition.getRow() + 1; i < kingPos.getRow(); i++){
                    if (board.getPiece(new ChessPosition(i, kingPos.getColumn())) != null) return true;
                }
            } else return true;
        } else if (kingPos.getRow() == chessPosition.getRow()){
            if (kingPos.getColumn() < chessPosition.getColumn()){
                for (int i = kingPos.getColumn() + 1; i < chessPosition.getColumn(); i++){
                    if (board.getPiece(new ChessPosition(kingPos.getRow(), i)) != null) return true;
                }
            } else if (kingPos.getColumn() > chessPosition.getColumn()){
                for (int i = chessPosition.getColumn() + 1; i < kingPos.getColumn(); i++){
                    if (board.getPiece(new ChessPosition(kingPos.getRow(), i)) != null) return true;
                }
            } else return true;
        } else {
            return true;
        }
        return false;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //System.out.println("Checking Valid Moves for Board:");
        System.out.println(board.toString());
        if (board.getPiece(startPosition) == null) return null;
        TeamColor teamColor = board.getPiece(startPosition).getTeamColor();
        Collection <ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        moves.removeIf(chessMove -> {
            //System.out.println("Is  --- " + chessMove.toString() + " ---  a valid Move?");
            return board.movePutsBoardInCheck(chessMove, teamColor);
        });

        //Check for Castling
            /*
            This is a special move where the King and a Rook move simultaneously. The castling move can only be taken when 4 conditions are met:
            1. Neither the King nor Rook have moved since the game started
            2. There are no pieces between the King and the Rook
            3. The King is not in Check
            4. Both your Rook and King will be safe after making the move (cannot be captured by any enemy pieces).
            To Castle, the King moves 2 spaces towards the Rook, and the Rook "jumps" the king moving to the position next to and on the other side of the King. This is represented in a ChessMove as the king moving 2 spaces to the side.
            */
        /*
        ChessPosition kingPos = board.getAllPieceColorIndividualByType(ChessPiece.PieceType.KING, getTeamTurn());
        ChessPiece x = board.getPiece(startPosition);
        ChessPiece.PieceType type = x.getPieceType();
        boolean hasMoved = true;
        if (x != null) {
            hasMoved = x.getPieceStatus();
        }
        if (!hasMoved && !isInCheck(getTeamTurn()) && (type == ChessPiece.PieceType.ROOK || type == ChessPiece.PieceType.KING)){
            if (type == ChessPiece.PieceType.KING) {
                HashSet<ChessPosition> allRooks = board.getAllPieceColorListByType(ChessPiece.PieceType.ROOK, getTeamTurn());
                if (allRooks == null) allRooks = new HashSet<>();
                allRooks.removeIf(chessPosition -> {
                    ChessMove move = new ChessMove(kingPos, chessPosition ,null);
                    move.setIsSwap(true);
                    return castlingCheckMove(move);
                });
                if (!allRooks.isEmpty()){
                    allRooks.forEach(chessPosition -> {
                        ChessMove move = new ChessMove(kingPos, chessPosition ,null);
                        move.setIsSwap(true);
                        moves.add(move);
                        ChessMove move_ = new ChessMove(chessPosition, kingPos ,null);
                        move_.setIsSwap(true);
                        moves.add(move_);
                        System.out.println("CASTLING MOVES ADDED:\n" + move.toString() + "\n" + move_.toString());
                    });
                }
            } else {
                ChessMove move = new ChessMove(startPosition, kingPos, null);
                move.setIsSwap(true);
                ChessMove move_ = new ChessMove(kingPos, startPosition, null);
                move.setIsSwap(true);
                if (!castlingCheckMove(move)){
                    moves.add(move);
                    moves.add(move_);
                    System.out.println("CASTLING MOVES ADDED:\n" + move.toString() + "\n" + move_.toString());
                }
            }
        }
        */


        //Check for En Passant
            /*
            This is a special move taken by a Pawn in response to your opponent double moving a Pawn.
            If your opponent double moves a pawn so it ends next to yours (skipping the position where your pawn could have captured their pawn),
                then on your immediately following turn your pawn may capture their pawn as if their pawn had only moved 1 square.
            This is as if your pawn is capturing their pawn mid motion, or In Passing.
       */


        //System.out.println(board.toString());
        return moves;



    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (move == null ) return;
        if (move.getStartPosition() == null || move.getEndPosition() == null) return;
        Collection <ChessMove> moves = validMoves(move.getStartPosition());
        //System.out.println(moves.toString());
        ChessPiece x = board.getPiece(move.getStartPosition());
        ChessPiece y = board.getPiece(move.getEndPosition());
        if (x != null && x.getTeamColor() != getTeamTurn()) {
            //System.out.println("TeamTurn = " + getTeamTurn().toString() + "\nPieceTeam = " + x.getTeamColor());
            throw new InvalidMoveException("Invalid Move: Not your piece");
        } else if (moves.contains(move)){
            ChessBoard updateBoard = board;
            if (move.getIsSwap()){
                updateBoard.swapAction(move);
                x.setPieceStatus(true);
                y.setPieceStatus(true);
            } else {
                updateBoard.moveAction(move);
                x.setPieceStatus(true);
            }
            setBoard(updateBoard);
        } else {
           throw new InvalidMoveException("Invalid Move: Piece not able to move to chosen position");
        }
        if (getTeamTurn() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
        board.updateColorMaps();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return board.boardInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        TeamColor opposingColor = null;
        if (teamColor == TeamColor.WHITE) {
            opposingColor = TeamColor.BLACK;
        }
        if (teamColor == TeamColor.BLACK) {
            opposingColor = TeamColor.WHITE;
        }
        if (!isInCheck(teamColor)) return false;
        AtomicBoolean inCheckMate = new AtomicBoolean(true);


        ChessPosition kingPos = getBoard().getAllPieceColorIndividualByType(ChessPiece.PieceType.KING, teamColor);
        Collection<ChessMove> kingMoves = validMoves(kingPos);
        Collection<ChessPosition> kingEndPositions = new HashSet<>();
        if (!kingMoves.isEmpty()) {
            kingMoves.forEach(chessMove -> {
                kingEndPositions.add(chessMove.getEndPosition());
            });
        }
        Collection <ChessPosition> opposingEndPositions = board.calculateTeamEndPositions(opposingColor);
        if (kingEndPositions == null){
            if (isInCheck(teamColor)) inCheckMate.set(true);
            else inCheckMate.set(false);
        } else if (kingEndPositions.containsAll(opposingEndPositions)) {
            inCheckMate.set(true);
        }
        if (inCheckMate.get()) return inCheckMate.get();

        return inCheckMate.get();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //System.out.println(teamColor.toString());
        AtomicBoolean x = new AtomicBoolean(true);
        if (teamColor == TeamColor.WHITE){
            Collection <ChessPosition> whiteStartPositions = board.calculateTeamStartPositions(TeamColor.WHITE);
            whiteStartPositions.forEach(position -> {
                if (!validMoves(position).isEmpty()){
                    x.set(false);
                }
            });
        } else if (teamColor == TeamColor.BLACK){
            Collection <ChessPosition> blackStartPositions = board.calculateTeamStartPositions(TeamColor.BLACK);
            blackStartPositions.forEach(position -> {
                if (!validMoves(position).isEmpty()){
                    x.set(false);
                }
            });
        }
        return x.get();
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        this.board.updateColorMaps();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessGame chessGame)) return false;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turn=" + turn +
                ", board=" + board +
                '}';
    }
}
