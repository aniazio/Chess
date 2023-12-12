package chess.pieces;

import chess.pieces.Square;

public class Empty extends Square {

    public Empty(int col){
        super(col);
    }

    public boolean isMoveLegal(int sx, int sy, int ex, int ey, Square[][] board) {
        return false;
    }

    public boolean isAttackingKing(int sx, int sy, int kingX, int kingY, Square[][] board) {
        return false;
    }
}
