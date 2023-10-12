package chess;

public class Knight extends Square {

    Knight(int col){
        super(col,ChessPiece.Knight);
    }

    boolean isMoveLegal(int sx, int sy, int ex, int ey, Square[][] board) {
        int horizontal = ex - sx;
        int vertical = ey - sy;
        int horizontalAbs = (horizontal>0)?horizontal : (-1 * horizontal);
        int verticalAbs = (vertical > 0)? vertical : (-1 * vertical);

        if(horizontalAbs > 0 && verticalAbs > 0 && horizontalAbs + verticalAbs == 3){
            setState(NORMAL_STATE);
            return(true);
        }
        else return false;
    }

    boolean isAttackingKing(int sx, int sy, int kingX, int kingY, Square[][] board) {
        int horizontal = kingX - sx;
        int vertical = kingY - sy;
        int horizontalAbs = (horizontal>0)?horizontal : (-1 * horizontal);
        int verticalAbs = (vertical > 0)? vertical : (-1 * vertical);

        if(horizontalAbs > 0 && verticalAbs > 0 && horizontalAbs + verticalAbs == 3) {
            return true;
        } else return false;
    }
}
