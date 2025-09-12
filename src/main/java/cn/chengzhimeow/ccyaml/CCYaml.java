package cn.chengzhimeow.ccyaml;

import cn.chengzhimeow.ccyaml.manager.FileManager;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@SuppressWarnings("unused")
public final class CCYaml {
    private final ClassLoader classLoader;
    private final String version;
    private final File parent;
    private final FileManager fileManager;

    @Setter
    private String configVersionKey = "config_version";

    public CCYaml(ClassLoader classLoader, File parent, String version) {
        this.classLoader = classLoader;
        this.parent = parent;
        this.version = version;

        this.fileManager = new FileManager(this);
    }

    public CCYaml(String version) {
        this(CCYaml.class.getClassLoader(), null, version);
    }
}
