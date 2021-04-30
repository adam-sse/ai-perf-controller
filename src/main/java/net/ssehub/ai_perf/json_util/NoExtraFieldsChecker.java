package net.ssehub.ai_perf.json_util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
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

public class NoExtraFieldsChecker implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        
        Set<String> fields = Arrays.stream(type.getRawType().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toSet());
        
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
                    for (Entry<String, JsonElement> entry : obj.entrySet()) {
                        if (!fields.contains(entry.getKey())) {
                            throw new JsonParseException("Field \"" + entry.getKey() + "\" is extra");
                        }
                    }
                }
                
                return delegate.fromJsonTree(element);
            }
        };
    }

}
