package cn.chengzhiya.mhdfyaml.yaml;

import cn.chengzhiya.mhdfyaml.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MemoryConfiguration implements ConfigurationSection {
    protected Map<String, Object> data;

    protected MemoryConfiguration(Map<String, Object> data) {
        this.data = data;
    }

    public MemoryConfiguration() {
        this(new LinkedHashMap<>());
    }

    /**
     * 设定配置值
     *
     * @param path  配置键
     * @param value 配置值
     */
    @Override
    public void set(String path, Object value) {
        String[] keys = path.split("\\.");
        Map<String, Object> currentMap = this.data;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            Object nested = currentMap.get(key);
            if (!(nested instanceof Map)) {
                nested = new LinkedHashMap<String, Object>();
                currentMap.put(key, nested);
            }
            currentMap = (Map<String, Object>) nested;
        }

        if (value == null) currentMap.remove(keys[keys.length - 1]);
        else currentMap.put(keys[keys.length - 1], value);
    }

    @Override
    public <T> T get(String path, Class<T> clazz) {
        String[] keys = path.split("\\.");
        Object currentValue = this.data;

        for (String key : keys) {
            if (!(currentValue instanceof Map)) return null;

            currentValue = ((Map<String, Object>) currentValue).get(key);
            if (currentValue == null) return null;
        }

        return (T) currentValue;
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if (!deep) return this.data.keySet();
        return MapUtil.getAllKeys(this.data);
    }
}
