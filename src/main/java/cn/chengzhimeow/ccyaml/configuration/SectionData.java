package cn.chengzhimeow.ccyaml.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings("unused")
public class SectionData {
    private Object data;
    private List<String> commentList;
    private List<String> inlineCommentList;
    private List<String> endCommentList;

    public SectionData(Object data) {
        this.data = data;
        this.commentList = new ArrayList<>();
        this.inlineCommentList = new ArrayList<>();
        this.endCommentList = new ArrayList<>();
    }

    public SectionData() {
        this(null);
    }

    /**
     * 从 Map 转换
     *
     * @param map 要转换的 Map
     * @return 转换后的 SectionData
     */
    public static SectionData fromMap(Map<Object, Object> map) {
        Map<String, SectionData> dataMap = new LinkedHashMap<>();

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object keyObj = entry.getKey();
            String key;
            if (keyObj instanceof String s) key = s;
            else if (keyObj instanceof StringSectionData s) key = s.getValue();
            else continue;

            if (entry.getValue() instanceof SectionData value) dataMap.put(key, value);
            else if (entry.getValue() instanceof Map) // noinspection unchecked
                dataMap.put(key, SectionData.fromMap((Map<Object, Object>) entry.getValue()));
            else dataMap.put(key, new SectionData(entry.getValue()));
        }

        return new SectionData(dataMap);
    }
}
