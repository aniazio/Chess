package chess.pieces;

import chess.ChessPiece;

abstract public class Square {

    private final int color;
    private final ChessPiece piece;
    private int state;

    public final static int STARTING_STATE = 0;
    public final static int NORMAL_STATE = 2;
    public final static int AFTER_2SQUARE_PAWN_MOVE1 = 1;   //AFTER_2SQUARE_PAWN_MOVE1 % 2 = AFTER_2SQUARE_PAWN_MOVE2 % 2
    public final static int AFTER_2SQUARE_PAWN_MOVE2 = 3;
    public final static int AFTER_EN_PASSANT = 4;
    public final static int SHORT_CASTLE_STATE = 6;
    public final static int LONG_CASTLE_STATE = 8;

    Square(int col){
        piece = ChessPiece.Empty;
        color = col;
        state = NORMAL_STATE;
    }

    Square(int col, ChessPiece pie){
        piece = pie;
        color = col;
        state = STARTING_STATE;
    }

    public int getColor() {
        return color;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public int getState() {
        return state;
    }

    public void setState(int s) {
        state = s;
    }

    public abstract boolean isMoveLegal(int sx, int sy, int ex, int ey, Square[][] board);

    public abstract boolean isAttackingKing(int sx, int sy, int kingX, int kingY, Square[][] plansza);
}
