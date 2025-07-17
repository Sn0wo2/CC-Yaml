package cn.chengzhiya.mhdfyaml.manager;

import cn.chengzhiya.mhdfyaml.MHDFYaml;
import cn.chengzhiya.mhdfyaml.exception.ResourceException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;

@Getter
public final class FileManager {
    private final MHDFYaml instance;

    public FileManager(MHDFYaml instance) {
        this.instance = instance;
    }

    /**
     * 格式化路径
     *
     * @param path 路径
     * @return 格式化后的路径
     */
    public String formatPath(String path) {
        return path.replace("\\", "/");
    }

    /**
     * 复制文件
     *
     * @param in       输入流
     * @param filePath 文件路径实例
     * @param replace  替换文件
     */
    @SneakyThrows
    private void copyFile(InputStream in, Path filePath, boolean replace) {
        if (Files.exists(filePath) && !replace) {
            return;
        }

        Files.createDirectories(filePath.getParent());
        Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 保存文件夹资源
     *
     * @param resourceFolderPath 资源文件夹目录
     * @param fileFolderPath     保存文件夹目录
     * @param replace            替换文件
     */
    @SneakyThrows
    public void saveFolderResource(@NotNull String resourceFolderPath, @NotNull String fileFolderPath, boolean replace) {
        resourceFolderPath = this.formatPath(resourceFolderPath);
        resourceFolderPath = resourceFolderPath.endsWith("/") ? resourceFolderPath : resourceFolderPath + "/";
        fileFolderPath = this.formatPath(fileFolderPath);
        fileFolderPath = fileFolderPath.endsWith("/") ? fileFolderPath : fileFolderPath + "/";

        ClassLoader loader = this.getInstance().getPlugin().getClass().getClassLoader();
        URL folderUrl = loader.getResource(resourceFolderPath);
        if (folderUrl == null) {
            throw new ResourceException("找不到资源文件夹: " + resourceFolderPath);
        }

        Enumeration<URL> resources = loader.getResources(resourceFolderPath);
        while (resources.hasMoreElements()) {
            URL resourceUrl = resources.nextElement();
            try (JarFile jar = ((JarURLConnection) resourceUrl.openConnection()).getJarFile()) {
                String finalResourceFolderPath = resourceFolderPath;
                String finalFileFolderPath = fileFolderPath;
                jar.stream()
                        .filter(entry -> this.formatPath(entry.getName()).startsWith(finalResourceFolderPath) && !entry.isDirectory())
                        .forEach(entry -> {
                            try {
                                File target = new File(this.getInstance().getPlugin().getDataFolder(), entry.toString().replaceFirst(finalResourceFolderPath, finalFileFolderPath));
                                this.copyFile(jar.getInputStream(entry), target.toPath(), replace);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
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
        resourcePath = this.formatPath(resourcePath);
        filePath = this.formatPath(filePath);

        File target = new File(this.getInstance().getPlugin().getDataFolder(), filePath);

        ClassLoader loader = this.getInstance().getPlugin().getClass().getClassLoader();
        URL resourceUrl = loader.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new ResourceException("找不到资源: " + resourcePath);
        }

        try (InputStream in = loader.getResourceAsStream(resourcePath)) {
            this.copyFile(in, target.toPath(), replace);
        }
    }


    /**
     * 获取一个目录下所有的文件实例列表
     *
     * @param directory 目录实例
     * @return 文件实例列表
     */
    public List<File> listFiles(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        File[] fileArray = directory.listFiles();
        if (fileArray == null) {
            return new ArrayList<>();
        }

        List<File> files = new ArrayList<>();
        for (File file : fileArray) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                files.addAll(listFiles(file));
            }
        }
        return files;
    }
}
