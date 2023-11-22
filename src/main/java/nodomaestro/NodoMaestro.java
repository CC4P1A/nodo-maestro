package nodomaestro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Walter Rivera
 */
public class NodoMaestro {

    private int nroCeros = 3;
    private int nroTransaccionesBloque = 128;
    private File file = new File("cuentas.txt");

    public int getNroCeros() {
        return nroCeros;
    }

    public void setNroCeros(int nroCeros) {
        this.nroCeros = nroCeros;
    }

    public int getNroTransaccionesBloque() {
        return nroTransaccionesBloque;
    }

    public void setNroTransaccionesBloque(int nroTransaccionesBloque) {
        this.nroTransaccionesBloque = nroTransaccionesBloque;
    }

    /**
     * Valida una transacción
     *
     * @param cuentaOrigen
     * @param cuentaDestino
     * @param montoTransferencia
     * @return true si la cuenta origen y destino existen y hay sufiente saldo en la cuenta origen para realizar la
     * transacción
     */
    public boolean validarTransaccion(int cuentaOrigen, int cuentaDestino, double montoTransferencia) {
        boolean origenValido = false;
        boolean destinoValido = false;

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                int cuenta = scanner.nextInt();
                double saldo = scanner.nextDouble();
                if (cuenta == cuentaDestino) {
                    destinoValido = true;
                }
                if (cuenta == cuentaOrigen && saldo >= montoTransferencia) {
                    origenValido = true;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error durante validación de transacción");
        }
        return origenValido && destinoValido;
    }

    public String ejecutarTransaccion(int cuentaOrigen, int cuentaDestino, double montoTransferencia) {

        double nuevoSaldoCuentaOrigen = 0;
        double nuevoSaldoCuentaDestino = 0;

        String lineaOrigen = "";
        String lineaDestino = "";

        StringBuilder cuentasStrB = new StringBuilder();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                cuentasStrB.append(linea).append("\n");

                String[] datosLinea = linea.split(" ");
                int cuenta = Integer.parseInt(datosLinea[0]);
                double saldo = Double.parseDouble(datosLinea[1]);

                if (cuenta == cuentaOrigen) {
                    nuevoSaldoCuentaOrigen = saldo - montoTransferencia;
                    lineaOrigen = linea;
                } else if (cuenta == cuentaDestino) {
                    nuevoSaldoCuentaDestino = saldo + montoTransferencia;
                    lineaDestino = linea;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error durante ejecución de transacción");
        }

        String cuentasStr = cuentasStrB.toString();
        cuentasStr = cuentasStr.replaceAll(lineaOrigen, cuentaOrigen + " " + nuevoSaldoCuentaOrigen);
        cuentasStr = cuentasStr.replaceAll(lineaDestino, cuentaDestino + " " + nuevoSaldoCuentaDestino);

        //System.out.println(cuentasStr);
        
        
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(cuentasStr);
        } catch (IOException ex) {
           System.err.println("No se pudo actualizar cuentas.txt");
        }
        

        // Arma respuesta a enviar
        StringBuilder respuesta = new StringBuilder("A-");
        respuesta.append(cuentaOrigen).append("-").append(nuevoSaldoCuentaOrigen);
        respuesta.append("-").append(cuentaDestino).append("-").append(nuevoSaldoCuentaDestino);

        return respuesta.toString();
    }

}
