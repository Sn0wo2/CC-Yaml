package cn.chengzhimeow.ccyaml.manager;

import cn.chengzhimeow.ccyaml.CCYaml;
import cn.chengzhimeow.ccyaml.configuration.yaml.YamlConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@SuppressWarnings("unused")
public abstract class AbstractFolderYamlManager {
    private final @NotNull CCYaml instance;
    private @NotNull Map<File, YamlConfiguration> fileHashMap;
    private @Nullable File folder;

    public AbstractFolderYamlManager(@NotNull CCYaml instance) {
        this.instance = instance;
        this.fileHashMap = new HashMap<>();
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
    public @NotNull File getFolder() {
        if (this.folder == null)
            this.folder = new File(this.getInstance().getParent(), this.filePath());
        return this.folder;
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
        this.fileHashMap = fileHashMap;
    }

    /**
     * 获取文件实例列表
     *
     * @return 文件实例列表
     */
    public @NotNull Set<File> getFileList() {
        return this.getFileHashMap().keySet();
    }

    /**
     * 获取配置实例实例列表
     *
     * @return 配置实例实例列表
     */
    public @NotNull Collection<YamlConfiguration> getDataList() {
        return this.getFileHashMap().values();
    }

    /**
     * 获取配置实例
     *
     * @param file 文件实例
     * @return 配置实例
     */
    public @Nullable YamlConfiguration getData(@NotNull File file) {
        return this.getFileHashMap().get(file);
    }

    /**
     * 获取配置实例
     *
     * @param path 文件路径
     * @return 配置实例
     */
    public @Nullable YamlConfiguration getData(@NotNull String path) {
        return this.getData(new File(this.getFolder(), path));
    }
}
