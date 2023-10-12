/*
Pictures of figures from:
https://commons.wikimedia.org/wiki/Category:PNG_chess_pieces/Standard_transparent
 */

package chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessSwing implements ActionListener {

    private JButton[][] squares;                        //display variables
    private JLabel corner;
    private MyPopupMenu pawnPromotionMenu;

    private final int FINISH_MOVE = 1;                  //state of a move
    private final int MOVE_FINISHED = 0;
    private final int BLACKS_MOVE = 0;                  //color, which should move now
    private final int WHITES_MOVE = 1;

    private BoardAccess chessboard = new BoardAccess();
    private int moveStatus = MOVE_FINISHED;
    private int color = WHITES_MOVE;

    private Integer startX, startY, endX, endY;         //auxiliary variables
    private Square movedFigure, endSquare;

    private boolean mate = false;                       //boolean variables
    private boolean duringPromotion = false;
    private boolean check = false;

    //DISPLAY CONFIGURATION//////////////////////////////

    ChessSwing(){
        JFrame jfrm = new JFrame("Chess");
        squares = new JButton[8][8];
        chessboard.initialize();
        setUpJFrameOf(jfrm);
        setUpPopupMenu();
        addLabelsAndButtonsTo(jfrm);
    }

    public void setUpJFrameOf(JFrame jfrm) {
        jfrm.setLayout(new GridLayout(9,9));
        jfrm.setSize(640,640);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setVisible(true);
    }

    public void setUpPopupMenu() {
        pawnPromotionMenu = new MyPopupMenu("Pawn's promotion");
        var queen = new JMenuItem("Queen");
        var rook = new JMenuItem("Rook");
        var bishop = new JMenuItem("Bishop");
        var knight = new JMenuItem("Knight");

        pawnPromotionMenu.add(queen);
        pawnPromotionMenu.add(rook);
        pawnPromotionMenu.add(bishop);
        pawnPromotionMenu.add(knight);

        ActionListener reakcjapromo = (e) -> {
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
            squares[endX][endY].setIcon(
                    new ImageIcon("files/" + str + kolor + ".png"));
            pawnPromotionMenu.closePopup();
            duringPromotion = false;
        };

        queen.addActionListener(reakcjapromo);
        rook.addActionListener(reakcjapromo);
        bishop.addActionListener(reakcjapromo);
        knight.addActionListener(reakcjapromo);
    }


    public void addLabelsAndButtonsTo(JFrame jfrm) {
        addCornerTo(jfrm);
        addColsMarkingsTo(jfrm);
        addRowsMarkingsAndButtonsTo(jfrm);
    }

    public void addCornerTo(JFrame jfrm) {
        corner = new JLabel("<html>White's<br>move</html>");
        corner.setHorizontalAlignment(JLabel.CENTER);
        jfrm.add(corner);
    }

    public void addColsMarkingsTo(JFrame jfrm) {
        JLabel markingsOfCols[] = new JLabel[8];
        JPanel jpanelsForColsMarkings[] = new JPanel[8];

        for(int i=0; i<8; i++) {
            Character c = (char) ('A' + i);
            addMarking(c.toString(), markingsOfCols[i], jpanelsForColsMarkings[i], jfrm, JLabel.CENTER, BorderLayout.PAGE_END);
        }
    }

    public void addRowsMarkingsAndButtonsTo(JFrame jfrm) {
        JLabel markingsOfRows[] = new JLabel[8];
        JPanel jpanelsForRowsMarkings[] = new JPanel[8];

        for(int j=8; j>0; j--) {
            Integer l = j;
            addMarking(l.toString(), markingsOfRows[j-1], jpanelsForRowsMarkings[j-1], jfrm, JLabel.RIGHT, BorderLayout.EAST);
            for(int i=0; i<8; i++) {
                setUpButton(jfrm, i, j);
            }
        }
    }

    public void addMarking(String str, JLabel label, JPanel panel, JFrame jfrm, int placeOnLabel, String placeOnBorderLO) {
        label = new JLabel(str, placeOnLabel);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(label, placeOnBorderLO);
        jfrm.add(panel);
    }

    public void setUpButton(JFrame jfrm, int i, int j) {
        Character c = (char) ('a' + i);
        String squareName = c.toString() + j;         //cords of a square e.g. a1
        squares[i][j-1] = new JButton();
        setButtonDisplay(i, j);
        squares[i][j-1].setActionCommand(squareName);
        squares[i][j-1].addActionListener(this);
        if(j==8 || j==1) pawnPromotionMenu.setInvoker(squares[i][j-1]);
        jfrm.add(squares[i][j-1]);
    }

    public void setButtonDisplay(int i, int j) {
        ChessPiece fig = chessboard.board[i][j-1].getPiece();
        int color = chessboard.board[i][j-1].getColor();

        if(fig!=ChessPiece.Empty) squares[i][j-1].setIcon(
                new ImageIcon("files/" + fig.toString() + color + ".png"));

        if((i+j)%2 == 0) squares[i][j-1].setBackground(Color.white);
        else squares[i][j-1].setBackground(Color.gray);
    }

    //ACTION PERFORMED//////////////////////////////

    @Override
    public void actionPerformed(ActionEvent e) {

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

    public void readMove(String squareName) {
        if (moveStatus == MOVE_FINISHED) readFistPartOfMove(squareName);
        else readSecondPartOfMove(squareName);
    }

    public void readFistPartOfMove(String squareName) {
        int squareX = squareName.charAt(0) - 97;
        int squareY = squareName.charAt(1) - 49;
        if(chessboard.board[squareX][squareY].getColor() == (color % 2) &&
                chessboard.board[squareX][squareY].getPiece() != ChessPiece.Empty) {
            startX = squareX;
            startY = squareY;
            squares[startX][startY].setBackground(Color.PINK);
        } else moveStatus = (moveStatus + 1) % 2;
    }

    public void readSecondPartOfMove(String squareName) {
        endX = squareName.charAt(0) - 97;
        endY = squareName.charAt(1) - 49;
        if ((startX+startY)%2 == 1) squares[startX][startY].setBackground(Color.white);
        else squares[startX][startY].setBackground(Color.gray);
    }

    public void makeMove() {
        chessboard.board[endX][endY] = movedFigure;
        chessboard.board[startX][startY] = chessboard.emptyBoard[startX][startY];
    }

    public void actualizeKingCords(Square startingSquare, int endingSquareX, int endingSquareY) {
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

    public void undoMove() {
        chessboard.board[startX][startY] = chessboard.board[endX][endY];
        chessboard.board[endX][endY] = endSquare;
        chessboard.board[startX][startY].setState(movedFigure.getState());
        if(movedFigure.getPiece() == ChessPiece.King) actualizeKingCords(chessboard.board[endX][endY], startX, startY);
    }

    public boolean isKingComingThroughCheck() {
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

    public void makeMoveOnDisplay() {
        squares[endX][endY].setIcon(squares[startX][startY].getIcon());
        squares[startX][startY].setIcon(null);
    }

    public void updateBoardInSpecialCaseOfEnPassant() {
        if (chessboard.board[endX][endY].getState() == Square.AFTER_EN_PASSANT) {
            chessboard.board[endX][startY] = chessboard.emptyBoard[endX][startY];
            squares[endX][startY].setIcon(null);
            chessboard.board[endX][endY].setState(Square.NORMAL_STATE);
        }
    }

    public void updateBoardInSpecialCaseOfCastle() {
        if (chessboard.board[endX][endY].getState() == Square.SHORT_CASTLE_STATE) {
            specialCaseOfShortCastle();
        }
        if (chessboard.board[endX][endY].getState() == Square.LONG_CASTLE_STATE) {
            specialCaseOfLongCastle();
        }
    }

    public void specialCaseOfShortCastle() {
        chessboard.board[endX-1][endY] = chessboard.board[endX+1][endY];
        squares[endX-1][endY].setIcon(squares[endX+1][endY].getIcon());
        chessboard.board[endX+1][endY] = chessboard.emptyBoard[endX+1][endY];
        squares[endX+1][endY].setIcon(null);
        chessboard.board[endX][endY].setState(Square.NORMAL_STATE);
        chessboard.board[endX-1][endY].setState(Square.NORMAL_STATE);
    }

    public void specialCaseOfLongCastle() {
        chessboard.board[endX+1][endY] = chessboard.board[endX-2][endY];
        squares[endX+1][endY].setIcon(squares[endX-2][endY].getIcon());
        chessboard.board[endX-2][endY] = chessboard.emptyBoard[endX-2][endY];
        squares[endX-2][endY].setIcon(null);
        chessboard.board[endX][endY].setState(Square.NORMAL_STATE);
        chessboard.board[endX+1][endY].setState(Square.NORMAL_STATE);
    }

    public void resetPawnsState() {
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

    public void promotePawn() {
        Integer kolor = chessboard.board[endX][endY].getColor();
        int ey = (kolor == WHITES_MOVE) ? 7 : 0;

        if (chessboard.board[endX][endY].getPiece() == ChessPiece.Pawn && endY == ey) {
            duringPromotion = true;
            while (duringPromotion) {
                pawnPromotionMenu.show(squares[endX][endY], 0, 0);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException exc) {
                    System.out.println(exc);
                }
            }
            duringPromotion = false;
        }
    }

    public void putTextInCorner() {
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
        corner.setText(str);
    }


    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChessSwing();
            }
        });
    }
}