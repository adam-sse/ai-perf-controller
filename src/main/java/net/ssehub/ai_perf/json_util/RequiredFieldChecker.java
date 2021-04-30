package net.ssehub.ai_perf.json_util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class RequiredFieldChecker implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        
        List<String> requiredFields = Arrays.stream(type.getRawType().getDeclaredFields())
                .filter((field) -> field.getAnnotation(Required.class) != null)
                .map(field -> field.getName())
                .collect(Collectors.toList());
        
        if (requiredFields.isEmpty()) {
            return null;
        }
        
        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        
        return new TypeAdapter<T>() {

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                JsonElement element = Streams.parse(in);
                
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    for (String field : requiredFields) {
                        if (!obj.has(field)) {
                            throw new JsonParseException("Field \"" + field + "\" missing");
                        }
                    }
                }
                
                return delegate.fromJsonTree(element);
            }
        };
    }

}
