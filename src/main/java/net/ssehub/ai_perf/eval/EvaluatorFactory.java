package net.ssehub.ai_perf.eval;

import net.ssehub.ai_perf.net.NetworkConnection;

public class EvaluatorFactory {

    private String type;
    
    private NetworkConnection connection;
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setConnection(NetworkConnection connection) {
        this.connection = connection;
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
            result = new NetworkEvaluator(connection);
            break;
            
        case "dummy":
            result = new DummyEvaluator();
            break;
        
        default:
            throw new IllegalArgumentException("Invalid evaluator type: " + type);
        }
        
        return result;
    }
    
}
