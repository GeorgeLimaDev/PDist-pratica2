package com.gugawag.so.ipc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DateClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 6013);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("=== Cliente conectado ao servidor ===\n");

            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                System.out.println("Servidor: " + serverResponse);
                if (serverResponse.equalsIgnoreCase("Encerrando conexão...")) {
                    break;
                }

                System.out.print("Comando: ");
                String userInput = console.readLine();
                out.println(userInput);

                if (userInput.trim().equalsIgnoreCase("quit")) {
                    break;
                }
            }

            socket.close();
            System.out.println("Cliente desconectado.");
        } catch (IOException e) {
            System.err.println("Erro no cliente: " + e);
        }
    }
}
