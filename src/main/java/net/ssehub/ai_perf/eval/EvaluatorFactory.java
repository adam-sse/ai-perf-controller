package net.ssehub.ai_perf.eval;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.net.NetworkConnection;

public class EvaluatorFactory {

    private static final Logger LOGGER = Logger.getLogger(EvaluatorFactory.class.getName());
    
    private String type;
    
    private NetworkConnection connection;
    
    private File resultLogFile;
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setConnection(NetworkConnection connection) {
        this.connection = connection;
    }
    
    public void setResultLogFile(File resultLogFile) {
        this.resultLogFile = resultLogFile;
    }
    
    public boolean needsNetwork() throws IllegalStateException {
        if (type == null) {
            throw new IllegalStateException("No evaluator type specified");
        }
        return type.equals("network");
    }
    
    public AbstractEvaluator create() throws IllegalStateException, IllegalArgumentException {
        if (type == null) {
            throw new IllegalStateException("No evaluator type specified");
        }
        
        AbstractEvaluator result;
        switch (type) {
        case "network":
            if (connection == null) {
                throw new IllegalStateException("No network connection supplied");
            }
            LOGGER.log(Level.CONFIG, "Using NetworkEvaluator with worker " + connection.getIp());
            result = new NetworkEvaluator(connection);
            break;
            
        case "dummy":
            LOGGER.log(Level.CONFIG, "Using DummyEvaluator");
            result = new DummyEvaluator();
            break;
        
        default:
            throw new IllegalArgumentException("Invalid evaluator type: " + type);
        }
        
        if (resultLogFile != null) {
            try {
                result.setResultLogFile(resultLogFile);
                LOGGER.log(Level.CONFIG, "Logging evaluator results to {0}", resultLogFile);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to create result log file for evaluator", e);
            }
        }
        
        return result;
    }
    
}
