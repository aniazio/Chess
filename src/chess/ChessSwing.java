/*
Pictures of figures from:
https://commons.wikimedia.org/wiki/Category:PNG_chess_pieces/Standard_transparent
 */

package chess;

import javax.swing.*;
import java.awt.*;

public class ChessSwing {

    JButton[][] squares;
    JLabel corner;
    MyPopupMenu pawnPromotionMenu;

    private BoardAccess chessboard;
    private ChessboardListener chessboardListener;

    ChessSwing(){
        chessboard = new BoardAccess();
        chessboardListener = new ChessboardListener(chessboard, this);

        JFrame jfrm = new JFrame("Chess");
        squares = new JButton[8][8];
        setUpJFrameOf(jfrm);
        setUpPopupMenu();
        addLabelsAndButtonsTo(jfrm);
    }

    private void setUpJFrameOf(JFrame jfrm) {
        jfrm.setLayout(new GridLayout(9,9));
        jfrm.setSize(640,640);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setVisible(true);
        jfrm.setLocationRelativeTo(null);
    }

    private void setUpPopupMenu() {
        pawnPromotionMenu = new MyPopupMenu("Pawn's promotion");
        var queen = new JMenuItem("Queen");
        var rook = new JMenuItem("Rook");
        var bishop = new JMenuItem("Bishop");
        var knight = new JMenuItem("Knight");

        pawnPromotionMenu.add(queen);
        pawnPromotionMenu.add(rook);
        pawnPromotionMenu.add(bishop);
        pawnPromotionMenu.add(knight);

        queen.addActionListener(chessboardListener::promotionReaction);
        rook.addActionListener(chessboardListener::promotionReaction);
        bishop.addActionListener(chessboardListener::promotionReaction);
        knight.addActionListener(chessboardListener::promotionReaction);
    }


    private void addLabelsAndButtonsTo(JFrame jfrm) {
        addCornerTo(jfrm);
        addColsMarkingsTo(jfrm);
        addRowsMarkingsAndButtonsTo(jfrm);
    }

    private void addCornerTo(JFrame jfrm) {
        corner = new JLabel("<html>White's<br>move</html>");
        corner.setHorizontalAlignment(JLabel.CENTER);
        jfrm.add(corner);
    }

    private void addColsMarkingsTo(JFrame jfrm) {
        JLabel markingsOfCols[] = new JLabel[8];
        JPanel jpanelsForColsMarkings[] = new JPanel[8];

        for(int i=0; i<8; i++) {
            Character c = (char) ('A' + i);
            addMarking(c.toString(), markingsOfCols[i], jpanelsForColsMarkings[i], jfrm, JLabel.CENTER, BorderLayout.PAGE_END);
        }
    }

    private void addRowsMarkingsAndButtonsTo(JFrame jfrm) {
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

    private void addMarking(String str, JLabel label, JPanel panel, JFrame jfrm, int placeOnLabel, String placeOnBorderLO) {
        label = new JLabel(str, placeOnLabel);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(label, placeOnBorderLO);
        jfrm.add(panel);
    }

    private void setUpButton(JFrame jfrm, int i, int j) {
        Character c = (char) ('a' + i);
        String squareName = c.toString() + j;         //cords of a square e.g. a1
        squares[i][j-1] = new JButton();
        setButtonDisplay(i, j);
        squares[i][j-1].setActionCommand(squareName);
        squares[i][j-1].addActionListener(chessboardListener::basicAction);
        if(j==8 || j==1) pawnPromotionMenu.setInvoker(squares[i][j-1]);
        jfrm.add(squares[i][j-1]);
    }

    private void setButtonDisplay(int i, int j) {
        ChessPiece fig = chessboard.board[i][j-1].getPiece();
        int color = chessboard.board[i][j-1].getColor();

        if(fig!=ChessPiece.Empty) squares[i][j-1].setIcon(
                new ImageIcon("files/" + fig.toString() + color + ".png"));

        if((i+j)%2 == 0) squares[i][j-1].setBackground(Color.white);
        else squares[i][j-1].setBackground(Color.gray);
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