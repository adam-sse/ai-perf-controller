package net.ssehub.ai_perf.eval;


public class EvaluationException extends Exception {

    private static final long serialVersionUID = 643593337221687493L;
    
    public EvaluationException(String message) {
        super(message);
    }
    
    public EvaluationException(Throwable cause) {
        super(cause);
    }
    
    public EvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

}
