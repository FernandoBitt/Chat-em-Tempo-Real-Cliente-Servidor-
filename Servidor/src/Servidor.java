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
                System.out.println("Novo cliente conectado: " + clienteSocket.getInetAddress());
                
                PrintWriter out = new PrintWriter(clienteSocket.getOutputStream(), true);
                synchronized (clientes) {
                    clientes.add(out);
                }
                
                new Thread(new ManipuladorCliente(clienteSocket, out)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static class ManipuladorCliente implements Runnable {
        private Socket clienteSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ManipuladorCliente(Socket socket, PrintWriter writer) {
            this.clienteSocket = socket;
            this.out = writer;
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
                System.out.println("Cliente desconectado: " + clienteSocket.getInetAddress());
            } finally {
                try {
                    synchronized (clientes) {
                        clientes.remove(out);
                    }
                    clienteSocket.close();
                } catch (IOException e) {
                    System.err.println("Erro ao fechar socket: " + e.getMessage());
                }
            }
        }
    }

    private static void broadcast(String mensagem) {
        synchronized (clientes) {
            for (PrintWriter cliente : clientes) {
                cliente.println(mensagem);
            }
        }
    }
}