package cz.muni.fi.forrest;

/**
 * @author Jakub Bohos 422419
 */
public class TreeBuilder {

    private Long treeId;
    private String name;
    private String treeType;
    private boolean isProtected;

    public TreeBuilder id(Long id) {
        this.treeId = id;
        return this;
    }

    public TreeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TreeBuilder treeType(String treeType) {
        this.treeType = treeType;
        return this;
    }

    public TreeBuilder isProtected(boolean isProtected) {
        this.isProtected = isProtected;
        return this;
    }

    /**
     * Creates new instance of {@link Tree} with configured properties.
     *
     * @return new instance of {@link Tree} with configured properties.
     */
    public Tree build() {
        Tree tree = new Tree();
        tree.setTreeId(treeId);
        tree.setName(name);
        tree.setTreeType(treeType);
        tree.setProtected(isProtected);
        return tree;
    }
}
