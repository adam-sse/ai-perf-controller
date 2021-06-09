package net.ssehub.ai_perf.strategies;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.eval.AbstractEvaluator;
import net.ssehub.ai_perf.eval.EvaluationException;
import net.ssehub.ai_perf.model.Parameter;
import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.model.ParameterValue.Direction;
import net.ssehub.ai_perf.net.MeasureResult;

public class FullEvaluation implements IStrategy {

    private static final Logger LOGGER = Logger.getLogger(FullEvaluation.class.getName());
    
    private List<Parameter<?>> parameters;
    
    private AbstractEvaluator evaluator;
    
    private List<ParameterValue<?>> currentValues;
    
    private MeasureResult bestResult;
    
    private List<ParameterValue<?>> bestConfiguration;
    
    public FullEvaluation(List<Parameter<?>> parameters, AbstractEvaluator evaluator) {
        this.parameters = parameters;
        this.evaluator = evaluator;
    }
    
    @Override
    public List<ParameterValue<?>> run() {
        LOGGER.log(Level.INFO, "Starting full evaluation with {0} parameters", parameters.size());
        
        currentValues = new LinkedList<>();
        fullEvaluation(0);
        
        return bestConfiguration;
    }
    
    private void fullEvaluation(int index) {
        if (index >= parameters.size()) {
            
            try {
                MeasureResult result = evaluator.measure(currentValues);
                
                if (bestResult == null || result.isBetterThan(bestResult)) {
                    bestResult = result;
                    bestConfiguration = new ArrayList<>(currentValues);
                }
                
            } catch (EvaluationException e) {
                LOGGER.log(Level.WARNING, "Could not evaluate", e);
            }
            
        } else {
            Parameter<?> parameter = parameters.get(index);
            
            ParameterValue<?> defaultValue = parameter.getDefaultValue();
            currentValues.add(defaultValue);
            fullEvaluation(index + 1);
            currentValues.remove(currentValues.size() - 1);
            
            ParameterValue<?> value = defaultValue.getNeighbor(Direction.HIGHER).orElse(null);
            while (value != null) {
                currentValues.add(value);
                fullEvaluation(index + 1);
                currentValues.remove(currentValues.size() - 1);
                
                value = value.getNeighbor(Direction.HIGHER).orElse(null);
            }
            
            value = defaultValue.getNeighbor(Direction.LOWER).orElse(null);
            while (value != null) {
                currentValues.add(value);
                fullEvaluation(index + 1);
                currentValues.remove(currentValues.size() - 1);
                
                value = value.getNeighbor(Direction.LOWER).orElse(null);
            }
        }
    }

}
