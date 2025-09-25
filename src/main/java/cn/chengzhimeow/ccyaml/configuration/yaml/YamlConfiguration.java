package cn.chengzhimeow.ccyaml.configuration.yaml;

import cn.chengzhimeow.ccyaml.configuration.MemoryConfiguration;
import cn.chengzhimeow.ccyaml.configuration.SectionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.nodes.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

@SuppressWarnings("unused")
public class YamlConfiguration extends MemoryConfiguration {
    public final @NotNull LoaderOptions loaderOptions;
    public final @NotNull DumperOptions dumperOptions;
    private final @NotNull YamlConstructor constructor;
    private final @NotNull YamlRepresenter representer;
    private final @NotNull Yaml yaml;

    public YamlConfiguration(@NotNull LoaderOptions loaderOptions, @NotNull DumperOptions dumperOptions, @NotNull YamlConstructor constructor, @NotNull YamlRepresenter representer) {
        super(null, "");

        this.loaderOptions = loaderOptions;
        this.dumperOptions = dumperOptions;
        this.constructor = constructor;
        this.representer = representer;

        this.yaml = new Yaml(this.constructor, this.representer, this.dumperOptions, this.loaderOptions);
    }

    public YamlConfiguration(@NotNull LoaderOptions loaderOptions, @NotNull DumperOptions dumperOptions, @NotNull YamlConstructor constructor) {
        this(loaderOptions, dumperOptions, constructor, new YamlRepresenter(dumperOptions));
    }

    public YamlConfiguration(@NotNull LoaderOptions loaderOptions, @NotNull DumperOptions dumperOptions, @NotNull YamlRepresenter representer) {
        this(loaderOptions, dumperOptions, new YamlConstructor(loaderOptions), representer);
    }

    public YamlConfiguration(@NotNull LoaderOptions loaderOptions, @NotNull DumperOptions dumperOptions) {
        this(loaderOptions, dumperOptions, new YamlConstructor(loaderOptions));
    }

    public YamlConfiguration(@NotNull DumperOptions dumperOptions) {
        this(YamlConfiguration.defaultLoaderOptions(), dumperOptions);
    }

    public YamlConfiguration(@NotNull LoaderOptions loaderOptions) {
        this(loaderOptions, YamlConfiguration.defaultDumperOptions());
    }

    public YamlConfiguration() {
        this(YamlConfiguration.defaultLoaderOptions());
    }

    /**
     * 默认加载配置实例
     */
    public static @NotNull LoaderOptions defaultLoaderOptions() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(Integer.MAX_VALUE);
        loaderOptions.setCodePointLimit(Integer.MAX_VALUE);
        loaderOptions.setNestingDepthLimit(100);
        loaderOptions.setProcessComments(true);

