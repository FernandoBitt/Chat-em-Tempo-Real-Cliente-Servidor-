package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class ClienteGUI extends Application {
    private TextArea areaMensagens;
    private TextField campoMensagem;
    private PrintWriter out;
    private Socket socket;
    private BufferedReader in;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat JavaFX");

        areaMensagens = new TextArea();
        areaMensagens.setEditable(false);
        
        campoMensagem = new TextField();
        campoMensagem.setPromptText("Digite sua mensagem...");

        Button botaoEnviar = new Button("Enviar");
        botaoEnviar.setOnAction(e -> enviarMensagem());
        campoMensagem.setOnAction(e -> enviarMensagem());

        VBox layout = new VBox(10, areaMensagens, campoMensagem, botaoEnviar);
        Scene cena = new Scene(layout, 400, 300);
        primaryStage.setScene(cena);
        primaryStage.setOnCloseRequest(e -> fecharConexao());
        primaryStage.show();

        conectarAoServidor();
    }

    private void conectarAoServidor() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = in.readLine()) != null) {
                        String finalMensagem = mensagem;
                        Platform.runLater(() -> 
                            areaMensagens.appendText(finalMensagem + "\n")
                        );
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> 
                        areaMensagens.appendText("Conexão com o servidor perdida.\n")
                    );
                } finally {
                    fecharConexao();
                }
            }).start();

        } catch (IOException e) {
            areaMensagens.appendText("Falha ao conectar ao servidor: " + e.getMessage() + "\n");
        }
    }

    private void enviarMensagem() {
        try {
            String mensagem = campoMensagem.getText();
            if (!mensagem.isEmpty() && out != null) {
                out.println(mensagem);
                campoMensagem.clear();
            }
        } catch (Exception e) {
            areaMensagens.appendText("Erro ao enviar mensagem: " + e.getMessage() + "\n");
        }
    }

    private void fecharConexao() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}