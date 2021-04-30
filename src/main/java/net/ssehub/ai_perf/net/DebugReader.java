package net.ssehub.ai_perf.net;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

class DebugReader extends Reader {

    private Reader inner;
    
    private Logger logger;
    
    private Level level;
    
    public DebugReader(Reader inner, Logger logger, Level level) {
        this.inner = inner;
        this.logger = logger;
        this.level = level;
    }
    
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int read = inner.read(cbuf, off, len);
        
        writeDebug(logger, level, "I: ", cbuf, off, read);
        
        return read;
    }

    @Override
    public void close() throws IOException {
        inner.close();
    }

    static void writeDebug(Logger logger, Level level, String prefix, char[] cbuf, int off, int len) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = off; i < off + len; i++) {
            if (!Character.isWhitespace(cbuf[i]) && cbuf[i] > 31) {
                sb.append(cbuf[i]);
            } else {
                switch (cbuf[i]) {
                case ' ':
                    sb.append(" ");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                    
                default:
                    sb.append("\\x" + Integer.toHexString(cbuf[i]));
                    break;
                }
            }
        }
        
        logger.log(level, sb.toString());
    }

}
