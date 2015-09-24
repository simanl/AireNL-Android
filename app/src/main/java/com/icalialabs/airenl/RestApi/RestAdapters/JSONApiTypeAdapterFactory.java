package com.icalialabs.airenl.RestApi.RestAdapters;

import android.support.annotation.NonNull;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Compean on 23/09/15.
 */
public class JSONApiTypeAdapterFactory implements TypeAdapterFactory {

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
                    HashMap<String, HashMap<String, JsonElement>> included = new HashMap<String, HashMap<String, JsonElement>>();
                    if (jsonObject.has("included") && jsonObject.get("included").isJsonArray()) {
                        JsonArray includedArray = (JsonArray)jsonObject.get("included");
                        Iterator<JsonElement> iterator = includedArray.iterator();
                        while (iterator.hasNext()) {
                            JsonObject includedData = (JsonObject)iterator.next();
                            HashMap<String, JsonElement> includedDataHashMap;
                            if (included.get(includedData.get("type").getAsString()) == null) {
                                includedDataHashMap = new HashMap<String, JsonElement>();
                                included.put(includedData.get("type").getAsString(),includedDataHashMap);
                            } else {
                                includedDataHashMap = included.get(includedData.get("type").getAsString());
                            }
                            ((JsonObject)includedData.get("attributes")).add("id",includedData.get("id"));
                            includedDataHashMap.put(includedData.get("id").getAsString(),includedData.get("attributes"));

                        }
                    }
                    if (jsonObject.has("data") && jsonObject.get("data").isJsonArray())
                    {
                        JsonArray jsonOutPut = new JsonArray();
                        JsonArray jsonArray = (JsonArray)jsonObject.get("data");
                        Iterator<JsonElement> iterator = jsonArray.iterator();
                        while (iterator.hasNext()) {
                            JsonObject station = (JsonObject)iterator.next();
                            JsonObject properties = (JsonObject)station.get("attributes");
                            properties.add("id",station.get("id"));
                            if (properties.get("latlon") != null) {
                                String[] latlon = properties.get("latlon").getAsString().split(",");
                                JsonObject coordinate = new JsonObject();
                                coordinate.addProperty("latitude",latlon[0]);
                                coordinate.addProperty("longitude",latlon[1]);
                                properties.add("coordinate", coordinate);
                            }
                            if (station.get("relationships") != null) {
                                JsonObject relationships = (JsonObject)station.get("relationships");
                                Iterator relationshipIterator = relationships.entrySet().iterator();
                                while (relationshipIterator.hasNext()) {
                                    Map.Entry<String, JsonElement> realtionship = (Map.Entry<String, JsonElement>)relationshipIterator.next();
                                    JsonObject lastMeasurementData = realtionship.getValue().getAsJsonObject().get("data").getAsJsonObject();
                                    HashMap<String, JsonElement> includedType =  included.get(lastMeasurementData.get("type").getAsString());
                                    if (includedType != null) {
                                        JsonElement includedElement = includedType.get(lastMeasurementData.get("id").getAsString());
                                        if (includedElement != null) {
                                            properties.add(realtionship.getKey(),includedElement);
                                        }
                                    } else {
                                        properties.add(realtionship.getKey(),lastMeasurementData);
                                    }
                                }
                            }
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
