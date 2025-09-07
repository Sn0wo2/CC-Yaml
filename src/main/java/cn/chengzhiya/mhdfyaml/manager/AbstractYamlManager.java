package cn.chengzhiya.mhdfyaml.manager;

import cn.chengzhiya.mhdfyaml.MHDFYaml;
import cn.chengzhiya.mhdfyaml.yaml.YamlConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class AbstractYamlManager {
    private final MHDFYaml instance;
    private YamlConfiguration data;

    public AbstractYamlManager(MHDFYaml instance) {
        this.instance = instance;
    }

    /**
     * 源文件路径字符串
     */
    abstract public String originFilePath();

    /**
     * 文件路径字符串
     */
    abstract public String filePath();

    /**
     * 获取文件实例
     *
     * @return 文件实例
     */
    public File getFile() {
        return new File(this.getInstance().getParentFile(), this.filePath());
    }

    /**
     * 保存默认文件
     */
    public void saveDefaultFile() {
        this.getInstance().getFileManager().saveResource(this.originFilePath(), this.filePath(), false);
    }

    /**
     * 保存文件
     */
    @SneakyThrows
    public void save() {
        this.getData().save(this.getFile());
    }

    /**
     * 更新配置
     */
    @SneakyThrows
    public void update() {
        String version = this.getInstance().getVersion();
        String configVersion = this.getData().getString(this.getInstance().getConfigVersionKey());

        // 版本相同不处理
        if (configVersion != null && configVersion.equals(version)) return;

        URL url = this.getInstance().getClassLoader().getResource(this.originFilePath());
        if (url == null) return;

        try (InputStream in = url.openStream()) {
            YamlConfiguration originConfig = YamlConfiguration.load(in);
            Set<String> originConfigKeys = originConfig.getKeys(true);
            Set<String> configKeys = this.getData().getKeys(true);

            // 过滤完全存在的键
            originConfigKeys.removeAll(configKeys);

            // 过滤有父级的键
            {
                // noinspection ExtractMethodRecommender
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

            for (String key : originConfigKeys) {
                // TODO 开发注释操作
//                String[] keyParts = key.split("\\.");
//                StringBuilder prefixBuilder = new StringBuilder();

//                // 绕过部分配置项使其不被更新加入
//                for (int i = 0; i < keyParts.length; i++) {
//                    String part = keyParts[i];
//
//                    if (i > 0) {
//                        prefixBuilder.append('.');
//                    }
//                    prefixBuilder.append(part);

//                    List<String> prefixComments = originConfig.getComments(prefixBuilder.toString());
//                    if (prefixComments.contains("!noUpdate")) {
//                        continue forKey;
//                    }
//                }

                // 更新配置值
                this.getData().set(key, originConfig.get(key));

//                // 更新注释
//                List<String> comments = originConfig.getComments(key);
//                if (!comments.isEmpty()) {
//                    this.getData().setComments(key, comments);
//                }
            }

            this.getData().set(this.getInstance().getConfigVersionKey(), version);

            this.getData().save(this.getFile());
        }
    }

    /**
     * 重载配置
     */
    @SneakyThrows
    public void reload() {
        this.data = YamlConfiguration.load(this.getFile());
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
}
