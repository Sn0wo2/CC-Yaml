package cn.chengzhimeow.ccyaml.configuration.yaml;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
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
    private final @NotNull List<Integer> foldLineList = new ArrayList<>();

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
            String value = styledString.getValue();

            if (value != null && styledString.value().getScalarStyle() == DumperOptions.ScalarStyle.FOLDED) {
                this.representer.getFoldLineList().add(styledString.value().getStartMark().getLine());
                return this.representer.representScalar(Tag.STR, value.replace(" ", "\n"), DumperOptions.ScalarStyle.LITERAL);
            } else return this.representer.representScalar(Tag.STR, value, styledString.value().getScalarStyle());
        }
    }
}
