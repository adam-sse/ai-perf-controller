package net.ssehub.ai_perf.net;

import java.util.Arrays;
import java.util.List;

import net.ssehub.ai_perf.model.ParameterValue;

public class MeasureTask {

    private List<ParameterValue<?>> parameters;
    
    public MeasureTask(ParameterValue<?>... parameters) {
        this.parameters = Arrays.asList(parameters);
    }
    
    public MeasureTask(List<ParameterValue<?>> parameters) {
        this.parameters = parameters;
    }
    
    List<ParameterValue<?>> getParameters() {
        return parameters;
    }
    
}
