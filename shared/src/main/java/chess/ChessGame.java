package chess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
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

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection <ChessMove> ifNullMoves = new HashSet <ChessMove>();
        ChessPiece x = getBoard().getPiece(startPosition);
        if (x == null) return ifNullMoves;
        Collection <ChessMove> moves = x.pieceMoves(getBoard(), startPosition);
        if (x.getPieceType() == ChessPiece.PieceType.KING) {
            if (x.getTeamColor() == TeamColor.WHITE){
                Collection <ChessPosition> blackEndPositions = calculateTeamEndPositions(TeamColor.BLACK);
                blackEndPositions.forEach(ChessPosition -> {
                    ChessMove chessMove = new ChessMove(startPosition, ChessPosition, null);
                    if (moves.contains(chessMove)){
                        moves.remove(chessMove);
                    }
                });
            } else if (x.getTeamColor() == TeamColor.BLACK){
                Collection <ChessPosition> whiteEndPositions = calculateTeamEndPositions(TeamColor.WHITE);
                whiteEndPositions.forEach(ChessPosition -> {
                    ChessMove chessMove = new ChessMove(startPosition, ChessPosition, null);
                    if (moves.contains(chessMove)){
                        moves.remove(chessMove);
                    }
                });
            }

        }

        //If changing this position puts the king in danger...make king in danger function
        //MAKE inCheck function for ChessBoard???

        //Check for Castling
            /*
            This is a special move where the King and a Rook move simultaneously. The castling move can only be taken when 4 conditions are met:
            1. Neither the King nor Rook have moved since the game started
            2. There are no pieces between the King and the Rook
            3. The King is not in Check
            4. Both your Rook and King will be safe after making the move (cannot be captured by any enemy pieces).
            To Castle, the King moves 2 spaces towards the Rook, and the Rook "jumps" the king moving to the position next to and on the other side of the King. This is represented in a ChessMove as the king moving 2 spaces to the side.
            */


        //Check for En Passant
            /*
            This is a special move taken by a Pawn in response to your opponent double moving a Pawn.
            If your opponent double moves a pawn so it ends next to yours (skipping the position where your pawn could have captured their pawn),
                then on your immediately following turn your pawn may capture their pawn as if their pawn had only moved 1 square.
            This is as if your pawn is capturing their pawn mid motion, or In Passing.
            */

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection <ChessMove> moves = validMoves(move.getStartPosition());
        System.out.println(moves.toString());
        ChessBoard boardUpdate = getBoard();
        ChessPiece x = boardUpdate.getPiece(move.getStartPosition());
        if (x.getTeamColor() != getTeamTurn()) {
            System.out.println("TeamTurn = " + getTeamTurn().toString() + "\nPieceTeam = " + x.getTeamColor());
            throw new InvalidMoveException("Invalid Move: Not your piece");
        } else if (moves.contains(move)){
            if (isInCheck(getTeamTurn()) && (x.getPieceType() != ChessPiece.PieceType.KING)){
                //need to fix so it either protects king OR it makes king move
                new InvalidMoveException("Invalid Move: King in check");

            }
            ChessPiece y = boardUpdate.getPiece(move.getEndPosition());
            boardUpdate.deletePiece(move.getStartPosition());
            if (y != null){
                boardUpdate.deletePiece(move.getEndPosition());
            }
            if (move.getPromotionPiece() != null){
                boardUpdate.addPiece(move.getEndPosition(), new ChessPiece(x.getTeamColor(), move.getPromotionPiece()));
            } else {
                boardUpdate.addPiece(move.getEndPosition(), x);
            }
            setBoard(boardUpdate);
        } else {
            throw new InvalidMoveException("Invalid Move: Piece not able to move to chosen position");
        }
        if (getTeamTurn() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else if (getTeamTurn() == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingColor = null;
        if (teamColor == TeamColor.WHITE) {
            opposingColor = TeamColor.BLACK;
        }
        if (teamColor == TeamColor.BLACK) {
            opposingColor = TeamColor.WHITE;
        }

        System.out.println(getBoard().toString());
        Collection<ChessPosition> opposingEndPositions = calculateTeamEndPositions(opposingColor);
        ChessPosition king = getBoard().getAllPieceColor(teamColor).get(ChessPiece.PieceType.KING);

        if (opposingEndPositions.contains(getBoard().getAllPieceColor(teamColor).get(ChessPiece.PieceType.KING))) {
            return true;
        } else {
            return false;
        }
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
        ChessPosition x = getBoard().getAllPieceColor(teamColor).get(ChessPiece.PieceType.KING);
        Collection<ChessMove> kingMoves = validMoves (x);
        Collection <ChessPosition> opposingEndPositions = calculateTeamEndPositions(opposingColor);
        if (opposingEndPositions.containsAll(kingMoves)) {
            inCheckMate.set(true);
        }


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
        System.out.println(teamColor.toString());
        AtomicBoolean x = new AtomicBoolean(true);
        if (teamColor == TeamColor.WHITE){
            Collection <ChessPosition> whiteStartPositions = calculateTeamStartPositions(TeamColor.WHITE);
            whiteStartPositions.forEach(position -> {
                if (!validMoves(position).isEmpty()){
                    x.set(false);
                }
            });
        } else if (teamColor == TeamColor.BLACK){
            Collection <ChessPosition> blackStartPositions = calculateTeamStartPositions(TeamColor.BLACK);
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
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
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
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == x){
                    Collection <ChessMove> validChessMoves = board.getPiece(position).pieceMoves(board, position);
                    moves.put(position, validChessMoves);
                }
            }
        }
        return moves;
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
