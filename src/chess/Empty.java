package chess;

public class Empty extends Square {

    Empty(int col){
        super(col);
    }

    boolean isMoveLegal(int sx, int sy, int ex, int ey, Square[][] board) {
        return false;
    }

    boolean isAttackingKing(int sx, int sy, int kingX, int kingY, Square[][] board) {
        return false;
    }
}
