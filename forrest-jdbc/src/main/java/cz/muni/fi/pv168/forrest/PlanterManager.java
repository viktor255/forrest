package cz.muni.fi.pv168.forrest;

/**
 * @author Jakub Bohos
 */

import cz.muni.fi.pv168.forrest.common.IllegalEntityException;
import cz.muni.fi.pv168.forrest.common.ServiceFailureException;

import java.util.List;

public interface PlanterManager {

    /**
     * Find pot that contains given tree. If the tree is not placed 
     * in any pot, null is returned.
     *
     * @param tree tree that we want to find
     * @return pot that contains given tree or null if there is no such pot
     * @throws IllegalArgumentException when tree is null.
     * @throws IllegalEntityException when given tree has null id 
     * @throws ServiceFailureException when db operation fails
     */
    Pot findPotWithTree(Tree tree) throws ServiceFailureException, IllegalEntityException;

    /**
     * Find all trees that are placed in given pot.
     *
     * @param pot pot that we want to search
     * @return collection of trees placed in given pot
     * @throws IllegalArgumentException when pot is null
     * @throws IllegalEntityException when given pot has null id 
     * @throws ServiceFailureException when db operation fails
     */
    List<Tree> findTreesInPot(Pot pot) throws ServiceFailureException, IllegalEntityException;

    /**
     * Find all trees that are not placed in any pot. 
     *
     * @return collection of all unburied trees
     * @throws ServiceFailureException when db operation fails.
     */
    List<Tree> findUnplantedTrees() throws ServiceFailureException;

    /**
     * Find all pots that contain no tree. 
     *
     * @return collection of all empty pots
     * @throws ServiceFailureException when db operation fails.
     */
    List<Pot> findEmptyPots() throws ServiceFailureException;

    /**
     * Find all pots that are not full.
     *
     * @return collection of all empty pots
     * @throws ServiceFailureException when db operation fails.
     */
    List<Pot> findPotsWithSomeFreeSpace() throws ServiceFailureException;

    /**
     * Inserts tree into given pot.
     *
     * @param tree tree to be placed to given pot
     * @param pot pot for placing given tree
     * @throws IllegalArgumentException when tree or pot is null
     * @throws IllegalEntityException when tree is already placed in some pot,
     * when pot is already full or when tree or pot have null id or do 
     * not exist in database 
     * @throws ServiceFailureException when db operation fails.
     */
    void putTreeIntoPot(Tree tree, Pot pot) throws ServiceFailureException, IllegalEntityException;

    /**
     * Removes tree from given pot.
     *
     * @param tree tree to be removed from given pot
     * @param pot pot for removing given tree
     * @throws IllegalArgumentException when tree or pot is null
     * @throws IllegalEntityException when given tree is not placed in given 
     * pot or when tree or pot have null id or do not exist in database
     * @throws ServiceFailureException when db operation fails.
     */
    void removeTreeFromPot(Tree tree, Pot pot) throws ServiceFailureException, IllegalEntityException;


}

