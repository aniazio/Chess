package szachy;

abstract public class Pole {


    private int kolor;
    private char figura;
    private int stan;

    Pole(int kol){
        figura = 'P';
        kolor = kol;
        stan = 2;              //pole puste ma zawsze stan 2
    }

    Pole(int kol, char fig){ //kolor=0 to czarny, 1 to biały
        figura = fig;
        kolor = kol;
        stan = 0;            //figura w pozycji początkowej ma stan 0
    }


    int getKolor() {return kolor;}

    char getFigura() {return figura;}

    int getStan() {return stan;}
    void setStan(int s) {stan=s;}

    abstract boolean ruch(int sx, int sy, int kx, int ky, Pole[][] plansza);
    //będzie pisał, czy ruch jest poprawny

    abstract boolean czyatak(int sx, int sy, int kingx, int kingy, Pole[][] plansza);
    //będzie pisał, czy dana figura atakuje obecnie króla


}
