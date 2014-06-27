package twittertrack.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonUtil {

    private JsonUtil() {
        // no-op
    }

    public static List<Object> getList(JSONArray jsonObject) {
        final List<Object> result = new ArrayList<>();
        final int max = jsonObject.length();
        for (int i = 0; i < max; i++) {
            result.add(getValue(jsonObject.get(i)));
        }
        return result;
    }

    public static Map<String, Object> getMap(JSONObject jsonObject) {
        final Map<String, Object> result = new HashMap<>();
        for (Object obj : jsonObject.keySet()) {
            final String key = String.valueOf(obj);
            final Object value = getValue(jsonObject.get(key));
            result.put(key, value);
        }

        return result;
    }

    public static Object getValue(Object obj) {
        if (JSONArray.class.isInstance(obj)) {
            return getList(JSONArray.class.cast(obj));
        }
        if (JSONObject.NULL.equals(obj)) {
            return null;
        }
        if (JSONObject.class.isInstance(obj)) {
            return getMap(JSONObject.class.cast(obj));
        }
        return obj;
    }

}
