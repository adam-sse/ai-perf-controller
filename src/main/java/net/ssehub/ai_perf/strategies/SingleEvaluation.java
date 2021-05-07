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

public class SingleEvaluation implements IStrategy {
    
    private static final Logger LOGGER = Logger.getLogger(SingleEvaluation.class.getName());

    private List<Parameter<?>> parameters;
    
    private AbstractEvaluator evaluator;
    
    public SingleEvaluation(List<Parameter<?>> parameters, AbstractEvaluator evaluator) {
        this.parameters = parameters;
        this.evaluator = evaluator;
    }
    
    @Override
    public List<ParameterValue<?>> run() {
        LOGGER.log(Level.INFO, "Running a single evaluation with default values for {0} parameters", parameters.size());
        
        List<ParameterValue<?>> values = new ArrayList<>(parameters.size());
        for (Parameter<?> param : parameters) {
            values.add(param.getDefaultValue());
        }

        try {
            MeasureResult result = evaluator.measure(values);
            LOGGER.log(Level.INFO, "Result for {0}: {1}", new Object[] { values, result });
        } catch (EvaluationException e) {
            LOGGER.log(Level.WARNING, "Failed to evaluate", e);
        }
        
        return values;
    }
    
}
