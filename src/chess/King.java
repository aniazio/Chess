package chess;

public class King extends Square {

    King(int col){
        super(col,ChessPiece.King);
    }

    boolean isMoveLegal(int sx, int sy, int ex, int ey, Square[][] board) {
        int horizontal = ex - sx;
        int vertical = ey - sy;
        int horizontalAbs = (horizontal > 0)? horizontal : (-1 * horizontal);
        int verticalAbs = (vertical>0)?vertical : (-1 * vertical);

        if((board[ex][ey].getPiece() == ChessPiece.Empty ||
                (board[ex][ey].getPiece() != ChessPiece.Empty && board[ex][ey].getColor() != getColor()))
                && (horizontalAbs <= 1) && (verticalAbs <= 1) && !((vertical==0) && (horizontal==0)) ) {
            setState(NORMAL_STATE);
            return true;
        }

        if(horizontal == 2 && vertical == 0 && getState() == STARTING_STATE
                && board[ex+1][ey].getState() == STARTING_STATE && board[ex+1][ey].getPiece() == ChessPiece.Rook
                && board[sx+1][sy].getPiece() == ChessPiece.Empty && board[sx+2][sy].getPiece() == ChessPiece.Empty) {
            setState(SHORT_CASTLE_STATE);
            return true;
        }

        if(horizontal == -2 && vertical == 0 && getState() == STARTING_STATE
                && board[ex-2][ey].getState() == STARTING_STATE && board[ex-2][ey].getPiece() == ChessPiece.Rook
                && board[sx-1][sy].getPiece() == ChessPiece.Empty &&
                board[sx-2][sy].getPiece() == ChessPiece.Empty && board[sx-3][sy].getPiece() == ChessPiece.Empty) {
            setState(LONG_CASTLE_STATE);
            return true;
        }
        return false;
    }

    boolean isAttackingKing(int sx, int sy, int kingX, int kingY, Square[][] plansza) {
        int horizontal = kingX - sx;
        int vertical = kingY - sy;
        int horizontalAbs = (horizontal > 0)? horizontal : (-1 * horizontal);
        int verticalAbs = (vertical>0)?vertical : (-1 * vertical);

        if((horizontalAbs <= 1) && (verticalAbs <= 1)
                && !((horizontal==0) && (vertical==0))) return true;
        else return false;
    }
}
