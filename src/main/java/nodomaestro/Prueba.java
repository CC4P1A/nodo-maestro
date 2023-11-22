package nodomaestro;

/**
 *
 * @author walterrg
 */
public class Prueba {
    public static void main(String[] args) {
        // Validar Transacción
        NodoMaestro nodoMaestro = new NodoMaestro();
        String transferenciaValida = nodoMaestro.ejecutarTransaccion(5, 7, 10000);
        System.out.println(transferenciaValida);
    }
}
