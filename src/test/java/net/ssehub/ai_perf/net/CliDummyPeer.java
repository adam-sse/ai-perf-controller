package net.ssehub.ai_perf.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CliDummyPeer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(NetworkConnection.PORT);
        
        System.out.println("Waiting for connection on port " + NetworkConnection.PORT);
        Socket socket = serverSocket.accept();
        System.out.println("Got connection from " + socket.getInetAddress().getHostName());
        serverSocket.close();
        
        new Thread(() -> {
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("I: " + line);
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }).start();
        
        try (Writer out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader cli = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = cli.readLine()) != null) {
                out.write(line + "\n");
                out.flush();
            }
        }
    }
    
}
