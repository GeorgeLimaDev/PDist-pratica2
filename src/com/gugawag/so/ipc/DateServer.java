package com.gugawag.so.ipc;

import java.net.*;
import java.io.*;
import java.util.Date;

public class DateServer {
    public static void main(String[] args) {
        try {
            ServerSocket sock = new ServerSocket(6013);
            System.out.println("=== Servidor de George Lima iniciado ===\n");
            
            // Escutando por conexões
            while (true) {
                Socket client = sock.accept();
                System.out.println("Servidor recebeu comunicação do ip: " + client.getInetAddress() + "-" + client.getPort());
                
                // Cria uma nova thread para tratar a conexão
                new ClientHandler(client).start();
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
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
			
            PrintWriter pout = new PrintWriter(clientSocket.getOutputStream(), true);
            pout.println(new Date().toString() + "-Boa noite alunos!");

            InputStream in = clientSocket.getInputStream();
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));

            String line = bin.readLine();
            System.out.println("O cliente me disse: " + line);
			try {
				// Pausa a execução por 3 segundos (3000 milissegundos)
				Thread.sleep(30000); // 3000 ms = 3 segundos
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Tchau, cliente!"); // Testamos enviar uma nova mensagem enquanto o código que chegaria aqui está em sleep.
            clientSocket.close();
        } catch (IOException ioe) {
            System.err.println("Erro ao comunicar com o cliente: " + ioe);
        }
    }
}
