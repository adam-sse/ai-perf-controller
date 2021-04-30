package net.ssehub.ai_perf;

public class InvalidConfigurationException extends Exception {

    private static final long serialVersionUID = -2737829640886743299L;
    
    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }
    
    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
