package nodomaestro;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author walterrg
 */
public class Cuentas {
    static String ipNodoRedireccion = "127.0.0.1";
    static int puertoNodoRedireccion = 8189;
    
    public static void main(String[] args) {
        NodoMaestro nodoMaestro = new NodoMaestro();
        nodoMaestro.crearCuentas();
        try {
            try (
                 Socket socket = new Socket(ipNodoRedireccion, puertoNodoRedireccion);
                 OutputStream outputStream = socket.getOutputStream();
                 PrintWriter printWriter = new PrintWriter(outputStream, true)
                ) {
                
                printWriter.print(nodoMaestro.getCuentas());
                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
