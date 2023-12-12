package chess.pieces;

import chess.ChessPiece;

public class Bishop extends Square {

    public Bishop(int col){
        super(col, ChessPiece.Bishop);
    }

    public boolean isMoveLegal(int sx, int sy, int ex, int ey, Square[][] board) {
        int horizontal=ex-sx;
        int vertical=ey-sy;
        int horizontalAbs = (horizontal > 0)? horizontal : (-1 * horizontal);
        int verticalAbs = (vertical>0)?vertical : (-1 * vertical);
        int horizontalSign = (horizontal>0)? 1 : -1;
        int verticalSign = (vertical>0)?1 : -1;

        if(verticalAbs > 0 && verticalAbs == horizontalAbs){
            for(int i = 1; i < horizontalAbs; i++) {
                if(board[sx + i*horizontalSign][sy + i*verticalSign].getPiece() != ChessPiece.Empty) return false;
            }
            if(board[ex][ey].getPiece() != ChessPiece.Empty && board[ex][ey].getColor() == getColor()) return false;

            setState(NORMAL_STATE);
            return true;
        }
        else return false;
    }

    public boolean isAttackingKing(int sx, int sy, int kingX, int kingY, Square[][] board) {
        int horizontal= kingX -sx;
        int vertical= kingY -sy;
        int horizontalAbs = (horizontal > 0)? horizontal : (-1 * horizontal);
        int verticalAbs = (vertical>0)?vertical : (-1 * vertical);
        int horizontalSign = (horizontal>0)? 1 : -1;
        int verticalSign = (vertical>0)?1 : -1;

        if(horizontalAbs > 0 && verticalAbs == horizontalAbs){
            for(int i=1; i<verticalAbs; i++) {
                if(board[sx + i*horizontalSign][sy + i*verticalSign].getPiece() != ChessPiece.Empty) return false;
            }
            return true;
        }
        else return false;
    }
}