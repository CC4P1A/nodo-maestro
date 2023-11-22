package nodomaestro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    public static void main(String[] args) {
        int nroCuentas = 10;
        String ipNodoRedireccion = "127.0.0.1";
        int puertoNodoRedireccion = 8189;
        
        Cuentas cuentas = new Cuentas();
        cuentas.crearCuentas(nroCuentas);
        String cuentasTodas = cuentas.getStringCuentas();
        
        /*
        try {
            Socket socket = new Socket(ipNodoRedireccion,puertoNodoRedireccion);
            
            try {
                OutputStream secuenciaDeSalida = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(secuenciaDeSalida, true);
                
                // Envía todas las cuentas al nodo redirección con formato:
                // C-id1-saldo1;id2-saldo2;....;idn-saldoN
                pw.println(cuentasTodas);
                
                InputStream secuenciaDeEntrada = socket.getInputStream();
                Scanner in = new Scanner(secuenciaDeEntrada);
                
                NodoMaestro nodoMaestro = new NodoMaestro();
                
                while(in.hasNextLine()) {
                    String line = in.nextLine();
                    
                    String[] datos = line.split("-");
                    
                    // Procesa solicitud de transacción
                    if(datos[0].equals("A")) {
                        int idSolicitud = Integer.parseInt(datos[1]);
                        int idCuentaOrigen = Integer.parseInt(datos[2]);
                        int idCuentaDestino = Integer.parseInt(datos[3]);
                        double montoTransferencia = Double.parseDouble(datos[4]);
                        
                        boolean transaccionExitosa = 
                                nodoMaestro.validarTransaccion(idCuentaOrigen, idCuentaDestino, montoTransferencia);
                        
                        if(transaccionExitosa) {
                            String mensajeTransaccionExitosa = nodoMaestro.ejecutarTransaccion(idCuentaOrigen, idCuentaDestino, montoTransferencia);
                            pw.println(mensajeTransaccionExitosa);
                        } else {
                            String mensajeTransaccionFallida = "F" + line.substring(1);
                            pw.println(mensajeTransaccionFallida);
                        }
                    }
                }
                

            } finally{
                socket.close();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    */
    }
}
