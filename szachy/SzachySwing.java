/*
Obrazki figur ze strony:
https://commons.wikimedia.org/wiki/Category:PNG_chess_pieces/Standard_transparent
 */


package szachy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SzachySwing implements ActionListener {

    private JButton[][] przyciski;          //pola szachownicy na wyświetlaczu
    private JLabel rog;                     //informacja w rogu wyświetlacza
    private MyPopupMenu promo;              //menu do promocji piona

    private Board b = new Board();          //backend

    private int statusRuchu=0;      //status ruchu jest równy 1, gdy gracz przycisnął pierwszy raz przycisk, żeby wykonać ruch
                                    //status ruchu jest równy 0, gdy gracz przycisnął przycisk drugi raz i ruch może być zrealizowany
    private int wczesniejszyStan;                       //zmienna pomocnicza na wypadek, gdyby trzeba było cofnąć ruch
    private int kol = 1;                                //przechowuje informację, czyj jest ruch. Jeśli 1, to ruch białych
    private Integer startx, starty, koniecx, koniecy;   //zmienne do zapisywania koordynatów wykonywnego ruchu

    private boolean mat = false;
    private boolean wTrakciePromocji=false;


    SzachySwing(){                      //wyświetlanie szachownicy

        JFrame jfrm = new JFrame("Szachy");
        jfrm.setLayout(new GridLayout(9,9));
        jfrm.setSize(640,640);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setVisible(true);

        JLabel jlab[][] = new JLabel[2][8];     //na oznaczenia po bokach planszy
        JPanel jpan[][] = new JPanel[2][8];


        promo = new MyPopupMenu("promocja piona");       //menu do promocji piona
        var hetman = new JMenuItem("Hetman");
        var wieza = new JMenuItem("Wieża");
        var goniec = new JMenuItem("Goniec");
        var skoczek = new JMenuItem("Skoczek");


        promo.add(hetman);
        promo.add(wieza);
        promo.add(goniec);
        promo.add(skoczek);

        ActionListener reakcjapromo = (e) -> {String str = e.getActionCommand(); //reakcje na promocję trzeba dostosować DO ZROBIENIA

            Character naCoPromo;               //zmienna mówiąca, na jaką figurę promować danego piona
            Integer kolor = b.plansza[koniecx][koniecy].getKolor();

            naCoPromo = str.charAt(0);

            switch(naCoPromo) {
                case 'W':
                    b.plansza[koniecx][koniecy] = new Wieza(kolor);
                    break;
                case 'S':
                    b.plansza[koniecx][koniecy] = new Skoczek(kolor);
                    break;
                case 'G':
                    b.plansza[koniecx][koniecy] = new Goniec(kolor);
                    break;
                case 'H':
                    b.plansza[koniecx][koniecy] = new Hetman(kolor);
                    break;

            }
            przyciski[koniecx][koniecy].setIcon(new ImageIcon("pliki/" + naCoPromo.toString() + kolor.toString() + ".png"));

            promo.closePopup();

            wTrakciePromocji = false;
        };

        hetman.addActionListener(reakcjapromo);
        wieza.addActionListener(reakcjapromo);
        goniec.addActionListener(reakcjapromo);
        skoczek.addActionListener(reakcjapromo);

        przyciski = new JButton[8][8];
        Character c;                    //zmienne pomocnicze
        Integer l;

        b.inicjalizacja();

        rog = new JLabel("<html>Ruch<br>białych</html>");
        rog.setHorizontalAlignment(JLabel.CENTER);
        jfrm.add(rog);

        for(int i=0; i<8; i++) {
            c = (char) ('A' + i);

            jlab[0][i] = new JLabel(c.toString(), JLabel.CENTER);       //górne oznaczenia kolumn
            jlab[0][i].setFont(new Font("Arial", Font.PLAIN, 16));
            jpan[0][i] = new JPanel();
            jpan[0][i].setLayout(new BorderLayout());
            jpan[0][i].add(jlab[0][i], BorderLayout.PAGE_END);
            jfrm.add(jpan[0][i]);
        }


        for(int j=8; j>0; j--) {
            l = j;

            jlab[1][j-1] = new JLabel(l.toString(), JLabel.RIGHT);       //boczne oznaczenie wierszy
            jlab[1][j-1].setFont(new Font("Arial", Font.PLAIN, 16));
            jpan[1][j-1] = new JPanel();
            jpan[1][j-1].setLayout(new BorderLayout());
            jpan[1][j-1].add(jlab[1][j-1], BorderLayout.EAST);
            jfrm.add(jpan[1][j-1]);


            for(int i=0; i<8; i++) {
                c = (char) ('a' + i);
                String pole = c.toString() + j;         //współrzędne pola np. a1



                przyciski[i][j-1] = new JButton();


                Character fig = b.plansza[i][j-1].getFigura();          //ustawienia wyświetlania
                Integer kolo = b.plansza[i][j-1].getKolor();

                if(fig!='P') przyciski[i][j-1].setIcon(new ImageIcon("pliki/" + fig.toString() + kolo.toString() + ".png"));



                if((i+j)%2 == 0) przyciski[i][j-1].setBackground(Color.white);
                else przyciski[i][j-1].setBackground(Color.gray);

                przyciski[i][j-1].setActionCommand(pole);               //ustawienia przycisku
                przyciski[i][j-1].addActionListener(this);

                if(j==8 || j==1) promo.setInvoker(przyciski[i][j-1]);

                jfrm.add(przyciski[i][j-1]);
            }
        }



    }


    @Override
    public void actionPerformed(ActionEvent e) {

        new Thread(new Runnable() {                         //w czasie actionPerformed nie można aktualizować JFrame'a
            @Override                                       //ze względu na długi czas trwania promocji piona trzeba utworzyć przy ruchu nowy wątek
            public void run() {
                if(!mat && !wTrakciePromocji) {
                    statusRuchu = (statusRuchu + 1) % 2;        //status ruchu jest równy 1, gdy gracz przycisnął pierwszy raz przycisk, żeby wykonać ruch
                    //status ruchu jest równy 0, gdy gracz przycisnął przycisk drugi raz i ruch może być zrealizowany

                    String pole = e.getActionCommand();

                    int polex = pole.charAt(0) - 97;
                    int poley = pole.charAt(1) - 49;

                    if (statusRuchu == 1) {

                        //chcemy zacząć ruch tylko wtedy, kiedy jest on sensowny, tzn. kiedy zaczyna od naszej figury
                        if(b.plansza[polex][poley].getKolor() == (kol%2) && b.plansza[polex][poley].getFigura()!='P') {

                            startx = polex;
                            starty = poley;
                            przyciski[startx][starty].setBackground(Color.PINK);            //podświetla pole, od którego zaczynamy ruch
                        } else statusRuchu = (statusRuchu + 1) % 2; //cofamy aktualizację statusu ruchu

                    } else {
                        koniecx = polex;
                        koniecy = poley;
                        if ((startx + starty) % 2 == 1) przyciski[startx][starty].setBackground(Color.white); //cofa podświetlenie pola początkowego
                        else przyciski[startx][starty].setBackground(Color.gray);


                        wczesniejszyStan = b.plansza[startx][starty].getStan();
                        if (b.plansza[startx][starty].ruch(startx, starty, koniecx, koniecy, b.plansza)) { //jeśli ruch jest zgodny z ruchami danej figury
                            Pole p = b.plansza[koniecx][koniecy];                           //zapamiętanie pola, na wypadek nieprawidłowego ruchu
                            b.plansza[koniecx][koniecy] = b.plansza[startx][starty];        //przesunięcie figury na planszy
                            b.plansza[startx][starty] = b.planszaPusta[startx][starty];

                            boolean dobryruch = true;               //zmienna mówiąca, czy mamy zmieniać wyświetlanie


                            if (kol == 1) {                                         //aktualizacja zmiennych dotyczących lokalizacji królów
                                if (startx == b.king1x && starty == b.king1y) {
                                    b.king1x = koniecx;
                                    b.king1y = koniecy;
                                }
                            }
                            else {
                                if (startx == b.king0x && starty == b.king0y) {
                                    b.king0x = koniecx;
                                    b.king0y = koniecy;
                                }
                            }

                            int kingx = (kol == 1) ? b.king1x : b.king0x;
                            int kingy = (kol == 1) ? b.king1y : b.king0y;


                            if (b.czySzach(((kol + 1) % 2), kingx, kingy)) {                      //sprawdzamy, czy ruchem gracz nie wystawia się na szach
                                dobryruch = false;
                            }



                            if(b.plansza[koniecx][koniecy].getStan()==6 ||
                                    b.plansza[koniecx][koniecy].getStan()==8) {              //sprawdzamy poprawność roszady:

                                //czy król nie jest aktualnie szachowany
                                if (b.czySzach(((kol + 1) % 2), kingx, kingy)) {
                                    dobryruch = false;
                                }
                                
                                //czy król nie przechodzi przez szach
                                int znak = (b.plansza[koniecx][koniecy].getStan()==6) ? 1 : -1;

                                b.plansza[koniecx-znak][koniecy] = b.plansza[koniecx][koniecy];
                                b.plansza[koniecx][koniecy] = b.planszaPusta[koniecx][koniecy];
                                if(kol==1) {
                                    b.king1x=koniecx-znak;
                                    kingx=b.king1x;
                                } else {
                                    b.king0x=koniecx-znak;
                                    kingx=b.king0x;
                                }

                                if (b.czySzach(((kol + 1) % 2), kingx, kingy)) {
                                    dobryruch = false;
                                }



                                b.plansza[koniecx][koniecy] = b.plansza[koniecx-znak][koniecy];             //cofamy króla z powrotem na miejsce
                                b.plansza[koniecx-znak][koniecy] = b.planszaPusta[koniecx-znak][koniecy];
                                if(kol==1) {
                                    b.king1x = koniecx;
                                } else {
                                    b.king0x = koniecx;
                                }
                            }




                            if(!dobryruch) {
                                b.plansza[startx][starty] = b.plansza[koniecx][koniecy];            //jeśli błędny ruch, to cofamy figurę na start
                                b.plansza[koniecx][koniecy] = p;
                                b.plansza[startx][starty].setStan(wczesniejszyStan);


                                if (kol == 1) {                                                     //cofamy aktualizację lokalizacji królów
                                    if (koniecx == b.king1x && koniecy == b.king1y) {
                                        b.king1x = startx;
                                        b.king1y = starty;
                                    }
                                } else {
                                    if (koniecx == b.king0x && koniecy == b.king0y) {
                                        b.king0x = startx;
                                        b.king0y = starty;
                                    }
                                }

                            }



                            if (dobryruch) {


                                przyciski[koniecx][koniecy].setIcon(przyciski[startx][starty].getIcon()); //przesunięcie figury na wyświetlaczu
                                przyciski[startx][starty].setIcon(null);

                                if (b.plansza[koniecx][koniecy].getStan() == 4) {                           //bicie w przelocie
                                    b.plansza[koniecx][starty] = b.planszaPusta[koniecx][starty];
                                    b.plansza[koniecx][koniecy].setStan(2);

                                    przyciski[koniecx][starty].setIcon(null);

                                }

                                if (b.plansza[koniecx][koniecy].getStan() == 6) {                   //krótka roszada
                                    b.plansza[koniecx-1][koniecy] = b.plansza[koniecx+1][koniecy];
                                    b.plansza[koniecx+1][koniecy] = b.planszaPusta[koniecx+1][koniecy];
                                    b.plansza[koniecx][koniecy].setStan(2);
                                    b.plansza[koniecx-1][koniecy].setStan(2);
                                    przyciski[koniecx-1][koniecy].setIcon(przyciski[koniecx+1][koniecy].getIcon());
                                    przyciski[koniecx+1][koniecy].setIcon(null);
                                }

                                if (b.plansza[koniecx][koniecy].getStan() == 8) {                   //długa roszada
                                    b.plansza[koniecx+1][koniecy] = b.plansza[koniecx-2][koniecy];
                                    b.plansza[koniecx-2][koniecy] = b.planszaPusta[koniecx-2][koniecy];
                                    b.plansza[koniecx][koniecy].setStan(2);
                                    b.plansza[koniecx+1][koniecy].setStan(2);
                                    przyciski[koniecx+1][koniecy].setIcon(przyciski[koniecx-2][koniecy].getIcon());
                                    przyciski[koniecx-2][koniecy].setIcon(null);
                                }


                                for (int i = 0; i < 8; i++)
                                    for (int j = 0; j < 8; j++) {                    //po ruchu trzeba zresetować piony
                                        if (b.plansza[i][j].getStan() == 1) {
                                            b.plansza[i][j].setStan(2);
                                        }
                                        if (b.plansza[i][j].getStan() == 3) {
                                            b.plansza[i][j].setStan(1);
                                        }
                                    }


                                Integer kolor = b.plansza[koniecx][koniecy].getKolor();             //promocja piona
                                int ky = (kolor==1) ? 7 : 0;

                                if(b.plansza[koniecx][koniecy].getFigura()=='X' && koniecy==ky) {
                                    wTrakciePromocji = true;

                                    while(wTrakciePromocji) {
                                        promo.show(przyciski[koniecx][koniecy], 0, 0);
                                        try{
                                            Thread.sleep(100);
                                        } catch(InterruptedException exc) {
                                            System.out.println(exc);
                                        }
                                    }
                                    wTrakciePromocji = false;
                                }


                                String str = "";
                                kol = (kol + 1) % 2;                                                //zmiana gracza, który ma wykonywać następny ruch
                                if (kol == 1) str = "<html>Ruch<br>białych";
                                else str = "<html>Ruch<br>czarnych";


                                kingx = (kol == 0) ? b.king0x : b.king1x;                           //król broniący się
                                kingy = (kol == 0) ? b.king0y : b.king1y;

                                if (b.czySzach((kol + 1) % 2, kingx, kingy)) {
                                    str += "<br>Szach!";
                                    if (b.czyMat((kol + 1) % 2, kingx, kingy)) {
                                        str = "<html>Mat!"; mat=true;
                                    }
                                } else {
                                    if (b.czyMat((kol + 1) % 2, kingx, kingy)) {
                                        str = "<html>Pat!"; mat=true;
                                    }
                                }



                                str += "</html>";
                                rog.setText(str);



                            }


                        }

                    }
                }


            }
        }).start();



    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SzachySwing();
            }
        });
    }


}
