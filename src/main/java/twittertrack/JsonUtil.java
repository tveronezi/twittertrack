/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package twittertrack;

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
