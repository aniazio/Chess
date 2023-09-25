package szachy;

public class Krol extends Pole {

    //Król musi też przechowywać stany, ze względu na roszadę
    //stan=6 to krótka roszada
    //stan=8 to długa roszada

    Krol(int kol){
        super(kol,'K');
    }

    boolean ruch(int sx, int sy, int kx, int ky, Pole[][] plansza) {

        if(kx<0 || kx>7 || ky<0 || ky>7) return false;
        int poziom=kx-sx;
        int pion=ky-sy;

        if((plansza[kx][ky].getFigura()=='P' || (plansza[kx][ky].getFigura()!='P' && plansza[kx][ky].getKolor()!=getKolor()))
                && (poziom<=1) && (poziom>=(-1)) && (pion<=1) && (pion>=(-1)) && !((pion==0) && (poziom==0))) {
            setStan(2);
            return true;
        }

        //roszada
        if(poziom==2 && pion==0 && plansza[sx][sy].getStan()==0
                && plansza[kx+1][ky].getStan()==0 && plansza[kx+1][ky].getFigura()=='W'
                && plansza[sx+1][sy].getFigura()=='P' && plansza[sx+2][sy].getFigura()=='P') {
            setStan(6); return true;
        }
        if(poziom==-2 && pion==0 && plansza[sx][sy].getStan()==0
                && plansza[kx-2][ky].getStan()==0 && plansza[kx-2][ky].getFigura()=='W'
                && plansza[sx-1][sy].getFigura()=='P' && plansza[sx-2][sy].getFigura()=='P' && plansza[sx-3][sy].getFigura()=='P') {
            setStan(8); return true;
        }

        return false;
    }


    boolean czyatak(int sx, int sy, int kingx, int kingy, Pole[][] plansza) {

        int pion=kingx-sx;
        int poziom=kingy-sy;

        if((poziom<=1) && (poziom>=(-1)) && (pion<=1) && (pion>=(-1)) && !((pion==0) && (poziom==0))) return true;
        else return false;

    }

}
