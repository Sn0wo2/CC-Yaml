package cn.chengzhimeow.ccyaml;

import cn.chengzhimeow.ccyaml.manager.FileManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Getter
@SuppressWarnings("unused")
public final class CCYaml {
    private final @NotNull ClassLoader classLoader;
    private final @NotNull String version;
    private final @Nullable File parent;
    private final @NotNull FileManager fileManager;

    @Setter
    private @NotNull String configVersionKey = "config_version";

    public CCYaml(@NotNull ClassLoader classLoader, @Nullable File parent, @NotNull String version) {
        this.classLoader = classLoader;
        this.parent = parent;
        this.version = version;

        this.fileManager = new FileManager(this);
    }

    public CCYaml(String version) {
        this(CCYaml.class.getClassLoader(), null, version);
    }
}
