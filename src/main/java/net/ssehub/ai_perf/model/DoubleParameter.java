package net.ssehub.ai_perf.model;

import java.util.Optional;

import net.ssehub.ai_perf.model.ParameterValue.Direction;

public class DoubleParameter extends Parameter<Double> {

    private double min;
    
    private double max;
    
    private double step;
    
    public DoubleParameter(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    <R> R accept(IParameterValueVisitor<R> visitor, ParameterValue<Double> value) {
        return visitor.visitDoubleParameter(value);
    }

    @Override
    public Optional<ParameterValue<Double>> getNeighbor(ParameterValue<Double> value, Direction direction) {
        double newValue;
        if (direction == Direction.HIGHER) {
            newValue = value.getValue() + step;
        } else {
            newValue = value.getValue() - step;
        }
        
        if (newValue <= max && newValue >= min) {
            return Optional.of(new ParameterValue<Double>(this, newValue));
        } else {
            return Optional.empty();
        }
    }

}
