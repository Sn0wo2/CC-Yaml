package cn.chengzhimeow.ccyaml.configuration.yaml;

import cn.chengzhimeow.ccyaml.configuration.StringSectionData;
import org.yaml.snakeyaml.nodes.ScalarNode;

@SuppressWarnings("unused")
public record YamlStringSectionData(
        ScalarNode value
) implements StringSectionData {
    @Override
    public String getValue() {
        return this.value.getValue();
    }

    public ScalarNode node() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
