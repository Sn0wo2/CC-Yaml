package cn.chengzhiya.mhdfyaml.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MapUtil {
    public static Set<String> getAllKeys(Map<String, Object> map) {
        Set<String> set = new HashSet<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            set.add(entry.getKey());
            if (entry.getValue() instanceof Map) {
                for (String key : MapUtil.getAllKeys((Map<String, Object>) entry.getValue())) {
                    set.add(entry.getKey() + "." + key);
                }
            }
        }

        return set;
    }
}
