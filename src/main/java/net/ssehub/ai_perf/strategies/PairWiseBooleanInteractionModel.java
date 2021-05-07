package net.ssehub.ai_perf.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.eval.AbstractEvaluator;
import net.ssehub.ai_perf.eval.EvaluationException;
import net.ssehub.ai_perf.model.BooleanParameter;
import net.ssehub.ai_perf.model.Parameter;
import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;

/**
 * Based on:
 * Siegmund, N., Kolesnikov, S. S., Kästner, C., Apel, S., Batory, D., Rosenmüller, M., & Saake, G. (2012, June).
 * Predicting performance via automated feature-interaction detection.
 * In 2012 34th International Conference on Software Engineering (ICSE) (pp. 167-177). IEEE.
 * 
 * Simplification: no implications between parameters ("features"), i.e. no feature model required, all combinations are valid
 */
class PairWiseBooleanInteractionModel implements IStrategy {
    
    private static final Logger LOGGER = Logger.getLogger(PairWiseBooleanInteractionModel.class.getName());

    private List<BooleanParameter> parameters;
    
    private AbstractEvaluator evaluator;
    
    private MeasureResult minBase;
    
    private Map<BooleanParameter, Long> individualEffect;
    
    private Map<Set<BooleanParameter>, Long> interactionEffects;
    
    public PairWiseBooleanInteractionModel(List<Parameter<?>> parameters, AbstractEvaluator evaluator)
            throws IllegalArgumentException {
        this.parameters = new ArrayList<>(parameters.size());
        for (Parameter<?> param : parameters) {
            if (!(param instanceof BooleanParameter)) {
                throw new IllegalArgumentException("Only BooleanParameters are supported by this strategy");
            }
            this.parameters.add((BooleanParameter) param);
        }
        
        this.evaluator = evaluator;
    }
    
    @Override
    public List<ParameterValue<?>> run() {
        individualEffect = new HashMap<>(parameters.size());
        interactionEffects = new HashMap<>(parameters.size());
        
        try {
            minBase = evaluator.measure(buildParameterValuesWithOneEnabled(-1));
            
            for (int i = 0; i < parameters.size(); i++) {
                MeasureResult measure = evaluator.measure(buildParameterValuesWithOneEnabled(i));
                
                long effectOfParam = measure.getTime() - minBase.getTime();
                
                individualEffect.put(parameters.get(i), effectOfParam);
            }
            
            LOGGER.log(Level.FINE, "Individual factors: {0}", individualEffect);
            
            Set<BooleanParameter> interactingParameters = new HashSet<>(parameters.size());
            MeasureResult maxBase = evaluator.measure(buildParameterValuesWithAllButOneEnabled(-1));
            for (int i = 0; i < parameters.size(); i++) {
                MeasureResult measure = evaluator.measure(buildParameterValuesWithAllButOneEnabled(i));
                
                long effectOfParam = measure.getTime() - maxBase.getTime();
                long stdev = Math.min(measure.getStdev(), maxBase.getStdev());
                
                if (Math.abs(effectOfParam - individualEffect.get(parameters.get(i))) > stdev) {
                    interactingParameters.add(parameters.get(i));
                }
            }
            LOGGER.log(Level.FINE, "Interacting parameters: {0}", interactingParameters);
            
            for (BooleanParameter p1 : interactingParameters) {
                for (BooleanParameter p2 : interactingParameters) {
                    int p1i = parameters.indexOf(p1);
                    int p2i = parameters.indexOf(p2);
                    if (p2i <= p1i) {
                        continue;
                    }

                    long prediction = minBase.getTime() + individualEffect.get(p1) + individualEffect.get(p2);
                    
                    List<ParameterValue<?>> configuration = buildParameterValuesWithTwoEnabled(p1i, p2i);
                    MeasureResult actual = evaluator.measure(configuration);
                    
                    long interactionEffect = prediction - actual.getTime();
                    if (Math.abs(interactionEffect) > actual.getStdev()) {
                        Set<BooleanParameter> set = new HashSet<>(2);
                        set.add(p1);
                        set.add(p2);
                        interactionEffects.put(set, interactionEffect);
                    }
                }
            }
            
            LOGGER.log(Level.FINE, "Interaction factors: {0}", interactionEffects);
            
        } catch (EvaluationException e) {
            LOGGER.log(Level.SEVERE, "Failed to evaluate", e);
        }
        
        
        LOGGER.log(Level.INFO, () -> "Performance model: " + buildFormula());
        
        List<ParameterValue<?>> bestResult = new ArrayList<>(parameters.size());
        for (BooleanParameter parameter : parameters) {
            bestResult.add(new ParameterValue<Boolean>(parameter, individualEffect.get(parameter) < 0));
        }
        
        return bestResult;
    }
    
    private String buildFormula() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(minBase.getTime());
        
        for (BooleanParameter param : parameters) {
            long effect = individualEffect.get(param);
            sb.append(' ').append(effect < 0 ? '-' : '+').append(' ').append(Math.abs(effect)).append('*').append(param.getName());
        }
        
        for (Map.Entry<Set<BooleanParameter>, Long> interaction : interactionEffects.entrySet()) {
            long effect = interaction.getValue();
            sb.append(' ').append(effect < 0 ? '-' : '+').append(' ').append(Math.abs(effect));
            for (BooleanParameter param : interaction.getKey()) {
                sb.append('*').append(param.getName());
            }
        }
        
        return sb.toString();
    }
    
    private List<ParameterValue<?>> buildParameterValuesWithOneEnabled(int indexOfSingleTrue) {
        List<ParameterValue<?>> values = new ArrayList<>(parameters.size());
        for (int i = 0; i < parameters.size(); i++) {
            values.add(new ParameterValue<Boolean>(parameters.get(i), i == indexOfSingleTrue));
        }
        return values;
    }
    
    private List<ParameterValue<?>> buildParameterValuesWithAllButOneEnabled(int indexOfSingleFalse) {
        List<ParameterValue<?>> values = new ArrayList<>(parameters.size());
        for (int i = 0; i < parameters.size(); i++) {
            values.add(new ParameterValue<Boolean>(parameters.get(i), i != indexOfSingleFalse));
        }
        return values;
    }
    
    private List<ParameterValue<?>> buildParameterValuesWithTwoEnabled(int indexOfTrueA, int indexofTrueB) {
        List<ParameterValue<?>> values = new ArrayList<>(parameters.size());
        for (int i = 0; i < parameters.size(); i++) {
            values.add(new ParameterValue<Boolean>(parameters.get(i), i == indexOfTrueA || i == indexofTrueB));
        }
        return values;
    }

}
