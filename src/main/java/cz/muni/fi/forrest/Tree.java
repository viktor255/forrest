package cz.muni.fi.forrest;

import java.util.Objects;

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
        this.treeId = id;
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

    public String toString() {
        return "Tree{"
                + "id=" + treeId
                + ", name=" + name
                + ", treeType=" + treeType
                + ", isProtected=" + isProtected
                + '}';
    }

    /**
     * Returns true if obj represents the same tree. Two objects are considered
     * to represent the same body when both are instances of {@link Tree} class,
     * both have assigned some id and this id is the same.
     *
     *
     * @param obj the reference object with which to compare.
     * @return true if obj represents the same body.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tree other = (Tree) obj;
        if (obj != this && this.treeId == null) {
            return false;
        }
        return Objects.equals(this.treeId, other.treeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.treeId);
    }

}
