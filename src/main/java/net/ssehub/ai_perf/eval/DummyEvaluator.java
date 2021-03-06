package net.ssehub.ai_perf.eval;

import java.util.List;
import java.util.Random;

import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;

public class DummyEvaluator extends AbstractEvaluator {

    private Random random = new Random(1234L);
    
    @Override
    protected MeasureResult measureImpl(List<ParameterValue<?>> values) throws EvaluationException {
        return new MeasureResult(random.nextInt(500) + 500, random.nextInt(50) + 50);
    }

}
