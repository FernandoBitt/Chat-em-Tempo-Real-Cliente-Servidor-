// Servidor.java
import java.net.*;
import java.io.*;
import java.util.*;

public class Servidor {
    private static final int PORTA = 12345;
    private static Set<PrintWriter> clientes = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciado na porta " + PORTA);
        
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Novo cliente conectado: " + clienteSocket);
                
                PrintWriter out = new PrintWriter(clienteSocket.getOutputStream(), true);
                clientes.add(out);
                
                new Thread(new ManipuladorCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ManipuladorCliente implements Runnable {
        private Socket clienteSocket;
        private BufferedReader in;

        public ManipuladorCliente(Socket socket) {
            this.clienteSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                String mensagem;
                
                while ((mensagem = in.readLine()) != null) {
                    System.out.println("Mensagem recebida: " + mensagem);
                    broadcast(mensagem);
                }
            } catch (IOException e) {
                System.out.println("Cliente desconectado: " + clienteSocket);
            } finally {
                try {
                    clienteSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void broadcast(String mensagem) {
        for (PrintWriter cliente : clientes) {
            cliente.println(mensagem);
        }
    }
}