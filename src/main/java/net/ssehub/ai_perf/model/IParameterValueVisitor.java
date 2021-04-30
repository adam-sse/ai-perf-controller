package net.ssehub.ai_perf.model;


public interface IParameterValueVisitor<R> {

    public R visitIntParameter(ParameterValue<Integer> parameter);
    
    public R visitDoubleParameter(ParameterValue<Double> parameter);
    
    public R visitBooleanParameter(ParameterValue<Boolean> parameter);
    
}
