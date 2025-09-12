package cn.chengzhimeow.ccyaml.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public interface ConfigurationSection {
    /**
     * 获取当前节点的完整路径键
     *
     * @param path 相对路径
     * @return 完整路径
     */
    String getKey(String path);

    /**
     * 设定指定路径的值
     *
     * @param path  值的路径
     * @param value 要设定的值, 如果为 null 则会移除该键
     */
    void set(String path, Object value);

    /**
     * 获取指定路径的 SectionData 对象
     *
     * @param path 路径
     * @return 包含数据和注释的 SectionData 对象, 如果路径不存在则返回一个空的 SectionData
     */
    SectionData getSectionData(String path);

    /**
     * 设定指定路径的块注释 (在键值对上方)
     *
     * @param path        路径
     * @param commentList 注释列表, 列表中的 null 或空字符串会变为空行
     */
    default void setCommentList(String path, List<String> commentList) {
        this.getSectionData(path).setCommentList(commentList);
    }

    /**
     * 获取指定路径的块注释
     *
     * @param path 路径
     * @return 注释列表
     */
    default List<String> getCommentList(String path) {
        return this.getSectionData(path).getCommentList();
    }

    /**
     * 获取指定路径的行内注释 (在键值对后方)
     *
     * @param path 路径
     * @return 行内注释列表
     */
    default List<String> getInlineCommentList(String path) {
        return this.getSectionData(path).getInlineCommentList();
    }

    /**
     * 设定指定路径的行内注释
     *
     * @param path        路径
     * @param commentList 行内注释列表
     */
    default void setInlineCommentList(String path, List<String> commentList) {
        this.getSectionData(path).setInlineCommentList(commentList);
    }

    /**
     * 获取指定路径的值并转换为指定类型
     *
     * @param path  路径
     * @param clazz 目标类型的 Class 对象
     * @param <T>   目标类型
     * @return 转换后的值, 如果路径不存在或类型不匹配可能返回 null 或抛出 ClassCastException
     */
    default <T> T get(String path, Class<T> clazz) {
        Object data = this.getSectionData(path).getData();
        if (data == null) return null;
        if (data instanceof StringSectionData str && clazz == String.class) return clazz.cast(str.getValue());
        return clazz.cast(data);
    }

    /**
     * 获取指定路径的 Object 类型的值
     *
     * @param path 路径
     * @return Object 值, 如果不存在则为 null
     */
    default Object get(String path) {
        return this.get(path, Object.class);
    }

    /**
     * 检查指定路径是否存在值
     *
     * @param path 路径
     * @return 如果路径存在值则为 true, 否则为 false
     */
    default boolean has(String path) {
        return this.getSectionData(path).getData() != null;
    }

    /**
     * 获取指定路径的字符串值
     *
     * @param path 路径
     * @param def  默认值
     * @return 字符串值, 如果不存在则返回默认值
     */
    default String getString(String path, String def) {
        String value = this.get(path, String.class);
        return value != null ? value : def;
    }

    /**
     * 获取指定路径的字符串值
     *
     * @param path 路径
     * @return 字符串值, 如果不存在则返回 null
     */
    default String getString(String path) {
        return this.getString(path, null);
    }

    /**
     * 获取指定路径的整数值
     *
     * @param path 路径
     * @param def  默认值
     * @return 整数值, 如果不存在则返回默认值
     */
    default int getInt(String path, int def) {
        Integer value = this.get(path, Integer.class);
        return value != null ? value : def;
    }

    /**
     * 获取指定路径的整数值
     *
     * @param path 路径
     * @return 整数值, 如果不存在则返回 0
     */
    default int getInt(String path) {
        return this.getInt(path, 0);
    }

    /**
     * 获取指定路径的长整数值
     *
     * @param path 路径
     * @param def  默认值
     * @return 长整数值, 如果不存在则返回默认值
     */
    default long getLong(String path, long def) {
        Long value = this.get(path, Long.class);
        return value != null ? value : def;
    }

    /**
     * 获取指定路径的长整数值
     *
     * @param path 路径
     * @return 长整数值, 如果不存在则返回 0L
     */
    default long getLong(String path) {
        return this.getLong(path, 0L);
    }

    /**
     * 获取指定路径的布尔值
     *
     * @param path 路径
     * @param def  默认值
     * @return 布尔值, 如果不存在则返回默认值
     */
    default boolean getBoolean(String path, boolean def) {
        Boolean value = this.get(path, Boolean.class);
        return value != null ? value : def;
    }

    /**
     * 获取指定路径的布尔值
     *
     * @param path 路径
     * @return 布尔值, 如果不存在则返回 false
     */
    default boolean getBoolean(String path) {
        return this.getBoolean(path, false);
    }

    /**
     * 获取指定路径的双精度浮点数值
     *
     * @param path 路径
     * @param def  默认值
     * @return 双精度浮点数值, 如果不存在则返回默认值
     */
    default double getDouble(String path, double def) {
        Double value = this.get(path, Double.class);
        return value != null ? value : def;
    }

    /**
     * 获取指定路径的双精度浮点数值
     *
     * @param path 路径
     * @return 双精度浮点数值, 如果不存在则返回 0.0
     */
    default double getDouble(String path) {
        return this.getDouble(path, 0.0);
    }

    /**
     * 获取指定路径的单精度浮点数值
     *
     * @param path 路径
     * @param def  默认值
     * @return 单精度浮点数值, 如果不存在则返回默认值
     */
    default float getFloat(String path, float def) {
        Float value = this.get(path, Float.class);
        return value != null ? value : def;
    }

    /**
     * 获取指定路径的单精度浮点数值
     *
     * @param path 路径
     * @return 单精度浮点数值, 如果不存在则返回 0.0f
     */
    default float getFloat(String path) {
        return this.getFloat(path, 0.0f);
    }

    /**
     * 获取指定路径的列表值
     *
     * @param path  路径
     * @param def   默认列表
     * @param clazz 列表元素的 Class 对象 (由于类型擦除, 此参数仅用于明确意图)
     * @param <T>   列表元素的类型
     * @return 列表值, 如果不存在则返回默认列表
     */
    @SuppressWarnings("unchecked")
    default <T> List<T> getList(String path, List<T> def, Class<T> clazz) {
        List<T> list = this.get(path, List.class);
        if (list == null) return def;
        if (list.isEmpty()) return list;

        if (clazz == String.class && list.get(0) instanceof StringSectionData)
            return (List<T>) list.stream()
                    .map(i -> ((StringSectionData) i).getValue())
                    .toList();

        return list;
    }

    /**
     * 获取指定路径的列表值
     *
     * @param path  路径
     * @param clazz 列表元素的 Class 对象 (由于类型擦除, 此参数仅用于明确意图)
     * @param <T>   列表元素的类型
     * @return 列表值, 如果不存在则返回一个空列表
     */
    default <T> List<T> getList(String path, Class<T> clazz) {
        return this.getList(path, new ArrayList<>(), clazz);
    }

    /**
     * 获取指定路径的字符串列表
     *
     * @param path 路径
     * @param def  默认列表
     * @return 字符串列表, 如果不存在则返回默认列表
     */
    default List<String> getStringList(String path, List<String> def) {
        return this.getList(path, def, String.class);
    }

    /**
     * 获取指定路径的字符串列表
     *
     * @param path 路径
     * @return 字符串列表, 如果不存在则返回一个空列表
     */
    default List<String> getStringList(String path) {
        return this.getStringList(path, new ArrayList<>());
    }

    /**
     * 获取指定路径的整数列表
     *
     * @param path 路径
     * @param def  默认列表
     * @return 整数列表, 如果不存在则返回默认列表
     */
    default List<Integer> getIntList(String path, List<Integer> def) {
        return this.getList(path, def, Integer.class);
    }

    /**
     * 获取指定路径的整数列表
     *
     * @param path 路径
     * @return 整数列表, 如果不存在则返回一个空列表
     */
    default List<Integer> getIntList(String path) {
        return this.getIntList(path, new ArrayList<>());
    }

    /**
     * 获取指定路径的布尔值列表
     *
     * @param path 路径
     * @param def  默认列表
     * @return 布尔值列表, 如果不存在则返回默认列表
     */
    default List<Boolean> getBooleanList(String path, List<Boolean> def) {
        return this.getList(path, def, Boolean.class);
    }

    /**
     * 获取指定路径的布尔值列表
     *
     * @param path 路径
     * @return 布尔值列表, 如果不存在则返回一个空列表
     */
    default List<Boolean> getBooleanList(String path) {
        return this.getBooleanList(path, new ArrayList<>());
    }

    /**
     * 获取指定路径的双精度浮点数列表
     *
     * @param path 路径
     * @param def  默认列表
     * @return 双精度浮点数列表, 如果不存在则返回默认列表
     */
    default List<Double> getDoubleList(String path, List<Double> def) {
        return this.getList(path, def, Double.class);
    }

    /**
     * 获取指定路径的双精度浮点数列表
     *
     * @param path 路径
     * @return 双精度浮点数列表, 如果不存在则返回一个空列表
     */
    default List<Double> getDoubleList(String path) {
        return this.getDoubleList(path, new ArrayList<>());
    }

    /**
     * 获取指定路径的单精度浮点数列表
     *
     * @param path 路径
     * @param def  默认列表
     * @return 单精度浮点数列表, 如果不存在则返回默认列表
     */
    default List<Float> getFloatList(String path, List<Float> def) {
        return this.getList(path, def, Float.class);
    }

    /**
     * 获取指定路径的单精度浮点数列表
     *
     * @param path 路径
     * @return 单精度浮点数列表, 如果不存在则返回一个空列表
     */
    default List<Float> getFloatList(String path) {
        return this.getFloatList(path, new ArrayList<>());
    }

    /**
     * 获取当前配置节点下的所有键
     *
     * @param deep 是否深度获取 (即包含所有子节点的键)
     * @return 键的集合
     */
    Set<String> getKeys(boolean deep);

    /**
     * 获取当前配置节点下的所有直接子键 (非深度)
     *
     * @return 键的集合
     */
    default Set<String> getKeys() {
        return this.getKeys(false);
    }

    /**
     * 获取指定路径下的子配置节点
     *
     * @param path 路径
     * @return 子配置节点, 如果路径不存在或不是一个配置节点则返回 null
     */
    default ConfigurationSection getConfigurationSection(String path) {
        SectionData data = this.getSectionData(path);
        if (!(data.getData() instanceof Map)) return null;

        MemoryConfiguration configuration = new MemoryConfiguration(this, this.getKey(path));
        configuration.data = data;
        return configuration;
    }

    /**
     * 获取指定路径下的配置节点列表
     *
     * @param path 路径
     * @return 配置节点列表
     */
    @SuppressWarnings("rawtypes")
    default List<ConfigurationSection> getConfigurationSectionList(String path) {
        List<Map> maps = this.getList(path, new ArrayList<>(), Map.class);

        List<ConfigurationSection> result = new ArrayList<>();
        for (int i = 0; i < maps.size(); i++) {
            Map map = maps.get(i);
            MemoryConfiguration configuration = new MemoryConfiguration(this, this.getKey(path) + "." + i);
            // noinspection unchecked
            configuration.data = SectionData.fromMap(map);
            result.add(configuration);
        }
        return result;
    }
}
