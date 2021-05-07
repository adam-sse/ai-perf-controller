package net.ssehub.ai_perf.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import net.ssehub.ai_perf.json_util.NoExtraFieldsChecker;
import net.ssehub.ai_perf.json_util.RequiredFieldChecker;

public class NetworkConnection implements Closeable {
    
    public static final int PORT = 50021;
    
    private static final Logger LOGGER = Logger.getLogger(NetworkConnection.class.getName());
    
    private Socket socket;
    
    private Gson gson;
    
    private Writer out;
    
    private JsonReader in;
    
    @SuppressWarnings("resource")
    public NetworkConnection(String host) throws IOException {
        LOGGER.info("Connecting to worker " + host + ":" + PORT);
        this.socket = new Socket(host, PORT);
        
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new RequiredFieldChecker());
        builder.registerTypeAdapterFactory(new NoExtraFieldsChecker());
        builder.registerTypeAdapter(MeasureTask.class, new MeasureTaskSerializer());
        this.gson = builder.create();
        
        this.out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        Reader in = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        if (LOGGER.isLoggable(Level.FINEST)) {
            this.out = new DebugWriter(this.out, LOGGER, Level.FINEST);
            in = new DebugReader(in, LOGGER, Level.FINEST);
        }
        this.in = new JsonReader(in);
        
        LOGGER.info("Connected");
    }
    
    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }
    
    @Override
    public void close() throws IOException {
        this.socket.close();
    }
    
    public MeasureResult sendTask(MeasureTask task) throws IOException {
        this.out.write(gson.toJson(task) + "\n");
        this.out.flush();
        
        MeasureResult result;
        try {
            result = gson.fromJson(in, MeasureResult.class);
            if (result == null) {
                throw new IOException("end of stream");
            }
            
        } catch (JsonParseException | IllegalArgumentException e) {
            throw new IOException(e);
        }
        
        return result;
    }
    
}
