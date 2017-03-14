package cz.muni.fi.forrest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Jakub Bohos 422419
 */
public class TreeManagerImplTest {

    private TreeManagerImpl manager;

    @Rule
    // attribute annotated with @Rule annotation must be public :-(
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        manager = new TreeManagerImpl();
    }

    @Test
    public void createTree() throws Exception {

        Tree tree = newTree("Jozef", "breza", false);
        manager.createTree(tree);

        Long treeId = tree.getTreeId();
        assertNotNull(treeId);
        Tree result = manager.getTree(treeId);
        assertEquals(tree, result);
        assertNotSame(tree, result);
        assertDeepEquals(tree, result);
    }

    @Test
    public void updateTree() throws Exception {
            Tree tree = newTree("Maria","ceresna", true);
            Tree anotherTree = newTree("Ilona", "dub", false);
            manager.createTree(tree);
            manager.createTree(anotherTree);
            Long treeId = tree.getTreeId();

            tree = manager.getTree(treeId);
            tree.setTreeType("Kveta");
            manager.updateTree(tree);
            assertEquals("Kveta", tree.getName());
            assertEquals("ceresna", tree.getTreeType());
            assertEquals(true, tree.isProtected());

            tree = manager.getTree(treeId);
            tree.setTreeType("buk");
            manager.updateTree(tree);
            assertEquals("Kveta", tree.getName());
            assertEquals("buk", tree.getTreeType());
            assertEquals(true, tree.isProtected());

            tree = manager.getTree(treeId);
            tree.setProtected(false);
            manager.updateTree(tree);
            assertEquals("Kveta", tree.getName());
            assertEquals("buk", tree.getTreeType());
            assertEquals(false, tree.isProtected());

            // Check if updates didn't affected other records
            assertDeepEquals(anotherTree, manager.getTree(anotherTree.getTreeId()));
        }

    @Test
    public void deleteTree() throws Exception {
        Tree t1 = newTree("Jozef", "breza",false);
        Tree t2 = newTree("Juraj", "lipa", false);
        manager.createTree(t1);
        manager.createTree(t2);
        assertNotNull(manager.getTree(t1.getTreeId()));
        assertNotNull(manager.getTree(t2.getTreeId()));
        manager.deleteTree(t1);
        assertNull(manager.getTree(t1.getTreeId()));
        assertNotNull(manager.getTree(t2.getTreeId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTree() {
        manager.createTree(null);
    }

    @Test
    public void updateTreeWithNullName() {
        Tree tree = newTree("Fero", "dub", true);
        manager.createTree(tree);
        tree.setName(null);
        expectedException.expect(IllegalArgumentException.class);
        manager.updateTree(tree);
    }

    @Test
    public void findAllTrees() throws Exception {

        assertTrue(manager.findAllTrees().isEmpty());

        Tree t1 = newTree("Renata","breza", false);
        Tree t2 = newTree("Viktor", "binary", true);

        manager.createTree(t1);
        manager.createTree(t2);

        List<Tree> expected = Arrays.asList(t1, t2);
        List<Tree> actual = manager.findAllTrees();

        Collections.sort(actual, TREE_ID_COMPARATOR);
        Collections.sort(expected, TREE_ID_COMPARATOR);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    private static Tree newTree(String name, String treeType, boolean isProtected) {
        Tree tree = new Tree();
        tree.setName(name);
        tree.setTreeType(treeType);
        tree.setProtected(isProtected);
        return tree;
    }

    private void assertDeepEquals(List<Tree> expectedList, List<Tree> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Tree expected = expectedList.get(i);
            Tree actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Tree expected, Tree actual) {
        assertEquals(expected.getTreeId(), actual.getTreeId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.isProtected(), actual.isProtected());
    }

    private static final Comparator<Tree> TREE_ID_COMPARATOR = Comparator.comparing(Tree::getTreeId);
}