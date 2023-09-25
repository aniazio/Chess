package szachy;

public class Puste extends Pole {

    Puste(int kol){
        super(kol);
    }

    boolean ruch(int sx, int sy, int kx, int ky, Pole[][] plansza){
        return false;
    }

    boolean czyatak(int sx, int sy, int kingx, int kingy, Pole[][] plansza){
        return false;
    }

}
