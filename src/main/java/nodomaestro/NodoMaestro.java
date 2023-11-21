package nodomaestro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Walter Rivera
 */
public class NodoMaestro {

    private int nroCeros = 3;
    private int nroTransaccionesBloque = 128;
    private final int nroCuentas = 10;
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

    public int getNroCuentas() {
        return nroCuentas;
    }

    public String getCuentas() {
        StringBuilder cuentas = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                cuentas.append(linea);
                if(scanner.hasNextLine()) {
                    cuentas.append("\n");
                }  
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error durante validación de transacción");
        }
        return cuentas.toString();
    }

    public void crearCuentas() {
        try {
            if (file.createNewFile()) {
                try (FileWriter fileWriter = new FileWriter(file)) {
                    int idAccount;
                    int money;
                    Random random = new Random();

                    for (int i = 0; i < nroCuentas; i++) {
                        idAccount = i + 1;
                        money = random.nextInt(1000, 100000);

                        String fila = String.valueOf(idAccount) + " " + String.valueOf(money);
                        if (i != nroCuentas - 1) {
                            fila += "\n";
                        }

                        fileWriter.write(fila);
                    }
                }
                System.out.println("Cuentas creadas.");
            } else {
                System.out.println("Archivo ya existe.");
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error durante la creación de cuentas.txt");
        }
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
