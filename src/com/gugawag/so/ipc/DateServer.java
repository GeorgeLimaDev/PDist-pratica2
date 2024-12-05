package com.gugawag.so.ipc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class DateServer {
    public static void main(String[] args) {
        try {
            ServerSocket sock = new ServerSocket(6013);
            System.out.println("=== Servidor iniciado ===\n");

            while (true) {
                Socket client = sock.accept();
                System.out.println("Conexão recebida de " + client.getInetAddress() + ":" + client.getPort());
                
                // Cria uma nova thread para tratar a conexão
                new ClientHandler(client).start();
            }
        } catch (IOException ioe) {
            System.err.println("Erro no servidor: " + ioe);
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Bem-vindo! Digite um comando: readdir, rename, create, remove, ou quit");

            String command;
            while ((command = in.readLine()) != null) {
                System.out.println("Comando recebido: " + command);
                String response = processCommand(command);
                out.println(response);

                if (command.trim().equalsIgnoreCase("quit")) {
                    System.out.println("Cliente desconectado.");
                    break;
                }
            }

            clientSocket.close();
        } catch (IOException ioe) {
            System.err.println("Erro ao comunicar com o cliente: " + ioe);
        }
    }

    private String processCommand(String command) {
        try {
            String[] parts = command.split(" ", 2);
            String cmd = parts[0].toLowerCase();

            switch (cmd) {
                case "readdir":
                    return Files.list(Paths.get("."))
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .collect(Collectors.joining("\n"));

                case "rename":
                    if (parts.length < 2 || !parts[1].contains(" ")) {
                        return "Erro: uso correto - rename <arquivo_antigo> <arquivo_novo>";
                    }
                    String[] names = parts[1].split(" ", 2);
                    Files.move(Paths.get(names[0]), Paths.get(names[1]));
                    return "Arquivo renomeado com sucesso.";

                case "create":
                    if (parts.length < 2) {
                        return "Erro: uso correto - create <nome_arquivo>";
                    }
                    Files.createFile(Paths.get(parts[1]));
                    return "Arquivo criado com sucesso.";

                case "remove":
                    if (parts.length < 2) {
                        return "Erro: uso correto - remove <nome_arquivo>";
                    }
                    Files.delete(Paths.get(parts[1]));
                    return "Arquivo removido com sucesso.";

                case "quit":
                    return "Encerrando conexão...";

                default:
                    return "Comando inválido.";
            }
        } catch (IOException e) {
            return "Erro ao processar comando: " + e.getMessage();
        }
    }
}
