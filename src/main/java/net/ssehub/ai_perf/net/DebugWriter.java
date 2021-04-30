package net.ssehub.ai_perf.net;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

class DebugWriter extends Writer {

    private Writer inner;
    
    private Logger logger;
    
    private Level level;
    
    public DebugWriter(Writer inner, Logger logger, Level level) {
        this.inner = inner;
        this.logger = logger;
        this.level = level;
    }
    
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        DebugReader.writeDebug(logger, level, "O: ", cbuf, off, len);
        inner.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        inner.flush();
    }

    @Override
    public void close() throws IOException {
        inner.close();
    }

}
