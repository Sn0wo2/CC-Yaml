package cn.chengzhiya.mhdfyaml;

import cn.chengzhiya.mhdfyaml.configuration.yaml.YamlConfiguration;
import lombok.SneakyThrows;

import java.io.File;

public class Test {
    @SneakyThrows
    public static void main(String[] args) {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(new File("qwq.yml"));
        String qwq = data.get("qwq", String.class);
        String awa = data.get("awa", String.class);
        System.out.println(qwq + " " + awa);

        String e = data.get("a.b.c.d.e", String.class);
        String f = data.get("a.b.c.d.f", String.class);
        String g = data.get("a.b.c.d.g", String.class);
        String h = data.get("a.b.c.d.h", String.class);
        String i = data.get("a.b.c.d.i", String.class);
        System.out.println(e + f + g + h + i);

        data.set("a.b.c.d.i", "高潮");
        data.save(new File("qwq.yml"));

        data = YamlConfiguration.loadConfiguration(new File("qwq.yml"));

        e = data.get("a.b.c.d.e", String.class);
        f = data.get("a.b.c.d.f", String.class);
        g = data.get("a.b.c.d.g", String.class);
        h = data.get("a.b.c.d.h", String.class);
        i = data.get("a.b.c.d.i", String.class);
        System.out.println(e + f + g + h + i);
    }
}
