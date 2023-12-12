package chess;

import chess.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChessboardListener {

    private BoardAccess chessboard;
    private ChessSwing display;

    private final int FINISH_MOVE = 1;                  //state of a move
    private final int MOVE_FINISHED = 0;
    private final int BLACKS_MOVE = 0;                  //color, which should move now
    private final int WHITES_MOVE = 1;

    private Integer startX, startY, endX, endY;         //auxiliary variables
    private Square movedFigure, endSquare;

    private boolean mate = false;                       //boolean variables
    private boolean duringPromotion = false;
    private boolean check = false;

    private int moveStatus = MOVE_FINISHED;
    private int color = WHITES_MOVE;

    ChessboardListener(BoardAccess chessboard, ChessSwing display) {
        this.chessboard = chessboard;
        this.display = display;
    }

    public void promotionReaction(ActionEvent e) {
        String str = e.getActionCommand();
        int kolor = chessboard.board[endX][endY].getColor();

        switch(str) {
            case "Rook":
                chessboard.board[endX][endY] = new Rook(kolor);
                break;
            case "Knight":
                chessboard.board[endX][endY] = new Knight(kolor);
                break;
            case "Bishop":
                chessboard.board[endX][endY] = new Bishop(kolor);
                break;
            case "Queen":
                chessboard.board[endX][endY] = new Queen(kolor);
                break;
        }
        display.squares[endX][endY].setIcon(
                new ImageIcon("files/" + str + kolor + ".png"));
        display.pawnPromotionMenu.closePopup();
        duringPromotion = false;
    }

    public void basicAction(ActionEvent e) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mate || duringPromotion) return;

                String squareName = e.getActionCommand();
                readMove(squareName);
                moveStatus = (moveStatus + 1) % 2;
                //in the first part of the move program only reads pressed button's cords
                if (moveStatus == FINISH_MOVE) return;

                movedFigure = chessboard.board[startX][startY];
                endSquare = chessboard.board[endX][endY];
                if (movedFigure.isMoveLegal(startX, startY, endX, endY, chessboard.board)) {
                    makeMove();
                    actualizeKingCords(movedFigure, endX, endY);
                    //defender's king
                    int kingX = (color == WHITES_MOVE) ? chessboard.whiteKingX : chessboard.blackKingX;
                    int kingY = (color == WHITES_MOVE) ? chessboard.whiteKingY : chessboard.blackKingY;
                    if (chessboard.isCheck(((color + 1) % 2), kingX, kingY)) {
                        undoMove();
                        return;
                    }

                    if (chessboard.board[endX][endY].getState() == Square.SHORT_CASTLE_STATE ||
                            chessboard.board[endX][endY].getState() == Square.LONG_CASTLE_STATE) {
                        if (isKingComingThroughCheck()) {
                            undoMove();
                            return;
                        }
                    }

                    makeMoveOnDisplay();
                    updateBoardInSpecialCaseOfEnPassant();
                    updateBoardInSpecialCaseOfCastle();
                    resetPawnsState();
                    promotePawn();
                    color = (color + 1) % 2;
                    putTextInCorner();
                }
            }
        }).start();
    }

    private void readMove(String squareName) {
        if (moveStatus == MOVE_FINISHED) readFistPartOfMove(squareName);
        else readSecondPartOfMove(squareName);
    }

    private void readFistPartOfMove(String squareName) {
        int squareX = squareName.charAt(0) - 97;
        int squareY = squareName.charAt(1) - 49;
        if(chessboard.board[squareX][squareY].getColor() == (color % 2) &&
                chessboard.board[squareX][squareY].getPiece() != ChessPiece.Empty) {
            startX = squareX;
            startY = squareY;
            display.squares[startX][startY].setBackground(Color.PINK);
        } else moveStatus = (moveStatus + 1) % 2;
    }

    private void readSecondPartOfMove(String squareName) {
        endX = squareName.charAt(0) - 97;
        endY = squareName.charAt(1) - 49;
        if ((startX+startY)%2 == 1) display.squares[startX][startY].setBackground(Color.white);
        else display.squares[startX][startY].setBackground(Color.gray);
    }

    private void makeMove() {
        chessboard.board[endX][endY] = movedFigure;
        chessboard.board[startX][startY] = chessboard.emptyBoard[startX][startY];
    }

    private void actualizeKingCords(Square startingSquare, int endingSquareX, int endingSquareY) {
        if(startingSquare.getPiece() == ChessPiece.King) {
            if(color == WHITES_MOVE) {
                chessboard.whiteKingX = endingSquareX;
                chessboard.whiteKingY = endingSquareY;
            } else {
                chessboard.blackKingX = endingSquareX;
                chessboard.blackKingY = endingSquareY;
            }
        }
    }

    private void undoMove() {
        chessboard.board[startX][startY] = chessboard.board[endX][endY];
        chessboard.board[endX][endY] = endSquare;
        chessboard.board[startX][startY].setState(movedFigure.getState());
        if(movedFigure.getPiece() == ChessPiece.King) actualizeKingCords(chessboard.board[endX][endY], startX, startY);
    }

    private boolean isKingComingThroughCheck() {
        if(check) return true;
        boolean result = false;
        int sign = (chessboard.board[endX][endY].getState() == Square.SHORT_CASTLE_STATE) ? 1 : -1;

        chessboard.board[endX-sign][endY] = chessboard.board[endX][endY];
        chessboard.board[endX][endY] = chessboard.emptyBoard[endX][endY];

        int kingX;
        int kingY = endY;
        if(color == 1) {
            chessboard.whiteKingX = endX-sign;
            kingX = chessboard.whiteKingX;
        } else {
            chessboard.blackKingX = endX-sign;
            kingX = chessboard.blackKingX;
        }

        if (chessboard.isCheck(((color + 1) % 2), kingX, kingY)) result = true;

        chessboard.board[endX][endY] = chessboard.board[endX-sign][endY];
        chessboard.board[endX-sign][endY] = chessboard.emptyBoard[endX-sign][endY];
        if(color==1) chessboard.whiteKingX = endX;
        else chessboard.blackKingX = endX;

        return result;
    }

    private void makeMoveOnDisplay() {
        Icon iconMoved = display.squares[startX][startY].getIcon();
        display.squares[endX][endY].setIcon(iconMoved);
        display.squares[startX][startY].setIcon(null);
    }

    private void updateBoardInSpecialCaseOfEnPassant() {
        if (chessboard.board[endX][endY].getState() == Square.AFTER_EN_PASSANT) {
            chessboard.board[endX][startY] = chessboard.emptyBoard[endX][startY];
            display.squares[endX][startY].setIcon(null);
            chessboard.board[endX][endY].setState(Square.NORMAL_STATE);
        }
    }

    private void updateBoardInSpecialCaseOfCastle() {
        if (chessboard.board[endX][endY].getState() == Square.SHORT_CASTLE_STATE) {
            specialCaseOfShortCastle();
        }
        if (chessboard.board[endX][endY].getState() == Square.LONG_CASTLE_STATE) {
            specialCaseOfLongCastle();
        }
    }

    private void specialCaseOfShortCastle() {
        chessboard.board[endX-1][endY] = chessboard.board[endX+1][endY];
        Icon iconMoved = display.squares[endX+1][endY].getIcon();
        display.squares[endX-1][endY].setIcon(iconMoved);

        chessboard.board[endX+1][endY] = chessboard.emptyBoard[endX+1][endY];
        display.squares[endX+1][endY].setIcon(null);

        chessboard.board[endX][endY].setState(Square.NORMAL_STATE);
        chessboard.board[endX-1][endY].setState(Square.NORMAL_STATE);
    }

    private void specialCaseOfLongCastle() {
        chessboard.board[endX+1][endY] = chessboard.board[endX-2][endY];
        Icon iconMoved = display.squares[endX-2][endY].getIcon();
        display.squares[endX+1][endY].setIcon(iconMoved);

        chessboard.board[endX-2][endY] = chessboard.emptyBoard[endX-2][endY];
        display.squares[endX-2][endY].setIcon(null);

        chessboard.board[endX][endY].setState(Square.NORMAL_STATE);
        chessboard.board[endX+1][endY].setState(Square.NORMAL_STATE);
    }

    private void resetPawnsState() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (chessboard.board[i][j].getState() == Square.AFTER_2SQUARE_PAWN_MOVE2) {
                    chessboard.board[i][j].setState(Square.NORMAL_STATE);
                }
                if (chessboard.board[i][j].getState() == Square.AFTER_2SQUARE_PAWN_MOVE1) {
                    chessboard.board[i][j].setState(Square.AFTER_2SQUARE_PAWN_MOVE2);
                }
            }
    }

    private void promotePawn() {
        Integer kolor = chessboard.board[endX][endY].getColor();
        int ey = (kolor == WHITES_MOVE) ? 7 : 0;

        if (chessboard.board[endX][endY].getPiece() == ChessPiece.Pawn && endY == ey) {
            duringPromotion = true;
            while (duringPromotion) {
                display.pawnPromotionMenu.show(display.squares[endX][endY], 0, 0);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException exc) {
                    System.out.println(exc);
                }
            }
            duringPromotion = false;
        }
    }

    private void putTextInCorner() {
        String str = "";
        if (color == WHITES_MOVE) str = "<html>White's<br>move";
        else str = "<html>Black's<br>move";

        int defendersKingX = (color == BLACKS_MOVE) ? chessboard.blackKingX : chessboard.whiteKingX;
        int defendersKingY = (color == BLACKS_MOVE) ? chessboard.blackKingY : chessboard.whiteKingY;

        if (chessboard.isCheck((color + 1) % 2, defendersKingX, defendersKingY)) {
            str += "<br>Check!";
            check = true;
            if (chessboard.isMate((color + 1) % 2, defendersKingX, defendersKingY)) {
                str = "<html>Mate!";
                mate = true;
            }
        } else {
            check = false;
            if (chessboard.isMate((color + 1) % 2, defendersKingX, defendersKingY)) {
                str = "<html>Stalemate!";
                mate = true;
            }
        }
        str += "</html>";
        display.corner.setText(str);
    }
}
