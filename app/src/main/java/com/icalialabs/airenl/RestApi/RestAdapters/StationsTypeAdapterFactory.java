package com.icalialabs.airenl.RestApi.RestAdapters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonArray;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Compean on 23/09/15.
 */
public class StationsTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {

            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            public T read(JsonReader in) throws IOException {

                JsonElement jsonElement = elementAdapter.read(in);

                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("data") && jsonObject.get("data").isJsonArray())
                    {
                        JsonArray jsonOutPut = new JsonArray();
                        JsonArray jsonArray = (JsonArray)jsonObject.get("data");
                        Iterator<JsonElement> iterator = jsonArray.iterator();
                        while (iterator.hasNext()) {
                            JsonObject station = (JsonObject)iterator.next();
                            JsonObject properties = (JsonObject)station.get("attributes");
                            properties.add("id",station.get("id"));
                            jsonOutPut.add(properties);
                        }
                        return delegate.fromJsonTree(jsonOutPut);
                    }
                }
                return delegate.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }
}
