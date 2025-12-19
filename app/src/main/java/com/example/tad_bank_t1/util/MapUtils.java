package com.example.tad_bank_t1.util;

import java.util.Map;

public class MapUtils {
    public static void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) map.put(key, value);
    }

}
