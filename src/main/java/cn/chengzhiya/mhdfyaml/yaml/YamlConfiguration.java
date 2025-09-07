package cn.chengzhiya.mhdfyaml.yaml;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlConfiguration extends MemoryConfiguration {
    @Getter
    @Setter
    private DumperOptions options = new DumperOptions();

    public YamlConfiguration() {
        this.options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.options.setWidth(Integer.MAX_VALUE);
        this.options.setPrettyFlow(true);
        this.options.setIndent(2);
    }

    /**
     * 加载配置文件
     *
     * @param reader 配置文件读取实例
     */
    public static YamlConfiguration load(Reader reader) {
        YamlConfiguration configuration = new YamlConfiguration();
        Map<String, Object> loadedData = new Yaml(configuration.getOptions()).load(reader);
        configuration.data = (loadedData != null) ? loadedData : new LinkedHashMap<>();

        return configuration;
    }

    /**
     * 加载配置文件
     *
     * @param inputStream 配置文件输入流实例
     */
    public static YamlConfiguration load(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return YamlConfiguration.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("无法从输入流加载 YAML", e);
        }
    }

    /**
     * 加载配置文件
     *
     * @param file 配置文件文件实例
     */
    public static YamlConfiguration load(File file) throws IOException {
        if (!file.exists()) throw new RuntimeException("找不到文件: " + file.getPath());

        try (FileInputStream fis = new FileInputStream(file)) {
            return YamlConfiguration.load(fis);
        }
    }

    /**
     * 保存配置文件
     *
     * @param file 文件实例
     */
    public void save(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) Files.createDirectories(parent.toPath());

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            new Yaml(this.getOptions()).dump(this.data, writer);
        }
    }
}
