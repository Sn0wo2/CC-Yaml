package cn.chengzhiya.mhdfyaml.configuration.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

public class YamlConstructor extends SafeConstructor {
    public YamlConstructor(LoaderOptions loaderOptions) {
        super(loaderOptions);
    }

    @Override
    public void flattenMapping(final MappingNode node) {
        super.flattenMapping(node);
    }

    public Object construct(Node node) {
        return super.constructObject(node);
    }
}
