package net.ssehub.ai_perf.eval;

import java.io.IOException;
import java.util.List;

import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;
import net.ssehub.ai_perf.net.MeasureTask;
import net.ssehub.ai_perf.net.NetworkConnection;

class NetworkEvaluator extends AbstractEvaluator {

    private NetworkConnection connection;
    
    public NetworkEvaluator(NetworkConnection connection) {
        this.connection = connection;
    }
    
    protected MeasureResult measureImpl(List<ParameterValue<?>> values) throws EvaluationException {
        MeasureTask task = new MeasureTask(values);
        MeasureResult result;
        try {
            result = connection.sendTask(task);
        } catch (IOException e) {
            throw new EvaluationException("Evaluation on remote worker failed", e);
        }
        
        return result;
    }
    
    @Override
    public void close() throws IOException {
        connection.close();
    }
    
}
