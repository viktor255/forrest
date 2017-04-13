package cz.muni.fi.pv168.forrest;


import java.util.List;

/**
 * @author Jakub Bohos 422419
 */
public interface TreeManager {

    /**
     * Stores new tree into database. Id for the new tree is automatically
     * generated and stored into id attribute.
     *
     * @param tree grave to be created.
     */
    void createTree(Tree tree);

    /**
     * Returns tree with given id.
     *
     * @param id primary key of requested tree.
     */
    Tree getTree(Long id);

    /**
     * Updates tree in database.
     *
     * @param tree updated tree to be stored into database.
     */
    void updateTree(Tree tree);

    /**
     * Deletes tree from database.
     *
     * @param tree tree to be deleted from db.
     */
    void deleteTree(Tree tree);

    /**
     * Returns list of all trees in the database.
     *
     * @return list of all trees in database.
     */
    List<Tree> findAllTrees();

}
