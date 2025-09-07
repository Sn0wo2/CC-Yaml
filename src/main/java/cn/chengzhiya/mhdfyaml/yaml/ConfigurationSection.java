package cn.chengzhiya.mhdfyaml.yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ConfigurationSection {
    void set(String path, Object value);

    /**
     * 获取配置值
     *
     * @param path  配置键
     * @param clazz 类
     */
    <T> T get(String path, Class<T> clazz);

    default Object get(String path) {
        return this.get(path, Object.class);
    }

    /**
     * 检查指定配置值是否存在
     *
     * @param path 配置键
     * @return 结果
     */
    default boolean has(String path) {
        return this.get(path) != null;
    }

    default String getString(String path, String def) {
        if (this.get(path) == null) return def;
        else return this.get(path, String.class);
    }

    default String getString(String path) {
        return this.getString(path, null);
    }

    default Integer getInt(String path, int def) {
        if (this.get(path) == null) return def;
        else return this.get(path, Integer.class);
    }

    default int getInt(String path) {
        return this.getInt(path, 0);
    }

    default Long getLong(String path, long def) {
        if (this.get(path) == null) return def;
        else return this.get(path, Long.class);
    }

    default long getLong(String path) {
        return this.getLong(path, 0);
    }

    default Boolean getBoolean(String path, boolean def) {
        if (this.get(path) == null) return def;
        else return this.get(path, Boolean.class);
    }

    default boolean getBoolean(String path) {
        return this.getBoolean(path, false);
    }

    default Double getDouble(String path, Double def) {
        if (this.get(path) == null) return def;
        else return this.get(path, Double.class);
    }

    default double getDouble(String path) {
        return this.getDouble(path, 0.0);
    }

    default Float getFloat(String path, Float def) {
        if (this.get(path) == null) return def;
        else return this.get(path, Float.class);
    }

    default float getFloat(String path) {
        return this.getFloat(path, 0.0f);
    }

    default <T> List<T> getList(String path, List<T> def, Class<T> clazz) {
        if (this.get(path) == null) return def;
        else return this.get(path, List.class);
    }

    default <T> List<T> getList(String path, Class<T> clazz) {
        return this.getList(path, new ArrayList<>(), clazz);
    }

    default List<String> getStringList(String path, List<String> def) {
        return this.getList(path, def, String.class);
    }

    default List<String> getStringList(String path) {
        return this.getStringList(path, new ArrayList<>());
    }

    default List<Integer> getIntList(String path, List<Integer> def) {
        return this.getList(path, def, Integer.class);
    }

    default List<Integer> getIntList(String path) {
        return this.getIntList(path, new ArrayList<>());
    }

    default List<Boolean> getBooleanList(String path, List<Boolean> def) {
        return this.getList(path, def, Boolean.class);
    }

    default List<Boolean> getBooleanList(String path) {
        return this.getBooleanList(path, new ArrayList<>());
    }

    default List<Double> getDoubleList(String path, List<Double> def) {
        return this.getList(path, def, Double.class);
    }

    default List<Double> getDoubleList(String path) {
        return this.getDoubleList(path, new ArrayList<>());
    }

    default List<Float> getFloatList(String path, List<Float> def) {
        return this.getList(path, def, Float.class);
    }

    default List<Float> getFloatList(String path) {
        return this.getFloatList(path, new ArrayList<>());
    }

    Set<String> getKeys(boolean deep);

    default Set<String> getKeys() {
        return this.getKeys(false);
    }

    default ConfigurationSection getConfigurationSection(String path) {
        Object val = this.get(path);
        if (!(val instanceof Map)) return null;
        return new MemoryConfiguration((Map<String, Object>) val);
    }

    default List<ConfigurationSection> getConfigurationSectionList(String path) {
        List<Map> val = this.getList(path, new ArrayList<>(), Map.class);

        List<ConfigurationSection> result = new ArrayList<>();
        for (Map map : val) {
            result.add(new MemoryConfiguration((Map<String, Object>) map));
        }
        return result;
    }
}