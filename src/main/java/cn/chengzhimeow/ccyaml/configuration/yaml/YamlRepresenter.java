package cn.chengzhimeow.ccyaml.configuration.yaml;

import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class YamlRepresenter extends Representer {
    @Getter
    private final List<Integer> foldLineList = new ArrayList<>();

    public YamlRepresenter(DumperOptions options) {
        super(options);
        this.representers.put(YamlStringSectionData.class, new StringSectionDataRepresenter(this));
    }

    @Override
    public Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style) {
        return super.representScalar(tag, value, style);
    }

    private record StringSectionDataRepresenter(
            YamlRepresenter representer
    ) implements Represent {
        @Override
        public Node representData(Object data) {
            YamlStringSectionData styledString = (YamlStringSectionData) data;
            if (styledString.value().getScalarStyle() == DumperOptions.ScalarStyle.FOLDED) {
                this.representer.getFoldLineList().add(styledString.value().getStartMark().getLine());
                return this.representer.representScalar(Tag.STR, styledString.getValue().replace(" ", "\n"), DumperOptions.ScalarStyle.LITERAL);
            }
            return this.representer.representScalar(Tag.STR, styledString.getValue(), styledString.value().getScalarStyle());
        }
    }
}
