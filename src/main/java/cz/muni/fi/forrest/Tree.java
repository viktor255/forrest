package cz.muni.fi.forrest;

/**
 * @author Jakub Bohos 422419
 */
public class Tree {
    private Long treeId;
    private String name;
    private String treeType;
    private boolean isProtected;

    public Tree() {
    }

    public Tree(Long treeId, String name, String treeType, boolean isProtected) {
        this.treeId = treeId;
        this.name = name;
        this.treeType = treeType;
        this.isProtected = isProtected;
    }

    public Long getTreeId() {
        return treeId;
    }

    public String getName() {
        return name;
    }

    public String getTreeType() {
        return treeType;
    }

    public void setTreeId(Long id) {
        this.treeId = treeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }
}
