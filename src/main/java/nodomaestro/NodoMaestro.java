package nodomaestro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Walter Rivera
 */
public class NodoMaestro {

    private final int nroCeros = 3;
    private final int nroTransaccionesBloque = 4;
    private int nroTransaccionesRealizadas = 0;
    private File file = new File("cuentas.txt");
    private String[] transacciones = new String[nroTransaccionesBloque];
    private String hashBLoqueAnterior = "0".repeat(64);
    private String hashRaiz = "";
    private boolean minar = false;
    private LinkedList<String[]> bloques = new LinkedList<>();

    public int getNroCeros() {
        return nroCeros;
    }

    public int getNroTransaccionesBloque() {
        return nroTransaccionesBloque;
    }

    public int getNroTransaccionesRealizadas() {
        return nroTransaccionesRealizadas;
    }

    public void setNroTransaccionesRealizadas(int nroTransaccionesRealizadas) {
        this.nroTransaccionesRealizadas = nroTransaccionesRealizadas;
    }

    public String getHashBLoqueAnterior() {
        return hashBLoqueAnterior;
    }

    public String getHashRaiz() {
        return hashRaiz;
    }

    public boolean isMinar() {
        return minar;
    }

    public void setMinar(boolean minar) {
        this.minar = minar;
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
    public boolean validarTransaccion(int cuentaOrigen, int cuentaDestino, int montoTransferencia) {
        boolean origenValido = false;
        boolean destinoValido = false;
        
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String [] arr = line.split(" ");
                int cuenta = Integer.parseInt(arr[0]);
                int saldo = Integer.parseInt(arr[1]);
                //int cuenta = scanner.nextInt();
                //int saldo = scanner.nextInt();
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

    public String ejecutarTransaccion(int idSolicitud, int cuentaOrigen, int cuentaDestino, int montoTransferencia) {
        transacciones[nroTransaccionesRealizadas] = cuentaOrigen + "-" + cuentaDestino + "-" + montoTransferencia;

        if (nroTransaccionesRealizadas < nroTransaccionesBloque - 1) {
            nroTransaccionesRealizadas++;
        } else {
            hashRaiz = calcularHashRaiz(transacciones);

            minar = true;

            bloques.add(Arrays.copyOf(transacciones, nroTransaccionesBloque));

            // Restear transacciones del bloque actual
            nroTransaccionesRealizadas = 0;
            transacciones = new String[nroTransaccionesBloque];
        }

        int nuevoSaldoCuentaOrigen = 0;
        int nuevoSaldoCuentaDestino = 0;

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
                int saldo = Integer.parseInt(datosLinea[1]);

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
        respuesta.append(idSolicitud).append("-");
        respuesta.append(cuentaOrigen).append("-").append(nuevoSaldoCuentaOrigen);
        respuesta.append("-").append(cuentaDestino).append("-").append(nuevoSaldoCuentaDestino);

        return respuesta.toString();
    }

    public String calcularHashRaiz(String[] arr) {
        if (arr.length == 1) {
            return calcularHash(arr[0]);
        }
        int indMed = arr.length / 2;
        String[] subArray1 = Arrays.copyOfRange(arr, 0, indMed);
        String[] subArray2 = Arrays.copyOfRange(arr, indMed, arr.length);

        return calcularHash(calcularHashRaiz(subArray1) + calcularHashRaiz(subArray2));

    }

    public String calcularHash(String palabra) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(palabra.getBytes());
            byte[] digest = md.digest();

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toHexString(0xFF & digest[i]));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException ex) {
            System.err.println("No soporta SHA-256");
        }
        return "";
    }

    public String[] actualizarBloque(String hash) {
        String[] ultimoBloque = bloques.pop();

        File fileBlockchain = new File("blockchaincuentas.txt");

        try (FileWriter fileWriter = new FileWriter(fileBlockchain, true)) {

            for (int i = 0; i < nroTransaccionesBloque; i++) {
                ultimoBloque[i] = ultimoBloque[i] + "-" + hash;
                fileWriter.write(ultimoBloque[i] + "\n");
            }
        } catch (IOException ex) {
            System.err.println("Error con archivo 'blockchaincuentas.txt'");
        }

        return ultimoBloque;
    }

}
