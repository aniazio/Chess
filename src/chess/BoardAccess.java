package chess;

import chess.pieces.*;

public class BoardAccess {

    Square[][] emptyBoard = new Square[8][8];     //board with empty squares
    Square[][] board = new Square[8][8];          //board with pieces (on empty squares it points empty board)
    int blackKingX, blackKingY;
    int whiteKingX, whiteKingY;
    private final int EMPTY_SQUARE_COLOR = 2;
    private final int BLACK = 0;
    private final int WHITE = 1;

    BoardAccess() {
        for(int i=0; i<8;i++){
            for(int j=0; j<8; j++) {
                emptyBoard[i][j] = new Empty(EMPTY_SQUARE_COLOR);
                board[i][j] = emptyBoard[i][j];
            }
        }

        board[1][0] = new Knight(WHITE);
        board[6][0] = new Knight(WHITE);
        board[1][7] = new Knight(BLACK);
        board[6][7] = new Knight(BLACK);

        board[2][0] = new Bishop(WHITE);
        board[5][0] = new Bishop(WHITE);
        board[2][7] = new Bishop(BLACK);
        board[5][7] = new Bishop(BLACK);

        board[0][0] = new Rook(WHITE);
        board[7][0] = new Rook(WHITE);
        board[0][7] = new Rook(BLACK);
        board[7][7] = new Rook(BLACK);

        board[3][0] = new Queen(WHITE);
        board[3][7] = new Queen(BLACK);
        board[4][0] = new King(WHITE);
        board[4][7] = new King(BLACK);

        whiteKingX =4; whiteKingY =0;
        blackKingX =4; blackKingY =7;

        for(int i=0; i<8;i++) {
            board[i][1] = new Pawn(WHITE);
            board[i][6] = new Pawn(BLACK);
        }
    }

    boolean isCheck(int attackingColor, int kingX, int kingY){            //(kingX, kingY) - cords of defender's king
        for(int i=0;i<8;i++) {
            for(int j=0; j<8; j++) {
                if(board[i][j].getColor() == attackingColor && board[i][j].isAttackingKing(i, j, kingX, kingY, board)) {
                    return true;}
            }
        }
        return false;
    }

    boolean isMate(int attackingColor, int kingX, int kingY){
        boolean mate=true;

        //we must check all possible moves from (i,j) to (k,l), if they can defend the king
        for(int i=0;i<8;i++) {              //(i,j) - cords of defender's piece
            for(int j=0;j<8;j++) {

                if(board[i][j].getColor() == ((attackingColor+1)%2)) {
                    for(int k=0;k<8;k++) {              //(k,l) - where piece can move
                        for(int l=0;l<8;l++){
                            int previousState = board[i][j].getState();
                            if((k != i || l != j) && board[i][j].isMoveLegal(i,j,k,l, board)) {
                                Square sqr = board[k][l];
                                board[k][l] = board[i][j];
                                board[i][j] = emptyBoard[i][j];

                                if (board[k][l].getPiece() == ChessPiece.King) {
                                    kingX = k;
                                    kingY = l;
                                    if (!isCheck(attackingColor, kingX, kingY)) mate = false;
                                    kingX = i;
                                    kingY = j;
                                } else if (!isCheck(attackingColor, kingX, kingY)) mate = false;

                                board[i][j] = board[k][l];
                                board[k][l] = sqr;
                                board[i][j].setState(previousState);
                            }
                        }
                    }
                }
            }
        }
        return mate;
    }
}