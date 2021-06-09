package net.ssehub.ai_perf.net;

import java.util.Collections;
import java.util.List;

import net.ssehub.ai_perf.json_util.Required;

public class MeasureResult {

    @Required
    private long time;
    
    @Required
    private long stdev;
    
    private List<Long> measures;  
    
    public MeasureResult(long time, long stdev) {
        this.time = time;
        this.stdev = stdev;
    }
    
    public MeasureResult(long time, long stdev, List<Long> measures) {
        this.time = time;
        this.stdev = stdev;
        this.measures = measures;
    }
    
    public long getTime() {
        return time;
    }
    
    public long getStdev() {
        return stdev;
    }
    
    public List<Long> getMeasures() {
        if (measures != null) {
            return Collections.unmodifiableList(measures);
        } else {
            return null;
        }
    }
    
    public boolean isBetterThan(MeasureResult other) {
        long stdev = Math.min(this.stdev, other.stdev);
        return this.time < other.time - stdev; // at least one standard deviation better
    }
    
    @Override
    public String toString() {
        return time + "±" + stdev + " ms";
    }
    
}
