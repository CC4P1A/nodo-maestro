package nodomaestro;

/**
 *
 * @author walterrg
 */
public class Prueba {
    public static void main(String[] args) {
        NodoMaestro nm = new NodoMaestro();
        String s = nm.calcularHash("adsadsadsada");
        System.out.println(s);
    }
}
