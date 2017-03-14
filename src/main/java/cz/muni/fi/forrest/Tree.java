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

    public long getTreeId() {
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

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }
}
