package net.ssehub.ai_perf.eval;

import java.io.Closeable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;

public abstract class AbstractEvaluator implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(AbstractEvaluator.class.getName());
    
    private int numEvaluations;
    
    public MeasureResult measure(List<ParameterValue<?>> values) throws EvaluationException {
        LOGGER.log(Level.FINE, "Evaluating {0}", values);
        numEvaluations++;
        
        MeasureResult result = measureImpl(values);
        LOGGER.log(Level.FINE, "Result: {0}", result);
        
        return result;
    }
    
    protected abstract MeasureResult measureImpl(List<ParameterValue<?>> values) throws EvaluationException;
 
    public void logStats() {
        LOGGER.log(Level.INFO, "Evaluated {0} configurations", numEvaluations);
    }
    
}
