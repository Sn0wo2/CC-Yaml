package cn.chengzhiya.mhdfyaml.manager;

import cn.chengzhiya.mhdfyaml.MHDFYaml;
import cn.chengzhiya.mhdfyaml.exception.ResourceException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter
public final class FileManager {
    private final MHDFYaml instance;

    public FileManager(MHDFYaml instance) {
        this.instance = instance;
    }

    /**
     * 检查指定目录是否为文件夹
     *
     * @param loader 类加载器实例
     * @param path   路径
     * @return 结果
     */
    private boolean isDirectoryResource(ClassLoader loader, String path) {
        String dirPath = path.endsWith("/") ? path : path + "/";
        URL url = loader.getResource(dirPath);

        return url != null && url.getProtocol().equals("jar");
    }

    /**
     * 复制目录文件
     *
     * @param loader       类加载器实例
     * @param resourcePath 资源路径
     * @param filePath     文件路径实例
     * @param replace      替换文件
     */
    @SneakyThrows
    private void copyDirectory(ClassLoader loader, String resourcePath, Path filePath, boolean replace) {
        String dirPath = resourcePath.endsWith("/") ? resourcePath : resourcePath + "/";

        try (InputStream stream = loader.getResourceAsStream(dirPath)) {
            if (stream == null) {
                return;
            }

            Enumeration<URL> resources = loader.getResources(dirPath);
            while (resources.hasMoreElements()) {
                URL jarUrl = resources.nextElement();
                try (JarFile jar = ((JarURLConnection) jarUrl.openConnection()).getJarFile()) {
                    jar.stream()
                            .filter(entry -> entry.getName().startsWith(resourcePath) && !entry.isDirectory())
                            .forEach(entry -> this.copyJarEntry(jar, entry, filePath, replace));
                }
            }
        }
    }


    /**
     * 复制文件
     *
     * @param jar      插件jar文件实例
     * @param entry    文件实例
     * @param filePath 文件路径实例
     * @param replace  替换文件
     */
    @SneakyThrows
    private void copyJarEntry(JarFile jar, JarEntry entry, Path filePath, boolean replace) {
        if (Files.exists(filePath) && !replace) {
            return;
        }
        Files.createDirectories(filePath.getParent());

        try (InputStream in = jar.getInputStream(entry)) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 保存资源
     *
     * @param resourcePath 资源目录
     * @param filePath     保存目录
     * @param replace      替换文件
     */
    @SneakyThrows
    public void saveResource(@NotNull String resourcePath, @NotNull String filePath, boolean replace) {
        File target = new File(this.getInstance().getPlugin().getDataFolder(), filePath);
        if (target.exists() && !replace) {
            return;
        }

        ClassLoader loader = this.getInstance().getPlugin().getClass().getClassLoader();
        URL resourceUrl = loader.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new ResourceException("找不到资源: " + resourcePath);
        }

        if (this.isDirectoryResource(loader, resourcePath)) {
            this.copyDirectory(loader, resourcePath, target.toPath(), replace);
        }

        Files.createDirectories(target.getParentFile().toPath());
        try (InputStream in = loader.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new ResourceException("无法读取资源: " + resourcePath);
            }
            Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }


    /**
     * 获取一个目录下所有的文件实例列表
     *
     * @param directory 目录实例
     * @return 文件实例列表
     */
    public List<File> listFiles(File directory) {
        List<File> files = new ArrayList<>();
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                files.addAll(listFiles(file));
            }
        }

        return files;
    }
}
