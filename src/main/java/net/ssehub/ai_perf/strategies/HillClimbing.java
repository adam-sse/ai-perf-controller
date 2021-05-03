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

class HillClimbing implements IStrategy {

    private static final Logger LOGGER = Logger.getLogger(HillClimbing.class.getName());
    
    private List<Parameter<?>> parameters;
    
    private AbstractEvaluator evaluator;
    
    private MeasureResult currentTime;
    
    private List<ParameterValue<?>> currentValues;
    
    public HillClimbing(List<Parameter<?>> parameters, AbstractEvaluator evaluator) {
        this.parameters = parameters;
        this.evaluator = evaluator;
    }
    
    public List<ParameterValue<?>> run() {
        LOGGER.info("Starting hill climbing with " + parameters.size() + " parameters");
        
        try {
            init();
            climb();
        } catch (EvaluationException e) {
            LOGGER.log(Level.SEVERE, "Failure during evaluation", e);
        }
        
        return currentValues;
    }
    
    private void init() throws EvaluationException {
        currentValues = new ArrayList<>(parameters.size());
        for (Parameter<?> param : parameters) {
            currentValues.add(param.getDefaultValue());
        }
        
        currentTime = evaluator.measure(currentValues);
        
        LOGGER.log(Level.INFO, "Baseline: {0} with {1}",
                new Object[] { currentValues, currentTime });
    }
    
    private void climb() throws EvaluationException {
        boolean foundBetter = true;
        while (foundBetter) {
            
            List<List<ParameterValue<?>>> neighbors = createNeighbors(currentValues);
            
            LOGGER.log(Level.FINER, "Neighbors: {0}", neighbors);
            
            MeasureResult bestTime = currentTime;
            int bestIndex = -1;
            for (int i = 0; i < neighbors.size(); i++) {
                MeasureResult time = evaluator.measure(neighbors.get(i));
                
                if (time.isBetterThan(bestTime)) {
                    bestIndex = i;
                    bestTime = time;
                }
            }
            
            if (bestIndex != -1) {
                LOGGER.log(Level.INFO, "Found better neighbor: {0} ({1} -> {2})",
                        new Object[] { neighbors.get(bestIndex), currentTime, bestTime });
                foundBetter = true;
                currentValues = neighbors.get(bestIndex);
                currentTime = bestTime;
            } else {
                foundBetter = false;
            }
        }
        
        LOGGER.info("Found best solution: " + currentValues + " with " + currentTime);
    }
    
    private List<List<ParameterValue<?>>> createNeighbors(List<ParameterValue<?>> currentValues) {
        List<List<ParameterValue<?>>> result = new LinkedList<>();
        
        for (int i = 0; i < currentValues.size(); i++) {
            ParameterValue<?> value = currentValues.get(i);
            
            int ii = i;
            value.getNeighbor(Direction.HIGHER).ifPresent((neighborValue) -> {
                result.add(copyAndInsert(currentValues, ii, neighborValue));
            });
            value.getNeighbor(Direction.LOWER).ifPresent((neighborValue) -> {
                result.add(copyAndInsert(currentValues, ii, neighborValue));
            });
        }
        
        return result;
    }
    
    private List<ParameterValue<?>> copyAndInsert(List<ParameterValue<?>> values, int overrideIndex, ParameterValue<?> overrideValue) {
        List<ParameterValue<?>> result = new ArrayList<>(values.size());
        for (int i = 0; i < overrideIndex; i++) {
            result.add(values.get(i));
        }
        result.add(overrideValue);
        for (int i = overrideIndex + 1; i < values.size(); i++) {
            result.add(values.get(i));
        }
        return result;
    }
    
}
