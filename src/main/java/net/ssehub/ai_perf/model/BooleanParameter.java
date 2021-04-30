package net.ssehub.ai_perf.model;

import java.util.Optional;

import net.ssehub.ai_perf.model.ParameterValue.Direction;

public class BooleanParameter extends Parameter<Boolean> {

    public BooleanParameter(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    <R> R accept(IParameterValueVisitor<R> visitor, ParameterValue<Boolean> value) {
        return visitor.visitBooleanParameter(value);
    }

    @Override
    public Optional<ParameterValue<Boolean>> getNeighbor(ParameterValue<Boolean> value, Direction direction) {
        ParameterValue<Boolean> result = null;
        
        if (value.getValue() && direction == Direction.LOWER) {
            result = new ParameterValue<Boolean>(this, false);
            
        } else if (!value.getValue() && direction == Direction.HIGHER) {
            result = new ParameterValue<Boolean>(this, true);
        }
        
        return Optional.ofNullable(result);
    }
    
}
