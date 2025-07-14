package cn.chengzhiya.mhdfyaml;

import cn.chengzhiya.mhdfreflection.manager.ReflectionManager;
import cn.chengzhiya.mhdfyaml.manager.FileManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MHDFYaml {
    public static MHDFYaml instance;

    private final JavaPlugin plugin;
    private final FileManager fileManager;
    private final ReflectionManager reflectionManager;

    public MHDFYaml(JavaPlugin plugin) {
        instance = this;

        this.plugin = plugin;
        this.fileManager = new FileManager();
        this.reflectionManager = new ReflectionManager();
    }
}
