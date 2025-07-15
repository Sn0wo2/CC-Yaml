package cn.chengzhiya.mhdfyaml.manager;

import cn.chengzhiya.mhdfyaml.MHDFYaml;
import cn.chengzhiya.mhdfyaml.exception.ResourceException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Getter
public final class FileManager {
    private final MHDFYaml instance;

    public FileManager(MHDFYaml instance) {
        this.instance = instance;
    }

    /**
     * 保存资源
     *
     * @param filePath     保存目录
     * @param resourcePath 资源目录
     * @param replace      替换文件
     */
    @SneakyThrows
    public void saveResource(@NotNull String filePath, @NotNull String resourcePath, boolean replace) {
        File file = new File(this.getInstance().getPlugin().getDataFolder(), filePath);
        if (file.exists() && !replace) {
            return;
        }

        URL url = this.getInstance().getPlugin().getClass().getClassLoader().getResource(resourcePath);
        if (url == null) {
            throw new ResourceException("找不到资源: " + resourcePath);
        }

        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);

        try (InputStream in = url.openStream()) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                if (in == null) {
                    throw new ResourceException("读取资源 " + resourcePath + " 的时候发生了错误");
                }

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            throw new ResourceException("无法保存资源", e);
        }
    }
}
