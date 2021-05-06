package net.ssehub.ai_perf.net;

import net.ssehub.ai_perf.json_util.Required;

public class MeasureResult {

    @Required
    private long time;
    
    @Required
    private long stdev;
    
    public MeasureResult(long time, long stdev) {
        this.time = time;
        this.stdev = stdev;
    }
    
    public long getTime() {
        return time;
    }
    
    public long getStdev() {
        return stdev;
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
