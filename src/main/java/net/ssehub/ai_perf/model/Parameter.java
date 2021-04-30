package net.ssehub.ai_perf.model;

import java.util.Optional;

import net.ssehub.ai_perf.model.ParameterValue.Direction;

public abstract class Parameter<T> {

    private String name;
    
    private T defaultValue;
    
    public Parameter(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
    
    public String getName() {
        return name;
    }
    
    public ParameterValue<T> getDefaultValue() {
        return new ParameterValue<T>(this, defaultValue);
    }
    
    abstract <R> R accept(IParameterValueVisitor<R> visitor, ParameterValue<T> value);
    
    public abstract Optional<ParameterValue<T>> getNeighbor(ParameterValue<T> value, Direction direction);
    
}
