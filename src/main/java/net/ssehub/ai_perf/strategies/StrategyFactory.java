package net.ssehub.ai_perf.strategies;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.eval.AbstractEvaluator;
import net.ssehub.ai_perf.model.Parameter;

public class StrategyFactory {
    
    private static final Logger LOGGER = Logger.getLogger(StrategyFactory.class.getName());
    
    private String type;
    
    private List<Parameter<?>> parameters;
    
    private AbstractEvaluator evaluator;
    
    public void setEvaluator(AbstractEvaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    public void setParameters(List<Parameter<?>> parameters) {
        this.parameters = parameters;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public IStrategy create() throws IllegalStateException, IllegalArgumentException {
        if (type == null) {
            throw new IllegalStateException("No type specified");
        }
        if (parameters == null) {
            throw new IllegalStateException("No parameters specified");
        }
        if (evaluator == null) {
            throw new IllegalStateException("No evaluator specified");
        }
        
        LOGGER.log(Level.CONFIG, "Using strategy {0}", type);
        
        IStrategy result;
        switch (type) {
        case "HillClimbing":
            result = new HillClimbing(parameters, evaluator);
            break;
            
        case "PairWiseBooleanInteractionModel":
            result = new PairWiseBooleanInteractionModel(parameters, evaluator);
            break;
            
        case "SingleEvaluation":
            result = new SingleEvaluation(parameters, evaluator);
            break;
        
        default:
            throw new IllegalArgumentException("Invalid evaluator type: " + type);
        }
        return result;
    }

}
