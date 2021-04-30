package net.ssehub.ai_perf.model;

import java.util.Optional;

import net.ssehub.ai_perf.model.ParameterValue.Direction;

public class IntParameter extends Parameter<Integer> {

    private int min;
    
    private int max;
    
    public IntParameter(String name, int defaultValue, int min, int max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
    }

    @Override
    <R> R accept(IParameterValueVisitor<R> visitor, ParameterValue<Integer> value) {
        return visitor.visitIntParameter(value);
    }

    @Override
    public Optional<ParameterValue<Integer>> getNeighbor(ParameterValue<Integer> value, Direction direction) {
        int newValue;
        if (direction == Direction.HIGHER) {
            newValue = value.getValue() + 1;
        } else {
            newValue = value.getValue() - 1;
        }
        
        if (newValue <= max && newValue >= min) {
            return Optional.of(new ParameterValue<Integer>(this, newValue));
        } else {
            return Optional.empty();
        }
    }

}
