package cn.chengzhimeow.ccyaml.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
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
    public static SectionData fromMap(Map<String, Object> map) {
        Map<String, SectionData> dataMap = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof SectionData value) dataMap.put(entry.getKey(), value);
            else if (entry.getValue() instanceof Map) // noinspection unchecked
                dataMap.put(entry.getKey(), SectionData.fromMap((Map<String, Object>) entry.getValue()));
            else dataMap.put(entry.getKey(), new SectionData(entry.getValue()));
        }

        return new SectionData(dataMap);
    }
}
