package szachy;

public class Pion extends Pole {

    /*
    stan = 0, gdy pion stoi w pozycji początkowej
    stan = 1 lub 3, gdy wykonał ruch o dwa przed chwilą
    stan = 4 po biciu w przelocie, bo trzeba dodatkowo usunąć po tym ruchu zbitą figurę
    stan = 2 w p.p.
     */

    Pion(int kol) {
        super(kol,'X');
    }


    boolean ruch(int sx, int sy, int kx, int ky, Pole[][] plansza) {

        if(kx<0 || kx>7 || ky<0 || ky>7) return false;
        int poziom=kx-sx;
        int pion=ky-sy;
        int k = (getKolor()==1)? 1 : -1;


        //zwykły ruch
        if(poziom==0 && pion==(1*k) && plansza[kx][ky].getFigura()=='P') {setStan(2); return true;}

        //ruch o dwa, gdy pion wychodzi ze stanu początkowego
        if(poziom==0 && pion==(2*k) && plansza[kx][ky].getFigura()=='P'
                && plansza[kx][ky-(1*k)].getFigura()=='P' && getStan()==0) {setStan(3); return true;}

        //zwykłe zbicie
        if((poziom==1 || poziom==-1) && pion==(1*k) && plansza[kx][ky].getFigura()!='P') {setStan(2); return true;}

        //bicie w przelocie
        if((poziom==1 || poziom==-1) && pion==(1*k) && plansza[kx][ky].getFigura()=='P' &&
                plansza[kx][sy].getFigura()=='X' && (plansza[kx][sy].getStan()%2)==1) {setStan(4); return true;}

        return false;
    }


    boolean czyatak(int sx, int sy, int kingx, int kingy, Pole[][] plansza) {

        int poziom=kingx-sx;
        int pion=kingy-sy;
        int poziomabs=(poziom>0)? poziom : (-1)*poziom;
        int k = (getKolor()==1)? 1 : -1;

        if(pion==(1*k) && poziomabs==1) return true;
        return false;

    }

}
