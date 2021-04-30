package net.ssehub.ai_perf.net;

import net.ssehub.ai_perf.json_util.Required;

public class MeasureResult {

    @Required
    private long time;
    
    public MeasureResult(long time) {
        this.time = time;
    }
    
    public long getTime() {
        return time;
    }
    
    public boolean isBetterThan(MeasureResult other) {
        return this.time < other.time;
    }
    
    @Override
    public String toString() {
        return time + " ms";
    }
    
}