        return loaderOptions;
    }

    /**
     * 默认加载输出实例
     */
    public static @NotNull DumperOptions defaultDumperOptions() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setWidth(Integer.MAX_VALUE);
        dumperOptions.setProcessComments(true);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setSplitLines(false);
        dumperOptions.setIndent(2);

        return dumperOptions;
    }

    /**
     * 从 Reader 加载配置文件
     *
     * @param reader 配置文件读取实例
     * @return 加载完成的 YamlConfiguration 实例
     */
    public static @NotNull YamlConfiguration loadConfiguration(@NotNull Reader reader) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(reader);
        return configuration;
    }

    /**
     * 从 InputStream 加载配置文件
     *
     * @param inputStream 配置文件输入流实例
     * @return 加载完成的 YamlConfiguration 实例
     */
    public static @NotNull YamlConfiguration loadConfiguration(@NotNull InputStream inputStream) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(inputStream);
        return configuration;
    }

    /**
     * 从 File 加载配置文件
     *
     * @param file 配置文件文件实例
     * @return 加载完成的 YamlConfiguration 实例
     * @throws IOException 如果文件读取失败
     */
    public static @NotNull YamlConfiguration loadConfiguration(@NotNull File file) throws IOException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.load(file);
        return configuration;
    }

    /**
     * 从 Reader 加载配置文件
     *
     * @param reader 配置文件读取实例
     */
    public void load(@NotNull Reader reader) {
        MappingNode node = (MappingNode) this.yaml.compose(reader);
        if (node != null) this.data = this.mappingNodeToSectionData(node);
    }

    /**
     * 从 InputStream 加载配置文件
     *
     * @param inputStream 配置文件输入流实例
     */
    public void load(@NotNull InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            this.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("无法从输入流加载 YAML", e);
        }
    }

    /**
     * 从 File 加载配置文件
     *
     * @param file 配置文件文件实例
     * @throws IOException 如果文件读取失败
     */
    public void load(@NotNull File file) throws IOException {
        if (!file.exists()) throw new FileNotFoundException("找不到文件: " + file.getPath());

        try (FileInputStream fis = new FileInputStream(file)) {
            this.load(fis);
        }
    }

    /**
     * 将配置数据保存到文件
     *
     * @param file 目标文件实例
     * @throws IOException 如果文件写入失败
     */
    public void save(@NotNull File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null) Files.createDirectories(parent.toPath());

        SectionData sectionData = this.data;
        assert sectionData.getData() != null;
        // noinspection unchecked
        MappingNode node = this.mapToMappingNode((Map<String, SectionData>) sectionData.getData());

        StringWriter stringWriter = new StringWriter();
        if (!this.isNotNullAndEmpty(node.getBlockComments()) || !this.isNotNullAndEmpty(node.getEndComments()) || !this.isNotNullAndEmpty(node.getValue())) {
            if (node.getValue().isEmpty()) node.setFlowStyle(DumperOptions.FlowStyle.FLOW);
            this.yaml.serialize(node, stringWriter);
        }
        String text = stringWriter.toString();

        String[] lines = text.split("\n");
        List<Integer> replaceLines = this.representer.getFoldLineList();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (replaceLines.contains(i)) {
                    line = line.replace("|", ">");
                }
                writer.write(line);
                writer.newLine();
            }
        }

        this.representer.getFoldLineList().clear();
    }

    /**
     * 检查指定集合实例不为空
     *
     * @param collection 集合实例
     * @return 结果
     */
    private boolean isNotNullAndEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 将 SnakeYAML 的 CommentLine 列表转换为字符串列表
     *
     * @param comments CommentLine 列表
     * @return 字符串注释列表
     */
    private @NotNull List<String> getCommentLines(@NotNull List<CommentLine> comments) {
        if (this.isNotNullAndEmpty(comments)) return new ArrayList<>();

        List<String> lines = new ArrayList<>();
        for (CommentLine comment : comments) {
            String line = comment.getValue();
            // 如果不是空行, 则去除前导空格, 否则添加 null 以表示空行
            lines.add(comment.getCommentType() != CommentType.BLANK_LINE ?
                    (line.startsWith(" ") ? line.substring(1) : line) : null
            );
        }
        return lines;
    }

    /**
     * 将字符串注释列表转换为 SnakeYAML 的 CommentLine 列表
     *
     * @param comments    字符串注释列表
     * @param commentType 注释类型 (BLOCK, IN_LINE)
     * @return CommentLine 列表
     */
    private @NotNull List<CommentLine> getCommentLines(@NotNull List<String> comments, @NotNull CommentType commentType) {
        if (this.isNotNullAndEmpty(comments)) return new ArrayList<>();

        List<CommentLine> lines = new ArrayList<>();
        for (String comment : comments) {
            // null 或空字符串表示一个空行注释
            if (comment == null) {
                lines.add(new CommentLine(null, null, "", CommentType.BLANK_LINE));
                continue;
            }
            lines.add(new CommentLine(
                    null,
                    null,
                    " " + comment,
                    commentType
            ));
        }
        return lines;
    }

    /**
     * 将 SnakeYAML 的 MappingNode 递归转换为 SectionData 结构
     *
     * @param root MappingNode 根节点
     * @return 转换后的 SectionData
     */
    private @NotNull SectionData mappingNodeToSectionData(@Nullable MappingNode root) {
        Map<String, SectionData> map = new LinkedHashMap<>();
        if (root == null) return new SectionData(map);

        this.constructor.flattenMapping(root);
        for (NodeTuple tuple : root.getValue()) {
            String keyString = String.valueOf(this.constructor.construct(tuple.getKeyNode()));
            Node valueNode = tuple.getValueNode();

            // 处理锚点
            while (valueNode instanceof AnchorNode) {
                valueNode = ((AnchorNode) valueNode).getRealNode();
            }

            SectionData sectionData;
            if (valueNode instanceof MappingNode mappingNode)
                sectionData = this.mappingNodeToSectionData(mappingNode);
            else sectionData = new SectionData(this.constructor.construct(valueNode));

            // 读取注释
            sectionData.setCommentList(this.getCommentLines(tuple.getKeyNode().getBlockComments()));
            if (valueNode instanceof MappingNode || valueNode instanceof SequenceNode)
                sectionData.setInlineCommentList(this.getCommentLines(tuple.getKeyNode().getInLineComments()));
            else sectionData.setInlineCommentList(this.getCommentLines(valueNode.getInLineComments()));


            map.put(keyString, sectionData);
        }

        SectionData data = new SectionData(map);
        data.setCommentList(this.getCommentLines(root.getBlockComments()));
        data.setInlineCommentList(this.getCommentLines(root.getInLineComments()));
        data.setEndCommentList(this.getCommentLines(root.getEndComments()));

        return data;
    }

    /**
     * 将包含 SectionData 的 Map 递归转换为 SnakeYAML 的 MappingNode
     *
     * @param map 包含 SectionData 的 Map
     * @return 转换后的 MappingNode
     */
    private @NotNull MappingNode mapToMappingNode(@NotNull Map<String, SectionData> map) {
        List<NodeTuple> tupleList = new ArrayList<>();

        for (Map.Entry<String, SectionData> entry : map.entrySet()) {
            Node keyNode = this.representer.represent(entry.getKey());
            Node valueNode;

            SectionData sectionData = entry.getValue();
            Object data = sectionData.getData();
            if (data instanceof Map<?, ?> v) // noinspection unchecked
                valueNode = this.mapToMappingNode((Map<String, SectionData>) v);
            else valueNode = this.representer.represent(data);

            // 应用注释
            List<String> commentList = sectionData.getCommentList();
            if (commentList.isEmpty()) keyNode.setBlockComments(null);
            else keyNode.setBlockComments(this.getCommentLines(commentList, CommentType.BLOCK));

            List<String> inlineCommentList = sectionData.getInlineCommentList();
            if (valueNode instanceof MappingNode || valueNode instanceof SequenceNode)
                if (inlineCommentList.isEmpty()) keyNode.setInLineComments(null);
                else keyNode.setInLineComments(this.getCommentLines(inlineCommentList, CommentType.IN_LINE));
            else if (inlineCommentList.isEmpty()) valueNode.setInLineComments(null);
            else valueNode.setInLineComments(this.getCommentLines(inlineCommentList, CommentType.IN_LINE));

            tupleList.add(new NodeTuple(keyNode, valueNode));
        }

        return new MappingNode(Tag.MAP, tupleList, DumperOptions.FlowStyle.BLOCK);
    }
}
