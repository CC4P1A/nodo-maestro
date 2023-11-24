package nodomaestro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author walterrg
 */
public class Cuentas {
    
    private File file = new File("cuentas.txt");
    
    public void crearCuentas(int nroCuentas) {
        try {
            if (file.createNewFile()) {
                try (FileWriter fileWriter = new FileWriter(file)) {
                    int idAccount;
                    int money;
                    Random random = new Random();

                    for (int i = 0; i < nroCuentas; i++) {
                        idAccount = i + 1;
                        money = random.nextInt(1000, 100000);

                        String registro = String.valueOf(idAccount) + " " + String.valueOf(money);
                        if (i != nroCuentas - 1) {
                            registro += "\n";
                        }

                        fileWriter.write(registro);
                    }
                }
                System.out.println("Cuentas creadas.");
            } else {
                System.out.println("Archivo 'cuentas.txt' ya existe.");
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error durante la creación de cuentas.txt");
        }
    }
    
    public String getStringCuentas() {
        StringBuilder cuentasTodas = new StringBuilder();
        cuentasTodas.append("C-");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                int idCuenta = scanner.nextInt();
                int saldo = scanner.nextInt();
                cuentasTodas.append(idCuenta);
                cuentasTodas.append("-");
                cuentasTodas.append(saldo);
                if(scanner.hasNextLine()) {
                    cuentasTodas.append(";");
                }  
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error durante validación de transacción");
        }
        return cuentasTodas.toString();
    }

    
    
}
