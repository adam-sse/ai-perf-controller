package net.ssehub.ai_perf.model;

import java.util.Optional;

public class ParameterValue<T> {
    
    private Parameter<T> parameter;
    
    private T value;

    public ParameterValue(Parameter<T> parameter, T value) {
        this.parameter = parameter;
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
    
    public Parameter<T> getParameter() {
        return parameter;
    }
    
    public enum Direction {
        LOWER, HIGHER
    }
    
    public Optional<ParameterValue<T>> getNeighbor(Direction direction) {
        return parameter.getNeighbor(this, direction);
    }
    
    public <R> R accept(IParameterValueVisitor<R> visitor) {
        return this.parameter.accept(visitor, this);
    }
    
    @Override
    public String toString() {
        return parameter.getName() + "=" + value;
    }

}
