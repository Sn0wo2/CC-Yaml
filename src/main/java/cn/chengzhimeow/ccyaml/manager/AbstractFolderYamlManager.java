package cn.chengzhimeow.ccyaml.manager;

import cn.chengzhimeow.ccyaml.CCYaml;
import cn.chengzhimeow.ccyaml.configuration.yaml.YamlConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@SuppressWarnings("unused")
public abstract class AbstractFolderYamlManager {
    private final CCYaml instance;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private Map<File, YamlConfiguration> fileHashMap;

    public AbstractFolderYamlManager(CCYaml instance) {
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
     * 获取文件夹文件实例
     *
     * @return 文件夹文件实例
     */
    public File getFolder() {
        return new File(this.getInstance().getParent(), this.filePath());
    }

    /**
     * 保存默认文件
     */
    public void saveDefaultFile() {
        this.getInstance().getFileManager().saveFolderResource(this.originFilePath(), this.filePath(), false);
    }

    /**
     * 保存文件
     */
    @SneakyThrows
    public void save() {
        for (Map.Entry<File, YamlConfiguration> entry : this.getFileHashMap().entrySet()) {
            entry.getValue().save(entry.getKey());
        }
    }

    /**
     * 重载配置
     */
    @SneakyThrows
    public void reload() {
        Map<File, YamlConfiguration> fileHashMap = new HashMap<>();
        for (File file : this.getInstance().getFileManager().listFiles(this.getFolder())) {
            fileHashMap.put(file, YamlConfiguration.loadConfiguration(file));
        }
        this.setFileHashMap(fileHashMap);
    }

    /**
     * 获取文件实例列表
     *
     * @return 文件实例列表
     */
    public Set<File> getFileList() {
        return this.getFileHashMap().keySet();
    }

    /**
     * 获取配置实例实例列表
     *
     * @return 配置实例实例列表
     */
    public Collection<YamlConfiguration> getDataList() {
        return this.getFileHashMap().values();
    }

    /**
     * 获取配置实例
     *
     * @param file 文件实例
     * @return 配置实例
     */
    public YamlConfiguration getData(File file) {
        if (file == null) return null;
        return this.getFileHashMap().get(file);
    }

    /**
     * 获取配置实例
     *
     * @param path 文件路径
     * @return 配置实例
     */
    public YamlConfiguration getData(String path) {
        if (path == null) return null;
        return this.getData(new File(this.getFolder(), path));
    }
}
