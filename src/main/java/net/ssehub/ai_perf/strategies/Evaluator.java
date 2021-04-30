package net.ssehub.ai_perf.strategies;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;
import net.ssehub.ai_perf.net.MeasureTask;
import net.ssehub.ai_perf.net.NetworkConnection;

public class Evaluator {

    private static final Logger LOGGER = Logger.getLogger(Evaluator.class.getName());
    
    private NetworkConnection connection;
    
    private int numEvaluations;
    
    public Evaluator(NetworkConnection connection) {
        this.connection = connection;
    }
    
    public MeasureResult measure(List<ParameterValue<?>> values) throws EvaluationException {
        LOGGER.log(Level.FINE, "Evaluating {0} on remote worker", values);
        numEvaluations++;
        
        MeasureTask task = new MeasureTask(values);
        MeasureResult result;
        try {
            result = connection.sendTask(task);
        } catch (IOException e) {
            throw new EvaluationException("Evaluation on remote worker failed", e);
        }
        
        LOGGER.log(Level.FINE, "Result: {0}", result);
        
        return result;
    }
    
    public void logStats() {
        LOGGER.log(Level.INFO, "Evaluated {0} configurations", numEvaluations);
    }
    
}
