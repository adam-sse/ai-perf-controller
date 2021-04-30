package net.ssehub.ai_perf.net;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.ssehub.ai_perf.model.IParameterValueVisitor;
import net.ssehub.ai_perf.model.ParameterValue;

class MeasureTaskSerializer implements JsonSerializer<MeasureTask> {

    @Override
    public JsonElement serialize(MeasureTask src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        
        JsonObject parameters = new JsonObject();
        result.add("parameters", parameters);
        
        for (ParameterValue<?> paramValue : src.getParameters()) {
            parameters.add(paramValue.getParameter().getName(), valueToJson(paramValue));
        }
        
        return result;
    }
    
    private static JsonElement valueToJson(ParameterValue<?> value) {
        return value.accept(new IParameterValueVisitor<JsonElement>() {

            @Override
            public JsonElement visitIntParameter(ParameterValue<Integer> value) {
                return new JsonPrimitive(value.getValue());
            }

            @Override
            public JsonElement visitDoubleParameter(ParameterValue<Double> value) {
                return new JsonPrimitive(value.getValue());
            }

            @Override
            public JsonElement visitBooleanParameter(ParameterValue<Boolean> value) {
                return new JsonPrimitive(value.getValue());
            }
        });
    }

}
