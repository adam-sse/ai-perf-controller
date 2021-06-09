package net.ssehub.ai_perf.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.eval.AbstractEvaluator;
import net.ssehub.ai_perf.eval.EvaluationException;
import net.ssehub.ai_perf.model.Parameter;
import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;

public class IntervalEvaluation implements IStrategy {
    
    private static final Logger LOGGER = Logger.getLogger(IntervalEvaluation.class.getName());
    
    private static final int INTERVAL_MS;
    
    private static final int NUM_MEASERS;
    
    static {
        INTERVAL_MS = Integer.parseInt(System.getProperty("evaluation.sleepInterval", 5 * 60 * 1000 + ""));
        NUM_MEASERS = Integer.parseInt(System.getProperty("evaluation.numMeasures", 24 * 12 + ""));
    }

    private List<Parameter<?>> parameters;
    
    private AbstractEvaluator evaluator;
    
    public IntervalEvaluation(List<Parameter<?>> parameters, AbstractEvaluator evaluator) {
        this.parameters = parameters;
        this.evaluator = evaluator;
    }
    
    @Override
    public List<ParameterValue<?>> run() {
        LOGGER.log(Level.INFO, "Repeatedly running the same evaluation with default values for {0} parameters", parameters.size());
        LOGGER.log(Level.INFO, "Running {0} measures at a sleep interval of {1} ms", new Object[] { NUM_MEASERS, INTERVAL_MS });
        
        List<ParameterValue<?>> values = new ArrayList<>(parameters.size());
        for (Parameter<?> param : parameters) {
            values.add(param.getDefaultValue());
        }

        LOGGER.log(Level.INFO, "Using parameter values {0}", values);
        
        for (int i = 0; i < NUM_MEASERS; i++) {
            try {
                MeasureResult result = evaluator.measure(values);
                LOGGER.log(Level.INFO, "Result {0} of {1} : {2}", new Object[] { i + 1, NUM_MEASERS, result });
                
                LOGGER.log(Level.FINER, "Sleeping for {0}", INTERVAL_MS);
                Thread.sleep(INTERVAL_MS);
                
            } catch (EvaluationException e) {
                LOGGER.log(Level.WARNING, "Failed to evaluate", e);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Failed to wait", e);
            }
        }
        
        return values;
    }
    
}
