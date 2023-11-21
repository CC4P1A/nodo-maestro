package nodomaestro;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
    synchronized public boolean validarTransaccion(int cuentaOrigen, int cuentaDestino, int montoTransferencia) {
        boolean origenValido = false;
        boolean destinoValido = false;

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                int cuenta = scanner.nextInt();
                int saldo = scanner.nextInt();
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

}
