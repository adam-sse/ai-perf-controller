package net.ssehub.ai_perf.strategies;

import java.util.List;

import net.ssehub.ai_perf.eval.AbstractEvaluator;
import net.ssehub.ai_perf.model.Parameter;

public class StrategyFactory {

    public static IStrategy createStrategy(List<Parameter<?>> parameters, AbstractEvaluator evaluator) {
        return new HillClimbing(parameters, evaluator);
    }
    
}