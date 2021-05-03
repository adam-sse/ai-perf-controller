package net.ssehub.ai_perf.eval;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;

public class DummyEvaluator extends AbstractEvaluator {

    private Random random = new Random(1234L);
    
    @Override
    protected MeasureResult measureImpl(List<ParameterValue<?>> values) throws EvaluationException {
        return new MeasureResult(random.nextInt(1000));
    }

    @Override
    public void close() throws IOException {
    }
    
}
