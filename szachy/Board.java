package szachy;

public class Board {


    Pole[][] planszaPusta = new Pole[8][8];     //plansza przechowująca puste pola (na wypadek zbicia)
    Pole[][] plansza = new Pole[8][8];          //plansza przechowująca figury (na pustych odwołuje się do planszaPusta)
    int king0x, king0y, king1x, king1y;         //lokalizacja króla
    private int wczesniejszyStan;               //zmienna pomocnicza do cofania ruchów


    //inicjalizacja - wpisuje na plansze odpowiednie figury

    void inicjalizacja() {

        for(int i=0; i<8;i++){
            for(int j=0; j<8; j++) {
                planszaPusta[i][j] = new Puste(2);
                plansza[i][j] = planszaPusta[i][j];
            }
        }

        plansza[1][0] = new Skoczek(1);
        plansza[6][0] = new Skoczek(1);
        plansza[1][7] = new Skoczek(0);
        plansza[6][7] = new Skoczek(0);

        plansza[2][0] = new Goniec(1);
        plansza[5][0] = new Goniec(1);
        plansza[2][7] = new Goniec(0);
        plansza[5][7] = new Goniec(0);

        plansza[0][0] = new Wieza(1);
        plansza[7][0] = new Wieza(1);
        plansza[0][7] = new Wieza(0);
        plansza[7][7] = new Wieza(0);

        plansza[3][0] = new Hetman(1);
        plansza[3][7] = new Hetman(0);
        plansza[4][0] = new Krol(1);
        plansza[4][7] = new Krol(0);

        king1x=4; king1y=0;
        king0x=4; king0y=7;

        for(int i=0; i<8;i++) {
            plansza[i][1] = new Pion(1);
            plansza[i][6] = new Pion(0);
        }


    }


    boolean czySzach(int kolatakujacy, int kingx, int kingy){
        //trzeba sprawdzić wszystkie figury, nie tylko tę, która się ruszyła, bo mogła odsłonić atak
        // king to król broniący się

        for(int i=0;i<8;i++) {
            for(int j=0; j<8; j++) {
                if(plansza[i][j].getKolor()==kolatakujacy && plansza[i][j].czyatak(i, j, kingx, kingy, plansza)) {
                    return true;}
            }
        }
        return false;
    }


    boolean czyMat(int kolatakujacy, int kingx, int kingy){
        //trzeba sprawdzić, czy któraś figura może zasłonić, nie tylko, czy może uciec król
        // king to król broniący się
        boolean mat=true;
        Pole p;


        //straszna złożoność, ale trzeba (chyba) zbadać wszystkie możliwe ruchy broniącego i dla nich, czy zachodzi nadal szach
        for(int i=0;i<8;i++) {              //(i,j) to współrzędne figury broniącego
            for(int j=0;j<8;j++) {

                if(plansza[i][j].getKolor()==((kolatakujacy+1)%2)) {
                    for(int k=0;k<8;k++) {              //(k,l) to współrzędne pola, na które potencjalnie może się przesunąć
                        for(int l=0;l<8;l++){

                            wczesniejszyStan = plansza[i][j].getStan();
                            if((k != i || l != j) && plansza[i][j].ruch(i,j,k,l,plansza)) {   //sprawdzamy, czy może wykonać ruch z (i,j) do (k,l)
                                p = plansza[k][l];
                                plansza[k][l] = plansza[i][j];
                                plansza[i][j] = planszaPusta[i][j];


                                if (plansza[k][l].getFigura() == 'K') {         //jeśli ruszamy się królem to aktualizujemy kingx, kingy
                                    int kx=kingx;
                                    int ky=kingy;
                                    kingx=k; kingy=l;
                                    if (!czySzach(kolatakujacy, kingx, kingy)) mat = false;
                                    kingx = kx;
                                    kingy = ky;
                                } else if (!czySzach(kolatakujacy, kingx, kingy)) mat = false;

                                plansza[i][j] = plansza[k][l];              //cofamy wykonanie ruchu
                                plansza[k][l] = p;
                                plansza[i][j].setStan(wczesniejszyStan);
                                }

                        }
                    }

                }

            }
        }

        return mat;
    }

}

