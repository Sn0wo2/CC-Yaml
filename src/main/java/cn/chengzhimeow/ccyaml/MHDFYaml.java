package cn.chengzhimeow.ccyaml;

import cn.chengzhimeow.ccyaml.manager.FileManager;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
public final class MHDFYaml {
    private final ClassLoader classLoader;
    private final String version;
    private final File parentFile;
    private final FileManager fileManager;

    @Setter
    private String configVersionKey = "config_version";

    public MHDFYaml(ClassLoader classLoader, File parentFile, String version) {
        this.classLoader = classLoader;
        this.parentFile = parentFile;
        this.version = version;

        this.fileManager = new FileManager(this);
    }

    public MHDFYaml(String version) {
        this(MHDFYaml.class.getClassLoader(), null, version);
    }
}
