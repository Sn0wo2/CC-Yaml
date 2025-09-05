package cn.chengzhiya.mhdfyaml;

import cn.chengzhiya.mhdfreflection.manager.ReflectionManager;
import cn.chengzhiya.mhdfyaml.manager.FileManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class MHDFYaml {
    private final JavaPlugin plugin;
    private final FileManager fileManager;
    private final ReflectionManager reflectionManager;

    @Setter
    private String configVersionKey = "configVersion";

    public MHDFYaml(JavaPlugin plugin) {
        this.plugin = plugin;
        this.fileManager = new FileManager(this);
        this.reflectionManager = new ReflectionManager();
    }
}
