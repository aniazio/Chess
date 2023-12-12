package chess.pieces;

import chess.ChessPiece;
import chess.pieces.Square;

public class Pawn extends Square {

    public Pawn(int col) {
        super(col, ChessPiece.Pawn);
    }

    public boolean isMoveLegal(int sx, int sy, int ex, int ey, Square[][] board) {
        int horizontal = ex - sx;
        int vertical = ey - sy;
        int horizontalAbs = (horizontal > 0)? horizontal : (-1 * horizontal);
        int col = (getColor() == 1) ? 1 : -1;

        //normal move
        if(horizontal == 0 && vertical == col && board[ex][ey].getPiece() == ChessPiece.Empty) {
            setState(NORMAL_STATE);
            return true;
        }

        //2-sqared move
        if(horizontal == 0 && vertical == (2*col) && getState()==STARTING_STATE
                && board[ex][ey-col].getPiece() == ChessPiece.Empty && board[ex][ey].getPiece() == ChessPiece.Empty) {
            setState(AFTER_2SQUARE_PAWN_MOVE1);
            return true;
        }

        //normal capture
        if(horizontalAbs == 1 && vertical == col
                && board[ex][ey].getPiece() != ChessPiece.Empty && board[ex][ey].getColor() != getColor()) {
            setState(NORMAL_STATE);
            return true;
        }

        //en passant
        if(horizontalAbs == 1 && vertical == col && board[ex][ey].getPiece() == ChessPiece.Empty
                && board[ex][sy].getPiece() == ChessPiece.Pawn && board[ex][sy].getColor() != getColor()
                && (board[ex][sy].getState()%2) == AFTER_2SQUARE_PAWN_MOVE1) {
            setState(AFTER_EN_PASSANT);
            return true;
        }
        return false;
    }

    public boolean isAttackingKing(int sx, int sy, int kingX, int kingY, Square[][] board) {
        int horizontal= kingX -sx;
        int vertical= kingY -sy;
        int horizontalAbs=(horizontal>0)? horizontal : (-1)*horizontal;
        int col = (getColor()==1)? 1 : -1;

        if(vertical == col && horizontalAbs == 1) return true;
        return false;
    }
}
