package net.ssehub.ai_perf.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class MockPeer {

    private IOException exception;
    
    private boolean running;
    
    private StringBuffer read;
    
    private String toSend;
    
    public MockPeer(String toSend) {
        this.running = true;
        this.toSend = toSend;
        this.read = new StringBuffer();
        new Thread(this::run).start();
    }
    
    public synchronized void waitUntilFinished() {
        while (running) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
    }
    
    public IOException getException() {
        return exception;
    }
    
    public String getRead() {
        return read.toString();
    }
    
    private void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(NetworkConnection.PORT);
            
            Socket socket = serverSocket.accept();
            
            serverSocket.close();
            
            Thread readThread = new Thread(() -> {
                try {
                    Reader in = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
                    
                    char[] buf = new char[255];
                    int read;
                    while ((read = in.read(buf)) >= 0) {
                        MockPeer.this.read.append(buf, 0, read);
                    }

                } catch (SocketException e) {
                    if (!e.getMessage().equals("Socket closed")) {
                        synchronized (MockPeer.this) {
                            MockPeer.this.exception = e;
                        }
                    }
                } catch (IOException e) {
                    synchronized (MockPeer.this) {
                        MockPeer.this.exception = e;
                    }
                }
            });
            readThread.start();
            
            
            boolean sent = false;
            while (!sent) {
                if (read.length() > 0) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                    
                    Writer out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                    out.write(toSend);
                    out.flush();
                    
                    sent = true;
                }
                
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
            
            socket.close();
            
        } catch (IOException e) {
            synchronized (MockPeer.this) {
                MockPeer.this.exception = e;
            }
        }
        
        synchronized (MockPeer.this) {
            MockPeer.this.running = false;
            MockPeer.this.notifyAll();
        }
    }
    
    public static void main(String[] args) {
        MockPeer peer = new MockPeer(
                "{\"time\": 123}\n"
                + "{\"time\": 89}\n"
                + "{\"time\": 93}\n"
            );
        peer.waitUntilFinished();
    }
    
}
