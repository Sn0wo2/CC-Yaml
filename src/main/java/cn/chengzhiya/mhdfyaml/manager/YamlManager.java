package cn.chengzhiya.mhdfyaml.manager;

import cn.chengzhiya.mhdfyaml.MHDFYaml;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public abstract class YamlManager {
    private final MHDFYaml instance;
    private YamlConfiguration data;

    public YamlManager(MHDFYaml instance) {
        this.instance = instance;
    }

    /**
     * 获取源文件路径
     *
     * @return 源文件路径
     */
    abstract public String getOriginFilePath();

    /**
     * 获取文件路径
     *
     * @return 文件路径
     */
    abstract public String getFilePath();

    /**
     * 获取文件实例
     *
     * @return 文件实例
     */
    public File getFile() {
        return new File(this.getInstance().getPlugin().getDataFolder(), getFilePath());
    }

    /**
     * 保存默认文件
     */
    public void saveDefaultFile() {
        this.getInstance().getFileManager().saveResource(this.getOriginFilePath(), getFilePath(), false);
    }

    /**
     * 更新配置
     */
    @SneakyThrows
    public void update() {
        String version = this.getInstance().getPlugin().getDescription().getVersion();
        String configVersion = this.getData().getString("configVersion");

        if (configVersion != null && configVersion.equals(version)) {
            return;
        }

        URL url = this.getInstance().getPlugin().getClass().getClassLoader().getResource(this.getOriginFilePath());
        if (url == null) {
            return;
        }

        try (InputStream in = url.openStream()) {
            YamlConfiguration originConfig = YamlConfiguration.loadConfiguration(new StringReader(new String(in.readAllBytes())));
            Set<String> originConfigKeys = originConfig.getKeys(true);
            Set<String> configKeys = this.getData().getKeys(true);

            // 过滤完全存在的键
            originConfigKeys.removeAll(configKeys);

            // 过滤有父级的键
            {
                //noinspection ExtractMethodRecommender
                Set<String> filteredKeys = new HashSet<>();
                for (String key : originConfigKeys) {
                    int lastDotIndex = key.lastIndexOf('.');
                    if (lastDotIndex == -1) {
                        filteredKeys.add(key);
                    } else {
                        String parentKey = key.substring(0, lastDotIndex);
                        if (configKeys.contains(parentKey)) {
                            filteredKeys.add(key);
                        }
                    }
                }
                originConfigKeys = filteredKeys;
            }

            forKey:
            for (String key : originConfigKeys) {
                String[] keyParts = key.split("\\.");
                StringBuilder prefixBuilder = new StringBuilder();

                // 绕过部分配置项使其不被更新加入
                for (int i = 0; i < keyParts.length; i++) {
                    String part = keyParts[i];

                    if (i > 0) {
                        prefixBuilder.append('.');
                    }
                    prefixBuilder.append(part);

                    List<String> prefixComments = originConfig.getComments(prefixBuilder.toString());
                    if (prefixComments.contains("!noUpdate")) {
                        continue forKey;
                    }
                }

                // 更新配置值
                this.getData().set(key, originConfig.get(key));

                // 更新注释
                List<String> comments = originConfig.getComments(key);
                if (!comments.isEmpty()) {
                    this.getData().setComments(key, comments);
                }
            }

            this.getData().set("configVersion", version);

            // 防止破坏横向格式
            YamlConfigurationOptions options = this.getData().options();
            options.width(Integer.MAX_VALUE);
            this.getInstance().getReflectionManager().setFieldValue(
                    this.getInstance().getReflectionManager().getField(MemoryConfiguration.class, "options", true),
                    originConfig,
                    options
            );

            this.getData().save(this.getFile());
        }
    }

    /**
     * 重载配置
     */
    public void reload() {
        this.data = YamlConfiguration.loadConfiguration(this.getFile());
    }

    /**
     * 获取配置实例
     *
     * @return 配置实例
     */
    public YamlConfiguration getData() {
        if (this.data == null) {
            this.reload();
        }

        return this.data;
    }

    /**
     * 获取配置实例列表
     *
     * @param path 位置
     * @return 配置实例列表
     */
    public List<YamlConfiguration> getYamlConfigurationList(String path) {
        List<?> data = getData().getList(path);
        if (data == null) {
            return new ArrayList<>();
        }

        Yaml yaml = new Yaml();
        return data.stream()
                .map(yaml::dump)
                .map(StringReader::new)
                .map(YamlConfiguration::loadConfiguration)
                .toList();
    }
}
