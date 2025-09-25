package cn.chengzhimeow.ccyaml.configuration;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class MemoryConfiguration implements ConfigurationSection {
    @Getter
    private final @Nullable ConfigurationSection parent;
    @Getter
    private final @Nullable String path;

    protected @NotNull SectionData data = new SectionData(new LinkedHashMap<String, SectionData>());

    /**
     * MemoryConfiguration 的构造函数
     *
     * @param parent 父配置节点
     * @param path   当前节点的路径
     */
    protected MemoryConfiguration(@Nullable ConfigurationSection parent, @Nullable String path) {
        this.parent = parent;
        this.path = path;
    }

    /**
     * 创建空配置节点
     *
     * @param parent 父配置节点
     * @param path   当前节点的路径
     * @return 节点
     */
    public static @NotNull MemoryConfiguration empty(@Nullable ConfigurationSection parent, @Nullable String path) {
        return new MemoryConfiguration(parent, path);
    }

    /**
     * 创建空配置节点
     *
     * @param parent 父配置节点
     * @return 节点
     */
    public static @NotNull MemoryConfiguration empty(@Nullable ConfigurationSection parent) {
        return new MemoryConfiguration(parent, null);
    }

    /**
     * 创建空配置节点
     *
     * @return 节点
     */
    public static @NotNull MemoryConfiguration empty() {
        return new MemoryConfiguration(null, null);
    }

    /**
     * 递归获取 Map 中的所有键 (包括子 Map 的键)
     *
     * @param map 要扫描的 Map
     * @return 所有键的集合
     */
    public static @NotNull Set<String> getKeys(@NotNull Map<String, ?> map) {
        Set<String> set = new HashSet<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            set.add(entry.getKey());
            if (entry.getValue() instanceof SectionData data && data.getData() instanceof Map) {
                // noinspection unchecked
                for (String key : MemoryConfiguration.getKeys((Map<String, ?>) data.getData())) {
                    set.add(entry.getKey() + "." + key);
                }
            }
        }

        return set;
    }

    /**
     * 获取当前节点的完整路径键
     *
     * @param path 相对路径
     * @return 完整路径
     */
    @Override
    public @NotNull String getKey(String path) {
        if (this.path == null || this.path.isEmpty()) return path;
        return this.path + "." + path;
    }

    /**
     * 设定指定路径的值
     * 如果路径中的父节点不存在, 会自动创建
     *
     * @param path  值的路径, 使用 '.' 分隔
     * @param value 要设定的值, 如果为 null 则会移除该键
     */
    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        String[] keys = path.split("\\.");
        int end = keys.length - 1;

        // noinspection unchecked
        Map<String, SectionData> currentMap = (Map<String, SectionData>) this.data.getData();
        for (int i = 0; i < end; i++) {
            String key = keys[i];
            SectionData sectionData = Objects.requireNonNull(currentMap).get(key);

            if (sectionData == null || !(sectionData.getData() instanceof Map)) {
                Map<String, SectionData> newMap = new LinkedHashMap<>();
                sectionData = new SectionData(newMap);
                currentMap.put(key, sectionData);
            }

            // noinspection unchecked
            currentMap = (Map<String, SectionData>) sectionData.getData();
        }

        String finalKey = keys[end];
        if (value == null) Objects.requireNonNull(currentMap).remove(finalKey);
        else {
            SectionData data = Objects.requireNonNull(currentMap).get(finalKey);
            if (data == null) data = new SectionData();

            // 如果值是 Map, 递归转换为 SectionData
            if (value instanceof Map) // noinspection unchecked
                data.setData(SectionData.fromMap((Map<Object, Object>) value).getData());
            else data.setData(value);

            currentMap.put(finalKey, data);
        }
    }

    /**
     * 获取指定路径的 SectionData 对象
     *
     * @param path 值的路径, 使用 '.' 分隔
     * @return 包含数据和注释的 SectionData 对象, 如果路径不存在则返回一个空的 SectionData
     */
    @Override
    public @NotNull SectionData getSectionData(String path) {
        String[] keys = path.split("\\.");
        int end = keys.length - 1;

        // noinspection unchecked
        Map<String, SectionData> currentMap = (Map<String, SectionData>) this.data.getData();
        for (int i = 0; i < end; i++) {
            String key = keys[i];

            SectionData currentSectionData = Objects.requireNonNull(currentMap).get(key);
            if (currentSectionData != null && currentSectionData.getData() instanceof Map) // noinspection unchecked
                currentMap = (Map<String, SectionData>) currentSectionData.getData();
            else return new SectionData();
        }

        SectionData sectionData = Objects.requireNonNull(currentMap).get(keys[end]);
        return sectionData != null ? sectionData : new SectionData();
    }

    /**
     * 获取当前配置节点下的所有键
     *
     * @param deep 是否深度获取 (即包含所有子节点的键, 以 '.' 分隔)
     * @return 键的集合
     */
    @Override
    public @NotNull Set<String> getKeys(boolean deep) {
        // noinspection unchecked
        Map<String, SectionData> map = (Map<String, SectionData>) this.data.getData();

        if (!deep) return Objects.requireNonNull(map).keySet();
        return MemoryConfiguration.getKeys(Objects.requireNonNull(map));
    }
}
