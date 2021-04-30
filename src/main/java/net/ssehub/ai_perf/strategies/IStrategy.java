package net.ssehub.ai_perf.strategies;

import java.util.List;

import net.ssehub.ai_perf.model.ParameterValue;

public interface IStrategy {
    
    public List<ParameterValue<?>> run();

}
