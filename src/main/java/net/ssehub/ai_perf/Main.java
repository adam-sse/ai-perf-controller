package net.ssehub.ai_perf;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ssehub.ai_perf.net.NetworkConnection;
import net.ssehub.ai_perf.strategies.Evaluator;
import net.ssehub.ai_perf.strategies.IStrategy;
import net.ssehub.ai_perf.strategies.StrategyFactory;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        LoggingSetup.setupStdoutLogging();
        
        LOGGER.info("Starting controller");
        
        Configuration config;
        try {
            File configFile = new File("config.json");
            LOGGER.info("Loading " + configFile);
            config = Configuration.load(configFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't read configuration file", e);
            return;
        } catch (InvalidConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Invalid configuration file", e);
            return;
        }
        LoggingSetup.setLevel(config.getLogLevel());
        LOGGER.log(Level.CONFIG, "Log level set to " + config.getLogLevel());
        
        try (NetworkConnection connection = new NetworkConnection(config.getWorkerIp())) {
            
            Evaluator evaluator = new Evaluator(connection);
            
            IStrategy strategy = StrategyFactory.createStrategy(config.getParameters(), evaluator);
            strategy.run();
            
            evaluator.logStats();
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't connect to worker", e);
            return;
        }
    }
    
}
