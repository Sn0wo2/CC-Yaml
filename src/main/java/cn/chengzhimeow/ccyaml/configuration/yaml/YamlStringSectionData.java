package cn.chengzhimeow.ccyaml.configuration.yaml;

import cn.chengzhimeow.ccyaml.configuration.StringSectionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.ScalarNode;

@SuppressWarnings("unused")
public record YamlStringSectionData(
        @NotNull ScalarNode value
) implements StringSectionData {
    @Override
    public @Nullable String getValue() {
        return this.value.getValue();
    }

    public @NotNull ScalarNode node() {
        return this.value;
    }

    @Override
    public @NotNull String toString() {
        String value = this.getValue();
        if (value == null) return "null";
        else return value;
    }
}
