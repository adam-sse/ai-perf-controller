package net.ssehub.ai_perf.model;

import java.util.Objects;
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

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Parameter)) {
            return false;
        }
        Parameter<?> other = (Parameter<?>) obj;
        return Objects.equals(name, other.name);
    }
    
}
