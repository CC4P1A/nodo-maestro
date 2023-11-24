package nodomaestro;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        int nroCuentas = 5;
        String ipNodoRedireccion = "127.0.0.1";
        int puertoNodoRedireccion = 40000;

        try {
            ServerSocket serverSocket = new ServerSocket(puertoNodoRedireccion);
            Socket socket = serverSocket.accept();

            Cuentas cuentas = new Cuentas();
            cuentas.crearCuentas(nroCuentas);
            String cuentasTodas = cuentas.getStringCuentas();

            try {
                OutputStream secuenciaDeSalida = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(secuenciaDeSalida, true);

                // Envía todas las cuentas al nodo redirección con formato:
                // C-id1-saldo1;id2-saldo2;....;idn-saldoN
                pw.println(cuentasTodas);

                InputStream secuenciaDeEntrada = socket.getInputStream();
                Scanner in = new Scanner(secuenciaDeEntrada);

                NodoMaestro nodoMaestro = new NodoMaestro();

                while (in.hasNextLine()) {
                    String line = in.nextLine();

                    String[] datos = line.split("-");

                    // Procesa solicitud de transacción
                    if (datos[0].equals("A")) {
                        int idSolicitud = Integer.parseInt(datos[1]);
                        int idCuentaOrigen = Integer.parseInt(datos[2]);
                        int idCuentaDestino = Integer.parseInt(datos[3]);
                        int montoTransferencia = Integer.parseInt(datos[4]);

                        boolean transaccionExitosa
                                = nodoMaestro.validarTransaccion(idCuentaOrigen, idCuentaDestino, montoTransferencia);

                        if (transaccionExitosa) {
                            String mensajeTransaccionExitosa = nodoMaestro.ejecutarTransaccion(idSolicitud, idCuentaOrigen, idCuentaDestino, montoTransferencia);
                            pw.println(mensajeTransaccionExitosa);

                            if (nodoMaestro.isMinar()) {
                                pw.println("M-" + nodoMaestro.getNroCeros() + "-" + nodoMaestro.getHashBLoqueAnterior() + "-" + nodoMaestro.getHashRaiz());
                                nodoMaestro.setMinar(false);
                            }

                        } else {
                            String mensajeTransaccionFallida = "F-" +idSolicitud + "-No se realizo la transaccion";
                            pw.println(mensajeTransaccionFallida);
                        }
                    } else if (datos[0].equals("V")) {
                        int nonce = Integer.parseInt(datos[1]);
                        double tiempo = Double.parseDouble(datos[2]);
                        int nroCeros = Integer.parseInt(datos[3]);
                        String hashTotal = datos[6];
                        double porcentaje = Double.parseDouble(datos[7]);

                        System.out.println("\n*****************************************");
                        System.out.println("Datos recibidos:");
                        System.out.println("Nonce: " + nonce);
                        System.out.println("Tiempo en hallar el nonce: " + tiempo);
                        System.out.println("Número de ceros: " + nroCeros);
                        System.out.println("Hash del bloque: " + hashTotal);
                        System.out.println("Porcentaje de nodos de acuerdo: " + porcentaje * 100 + "%");
                        System.out.println("*****************************************");

                        if (porcentaje > 0.5) {
                            String[] bloque = nodoMaestro.actualizarBloque(hashTotal);

                            StringBuilder mensajeBloque = new StringBuilder();

                            // Forma mensaje a enviar con la información del bloque
                            for (int i = 0; i < bloque.length - 1; i++) {
                                mensajeBloque.append("B-" + bloque[i] + ";");
                            }
                            mensajeBloque.append("B-" + bloque[bloque.length - 1]);

                            pw.println(mensajeBloque.toString());
                        }

                    }
                }

            } finally {
                socket.close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
