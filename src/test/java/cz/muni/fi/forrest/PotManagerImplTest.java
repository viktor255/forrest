package cz.muni.fi.forrest;

import cz.muni.fi.forrest.common.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;


import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Viktor Lehotsky on 14.03.2017.
 */
public class PotManagerImplTest {

    private PotManagerImpl manager;
    private DataSource ds;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        // we will use in memory database
       // ds.setConnectionAttributes("create=true");
        ds.setDatabaseName("memory:potmgr-test");
        // database is created automatically if it does not exist yet
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, PotManager.class.getClassLoader().getResource("createTables.sql"));

        manager = new PotManagerImpl();
        manager.setDataSource(ds);
    }
    @After
    public void tearDown() throws SQLException {
        // Drop tables after each test
        DBUtils.executeSqlScript(ds,PotManager.class.getResource("dropTables.sql"));
    }

    private PotBuilder sampleSmallPotBuilder() {
        return new PotBuilder()
                .id(null)
                .column(1)
                .row(2)
                .capacity(1)
                .note("Small Pot");
    }

    private PotBuilder sampleBigPotBuilder() {
        return new PotBuilder()
                .id(null)
                .column(2)
                .row(3)
                .capacity(3)
                .note("Big Pot");
    }

    @Test
    public void createPot() throws Exception {

        Pot pot = sampleSmallPotBuilder().build();
        manager.createPot(pot);

        Long potId = pot.getId();
        assertThat(potId).isNotNull();

        assertThat(manager.findPotById(potId))
                .isNotSameAs(pot)
                .isEqualToComparingFieldByField(pot);
    }

    @Test
    public void createPotWithNegativeCapacity() {
        Pot pot = sampleSmallPotBuilder().column(-1).build();
        assertThatThrownBy(() -> manager.createPot(pot)).isInstanceOf(ValidationException.class);
    }

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }

    private void testUpdatePot(Operation<Pot> updateOperation) {
        Pot sourcePot = sampleSmallPotBuilder().build();
        Pot anotherPot = sampleBigPotBuilder().build();
        manager.createPot(sourcePot);
        manager.createPot(anotherPot);

        updateOperation.callOn(sourcePot);

        manager.updatePot(sourcePot);
        assertThat(manager.findPotById(sourcePot.getId()))
                .isEqualToComparingFieldByField(sourcePot);
        // Check if updates didn't affected other records
        assertThat(manager.findPotById(anotherPot.getId()))
                .isEqualToComparingFieldByField(anotherPot);
    }

    @Test
    public void updatePotRow() {
        testUpdatePot((pot) -> pot.setRow(3));
    }

    @Test
    public void updatePotColumn() {
        testUpdatePot((pot) -> pot.setColumn(10));
    }

    @Test
    public void updatePotCapacity() {
        testUpdatePot((pot) -> pot.setCapacity(5));
    }

    @Test
    public void updatePotNote() {
        testUpdatePot((pot) -> pot.setNote("Not so nice pot"));
    }

    @Test
    public void updatePotNoteToNull() {
        testUpdatePot((pot) -> pot.setNote(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullPot() {
        manager.updatePot(null);
    }

    @Test
    public void updatePotWithNullId() {
        Pot pot = sampleSmallPotBuilder().id(null).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.updatePot(pot);
    }

    @Test
    public void updatePotWithNonExistingId() {
        Pot pot = sampleSmallPotBuilder().id(1L).build();
        expectedException.expect(IllegalArgumentException.class);
        manager.updatePot(pot);
    }

    @Test
    public void updatePotWithNegativeColumn() {
        Pot pot = sampleSmallPotBuilder().build();
        manager.createPot(pot);
        pot.setColumn(-1);
        expectedException.expect(ValidationException.class);
        manager.updatePot(pot);
    }

    @Test
    public void updatePotWithNegativeRow() {
        Pot pot = sampleSmallPotBuilder().build();
        manager.createPot(pot);
        pot.setRow(-1);
        expectedException.expect(ValidationException.class);
        manager.updatePot(pot);
    }

    @Test
    public void updatePotWithZeroCapacity() {
        Pot pot = sampleSmallPotBuilder().build();
        manager.createPot(pot);
        pot.setCapacity(0);
        expectedException.expect(ValidationException.class);
        manager.updatePot(pot);
    }

    @Test
    public void updatePotWithNegativeCapacity() {
        Pot pot = sampleSmallPotBuilder().build();
        manager.createPot(pot);
        pot.setCapacity(-1);
        expectedException.expect(ValidationException.class);
        manager.updatePot(pot);
    }

    @Test
    public void deletePot() throws Exception {

        Pot g1 = newPot(2, 3, 6, "Nice pot");
        Pot g2 = newPot(1, 9, 4, "Another record");
        manager.createPot(g1);
        manager.createPot(g2);

        assertNotNull(manager.findPotById(g1.getId()));
        assertNotNull(manager.findPotById(g2.getId()));

        manager.deletePot(g1);

        assertNull(manager.findPotById(g1.getId()));
        assertNotNull(manager.findPotById(g2.getId()));
    }

    @Test
    public void findAllPots() throws Exception {

        assertThat(manager.findAllPots()).isEmpty();

        Pot g1 = sampleSmallPotBuilder().build();
        Pot g2 = sampleBigPotBuilder().build();

        manager.createPot(g1);
        manager.createPot(g2);

        assertThat(manager.findAllPots())
                .usingFieldByFieldElementComparator()
                .containsOnly(g1,g2);

    }
    private static Pot newPot(int column, int row, int capacity, String note) {
        Pot pot = new Pot();
        pot.setColumn(column);
        pot.setRow(row);
        pot.setCapacity(capacity);
        pot.setNote(note);
        return pot;
    }
}