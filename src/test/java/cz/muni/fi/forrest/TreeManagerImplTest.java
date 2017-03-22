package cz.muni.fi.forrest;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * @author Jakub Bohos 422419
 */
public class TreeManagerImplTest {

    private TreeManagerImpl manager;
//    private DataSource ds;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    
/*
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:gravemgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }
*/
/*
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds,TreeManager.class.getResource("createTables.sql"));
        manager = new TreeManagerImpl();
        manager.setDataSource(ds);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,TreeManager.class.getResource("dropTables.sql"));
    }
*/

    private TreeBuilder sampleWilhelmTreeBuilder() {
        return new TreeBuilder()
                .name("Wilhelm")
                .treeType("buk")
                .isProtected(false);
    }

    private TreeBuilder sampleBobTreeBuilder() {
        return new TreeBuilder()
                .name("Bob")
                .treeType("tuja")
                .isProtected(true);
    }


    /*
    @Before
    public void setUp() throws SQLException {
        manager = new TreeManagerImpl();
    }
*/


    @Test
    public void createTree() {
        Tree tree = sampleWilhelmTreeBuilder().build();
        manager.createTree(tree);

        Long treeId = tree.getTreeId();
        assertThat(treeId).isNotNull();

        assertThat(manager.getTree(treeId))
                .isNotSameAs(tree)
                .isEqualToComparingFieldByField(tree);
    }


    @Test
    public void findAllTrees() {

        assertThat(manager.findAllTrees()).isEmpty();

        Tree wilhelm = sampleWilhelmTreeBuilder().build();
        Tree bob = sampleBobTreeBuilder().build();

        manager.createTree(wilhelm);
        manager.createTree(bob);

        assertThat(manager.findAllTrees())
                .usingFieldByFieldElementComparator()
                .containsOnly(wilhelm,bob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullTree() {
        manager.createTree(null);
    }

    @Test
    public void createTreeWithExistingId() {
        Tree tree = sampleWilhelmTreeBuilder()
                .id(1L)
                .build();
        expectedException.expect(IllegalArgumentException.class);
        manager.createTree(tree);
    }

    @Test
    public void createTreeWithNullName() {
        Tree tree = sampleWilhelmTreeBuilder()
                .name(null)
                .build();
        assertThatThrownBy(() -> manager.createTree(tree))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createTreeNullTreeType() {
        Tree tree = sampleWilhelmTreeBuilder()
                .treeType(null)
                .build();
        manager.createTree(tree);
        assertThat(manager.getTree(tree.getTreeId()))
                .isNotNull()
                .isEqualToComparingFieldByField(tree);
    }

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }


    private void testUpdateTree(Operation<Tree> updateOperation) {
        Tree treeForUpdate = sampleWilhelmTreeBuilder().build();
        Tree anotherTree = sampleBobTreeBuilder().build();
        manager.createTree(treeForUpdate);
        manager.createTree(anotherTree);

        updateOperation.callOn(treeForUpdate);

        manager.updateTree(treeForUpdate);
        assertThat(manager.getTree(treeForUpdate.getTreeId()))
                .isEqualToComparingFieldByField(treeForUpdate);
        // Check if updates didn't affected other records
        assertThat(manager.getTree(anotherTree.getTreeId()))
                .isEqualToComparingFieldByField(anotherTree);
    }

    @Test
    public void updateTreeName() {
        testUpdateTree((tree) -> tree.setName("Jozef"));
    }

    @Test
    public void updateTreeType() {
        testUpdateTree((tree) -> tree.setTreeType("jelsa"));
    }

    @Test
    public void updateTreeProtected() {
        testUpdateTree((tree) -> tree.setProtected(true));
    }


    @Test(expected = IllegalArgumentException.class)
    public void updateNullTree() {
        manager.updateTree(null);
    }

    @Test
    public void updateTreeWithNullId() {
        Tree tree = sampleWilhelmTreeBuilder().id(null).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.updateTree(tree);
    }

    @Test
    public void updateNonExistingTree() {
        Tree tree = sampleWilhelmTreeBuilder().id(1L).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.updateTree(tree);
    }

    @Test
    public void updateTreeWithNullName() {
        Tree tree = sampleWilhelmTreeBuilder().build();
        manager.createTree(tree);
        tree.setName(null);

        expectedException.expect(IllegalArgumentException.class);
        manager.updateTree(tree);
    }

    @Test
    public void deleteTree() {

        Tree wilhelm = sampleWilhelmTreeBuilder().build();
        Tree bob = sampleBobTreeBuilder().build();
        manager.createTree(wilhelm);
        manager.createTree(bob);

        assertThat(manager.getTree(wilhelm.getTreeId())).isNotNull();
        assertThat(manager.getTree(bob.getTreeId())).isNotNull();

        manager.deleteTree(wilhelm);

        assertThat(manager.getTree(wilhelm.getTreeId())).isNull();
        assertThat(manager.getTree(bob.getTreeId())).isNotNull();

    }


    @Test(expected = IllegalArgumentException.class)
    public void deleteNullTree() {
        manager.deleteTree(null);
    }

    @Test
    public void deleteTreeWithNullId() {
        Tree tree = sampleWilhelmTreeBuilder().id(null).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteTree(tree);
    }

    @Test
    public void deleteNonExistingTree() {
        Tree tree = sampleWilhelmTreeBuilder().id(1L).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.deleteTree(tree);
    }

/*

    private void testExpectedServiceFailureException(Operation<TreeManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        manager.setDataSource(failingDataSource);
        assertThatThrownBy(() -> operation.callOn(manager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void updateTreeWithSqlExceptionThrown() throws SQLException {
        Tree tree = sampleWilhelmTreeBuilder().build();
        manager.createTree(tree);
        testExpectedServiceFailureException((treeManager) -> treeManager.updateTree(tree));
    }

    @Test
    public void getTreeWithSqlExceptionThrown() throws SQLException {
        Tree tree = sampleBobTreeBuilder().build();
        manager.createTree(tree);
        testExpectedServiceFailureException((treeManager) -> treeManager.getTree(tree.getTreeId()));
    }

    @Test
    public void deleteTreeWithSqlExceptionThrown() throws SQLException {
        Tree tree = sampleWilhelmTreeBuilder().build();
        manager.createTree(tree);
        testExpectedServiceFailureException((treeManager) -> treeManager.deleteTree(tree));
    }

    @Test
    public void findAllTreesWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((treeManager) -> treeManager.findAllTrees());
    }
*/
/*


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
*/

}