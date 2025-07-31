// Cliente.java
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Cliente {
    private static final String ENDERECO = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(ENDERECO, PORTA);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {
            
            System.out.println("Conectado ao servidor. Digite suas mensagens:");

            // Thread para receber mensagens do servidor
            new Thread(() -> {
                try {
                    String mensagemServidor;
                    while ((mensagemServidor = in.readLine()) != null) {
                        System.out.println("Servidor: " + mensagemServidor);
                    }
                } catch (IOException e) {
                    System.out.println("Conexão com o servidor encerrada.");
                }
            }).start();

            // Envia mensagens digitadas pelo usuário
            while (true) {
                String mensagem = scanner.nextLine();
                out.println(mensagem);
            }
        } catch (IOException e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
    }
}