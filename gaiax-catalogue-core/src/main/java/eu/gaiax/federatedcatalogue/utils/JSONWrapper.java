package eu.gaiax.federatedcatalogue.utils;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class JSONWrapper {
    private static final JSONObject EMPTY = new JSONObject();
    protected final JSONObject json;

    public JSONWrapper() {
        json = new JSONObject();
    }

    public String getString(String key) {
        return json.optString(key);
    }

    public boolean getBoolean(String key) {
        return json.optBoolean(key);
    }

    public Number getNumber(String key, Number defaultValue) {
        return json.optNumber(key, defaultValue);
    }

    public String getString(String... path) {
        return getFromPath(-1, path).optString(path[path.length - 1]);
    }

    public Optional<String> getOptString(String... path) {
        return Optional.of(getString(path)).filter(s -> !s.isBlank());
    }

    public JSONObject getObject(String... path) {
        return getFromPath(0, path);
    }

    public JSONObject getFromPath(int distance, String... path) {
        if (path == null || path.length == 0) {
            return distance == 0 ? json : EMPTY;
        }
        JSONObject o = json;
        for (var i = 0; i < Math.max(path.length - 1, path.length + distance); i++) {
            o = o.optJSONObject(path[i], EMPTY);
        }
        return o;
    }

    public JSONObject getObject(String key) {
        return json.optJSONObject(key, EMPTY);
    }

    public JSONArray getArray(String key) {
        return SelfDescriptionConstant.getAsArray(json, key);
    }

    public JSONArray getArray(String... path) {
        return SelfDescriptionConstant.getAsArray(getFromPath(-1, path), path[path.length - 1]);
    }

    public Stream<JSONPathValue> stream() {
        return streamProperties(JSONPathValue.NOT_IN_ARRAY, json);
    }

    public static Stream<JSONPathValue> streamProperties(int arrayIndex, Object obj, String... currentPath) {
        if (obj instanceof JSONObject jsonObject) {
            return jsonObject
                    .keySet()
                    .stream()
                    .flatMap(key -> streamProperties(JSONPathValue.NOT_IN_ARRAY, jsonObject.get(key), add(currentPath, key)));
        }
        if (obj instanceof JSONArray jsonArray) {
            return IntStream
                    .range(0, jsonArray.length())
                    .boxed()
                    .flatMap(index -> streamProperties(index, jsonArray.get(index), currentPath));
        }
        return Stream.of(new JSONPathValue(arrayIndex, obj, currentPath));
    }

    public static String[] add(String[] array, String element) {
        String[] result = new String[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = element;
        return result;
    }

}
