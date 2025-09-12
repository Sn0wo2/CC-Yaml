package cn.chengzhimeow.ccyaml.configuration.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

@SuppressWarnings("unused")
public class YamlConstructor extends SafeConstructor {
    public YamlConstructor(LoaderOptions loaderOptions) {
        super(loaderOptions);
        this.yamlConstructors.put(Tag.STR, new StringConstructor());
    }

    @Override
    public void flattenMapping(final MappingNode node) {
        super.flattenMapping(node);
    }

    public Object construct(Node node) {
        return super.constructObject(node);
    }

    private static class StringConstructor implements Construct {
        @Override
        public Object construct(Node node) {
            ScalarNode scalarNode = (ScalarNode) node;
            return new YamlStringSectionData(scalarNode);
        }

        @Override
        public void construct2ndStep(Node node, Object o) {
        }
    }
}
