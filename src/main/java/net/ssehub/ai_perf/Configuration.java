package net.ssehub.ai_perf;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.ssehub.ai_perf.json_util.NoExtraFieldsChecker;
import net.ssehub.ai_perf.json_util.Required;
import net.ssehub.ai_perf.json_util.RequiredFieldChecker;
import net.ssehub.ai_perf.model.BooleanParameter;
import net.ssehub.ai_perf.model.DoubleParameter;
import net.ssehub.ai_perf.model.IntParameter;
import net.ssehub.ai_perf.model.Parameter;

public class Configuration {

    private String logLevel = "INFO";

    @Required
    private String workerIp;
    
    @Required
    private List<Parameter<?>> parameters;
    
    private Configuration() {}
    
    public static Configuration load(File file) throws IOException, InvalidConfigurationException {
        Gson gson = createGson();
        try {
            return gson.fromJson(new FileReader(file, StandardCharsets.UTF_8), Configuration.class);
            
        } catch (JsonParseException e) {
            throw new InvalidConfigurationException(e); 
        }
    }

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new RequiredFieldChecker());
        builder.registerTypeAdapterFactory(new NoExtraFieldsChecker());
        builder.registerTypeAdapter(Parameter.class, new JsonDeserializer<Parameter<?>>() {

            @Override
            public Parameter<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return jsonToParameter(json);
            }
        });
        Gson gson = builder.create();
        return gson;
    }
    
    private static Parameter<?> jsonToParameter(JsonElement element) throws JsonParseException {
        if (!element.isJsonObject()) {
            throw new JsonParseException("Parameter must be JsonObject");
        }
        JsonObject obj = element.getAsJsonObject();
        
        String type = getRequiredString(obj, "type");
        String name = getRequiredString(obj, "name");
        
        Parameter<?> result;
        switch (type) {
        case "int":
            result = new IntParameter(name, getRequiredInt(obj, "default"),
                    getRequiredInt(obj, "min"), getRequiredInt(obj, "max"));
            break;
        
        case "bool":
            result = new BooleanParameter(name, getRequiredBool(obj, "default"));
            break;
            
        case "double":
            result = new DoubleParameter(name, getRequiredDouble(obj, "default"),
                    getRequiredDouble(obj, "min"), getRequiredDouble(obj, "max"), getRequiredDouble(obj, "step"));
            break;
        
        default:
            throw new JsonParseException("Invalid type: " + type);
        }
        
        return result;
    }
    
    private static String getRequiredString(JsonObject obj, String key) throws JsonParseException {
        JsonElement element = obj.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw new JsonParseException(key + " must be a string");
        }
        return element.getAsString();
    }
    
    private static boolean getRequiredBool(JsonObject obj, String key) throws JsonParseException {
        JsonElement element = obj.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
            throw new JsonParseException(key + " must be a boolean");
        }
        return element.getAsBoolean();
    }
    
    private static int getRequiredInt(JsonObject obj, String key) throws JsonParseException {
        JsonElement element = obj.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException(key + " must be a number");
        }
        return element.getAsInt();
    }
    
    private static double getRequiredDouble(JsonObject obj, String key) throws JsonParseException {
        JsonElement element = obj.get(key);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException(key + " must be a number");
        }
        return element.getAsDouble();
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public String getWorkerIp() {
        return workerIp;
    }
    
    
    public List<Parameter<?>> getParameters() {
        return parameters;
    }
    
}
