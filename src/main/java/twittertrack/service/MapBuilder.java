package twittertrack.service;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {
    private final Map<String, String> map = new HashMap<>();

    public Map<String, String> getMap() {
        return map;
    }

    public MapBuilder put(String key, String value) {
        this.map.put(key, value);
        return this;
    }

}
