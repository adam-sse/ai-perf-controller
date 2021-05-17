package net.ssehub.ai_perf.eval;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.model.ParameterValue;
import net.ssehub.ai_perf.net.MeasureResult;

public abstract class AbstractEvaluator implements Closeable {

    private static final Logger LOGGER = Logger.getLogger(AbstractEvaluator.class.getName());
    
    private int numEvaluations;
    
    private Writer resultLogFile;
    
    private boolean first = true;
    
    void setResultLogFile(File file) throws IOException {
        this.resultLogFile = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
    }
    
    public MeasureResult measure(List<ParameterValue<?>> values) throws EvaluationException {
        LOGGER.log(Level.FINE, "Evaluating {0}", values);
        numEvaluations++;
        
        MeasureResult result = measureImpl(values);
        LOGGER.log(Level.FINE, "Result: {0}", result);
        
        try {
            logResult(values, result);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to log evaluation result", e);
        }
        
        return result;
    }
    
    private void logResult(List<ParameterValue<?>> values, MeasureResult result) throws IOException {
        if (first) {
            for (ParameterValue<?> value : values) {
                resultLogFile.write(value.getParameter().getName());
                resultLogFile.write(';');
            }
            
            resultLogFile.write("time;stdev\n");
            
            first = false;
        }
        
        for (ParameterValue<?> value : values) {
            resultLogFile.write(value.getValue().toString());
            resultLogFile.write(';');
        }
        resultLogFile.write(Long.toString(result.getTime()));
        resultLogFile.write(';');
        resultLogFile.write(Long.toString(result.getStdev()));
        resultLogFile.write('\n');
        resultLogFile.flush();
    }
    
    protected abstract MeasureResult measureImpl(List<ParameterValue<?>> values) throws EvaluationException;
 
    @Override
    public void close() throws IOException {
        resultLogFile.close();
    }
    
    public void logStats() {
        LOGGER.log(Level.INFO, "Evaluated {0} configurations", numEvaluations);
    }
    
}
