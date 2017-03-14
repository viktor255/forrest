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
 * @author Viktor Lehotsky on 14.03.2017.
 */
public class PotManagerImplTest {

    private PotManagerImpl manager;

    @Rule
    // attribute annotated with @Rule annotation must be public
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        manager = new PotManagerImpl();
    }

    @Test
    public void createPot() throws Exception {

        Pot pot = newPot(3, 11, 2, "Nice one pot");
        manager.createPot(pot);
        Long potId = pot.getId();
        assertNotNull(potId);
        Pot result = manager.findPotById(potId);
        assertEquals(pot, result);
        assertNotSame(pot, result);
        assertDeepEquals(pot, result);
    }

    @Test
    public void createPotWithNegativeCapacity() {
        Pot pot = newPot(1, 1, -1, "Negative capacity pot");
        expectedException.expect(IllegalArgumentException.class);
        manager.createPot(pot);
    }


    @Test
    public void updatePot() throws Exception {

        Pot pot = newPot(1, 3, 6, "Cool pot");
        Pot anotherPot = newPot(2, 5, 3, "Another pot");
        manager.createPot(pot);
        manager.createPot(anotherPot);
        Long potId = pot.getId();

        pot = manager.findPotById(potId);
        pot.setColumn(0);
        pot.setRow(0);
        pot.setCapacity(1);
        pot.setNote("Zero pot");
        manager.updatePot(pot);
        assertEquals(0, pot.getColumn());
        assertEquals(0, pot.getRow());
        assertEquals(1, pot.getCapacity());
        assertEquals("Zero pot", pot.getNote());

        // Check if updates didn't affected other records
        assertDeepEquals(anotherPot, manager.findPotById(anotherPot.getId()));

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

        assertTrue(manager.findAllPots().isEmpty());

        Pot g1 = newPot(23, 44, 5, "Pot 1");
        Pot g2 = newPot(12, 4, 1, "Pot 2");

        manager.createPot(g1);
        manager.createPot(g2);

        List<Pot> expected = Arrays.asList(g1, g2);
        List<Pot> actual = manager.findAllPots();

        Collections.sort(actual, POT_ID_COMPARATOR);
        Collections.sort(expected, POT_ID_COMPARATOR);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);

    }

    private static Pot newPot(int column, int row, int capacity, String note) {
        Pot pot = new Pot();
        pot.setColumn(column);
        pot.setRow(row);
        pot.setCapacity(capacity);
        pot.setNote(note);
        return pot;
    }

    private void assertDeepEquals(List<Pot> expectedList, List<Pot> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Pot expected = expectedList.get(i);
            Pot actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Pot expected, Pot actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getColumn(), actual.getColumn());
        assertEquals(expected.getRow(), actual.getRow());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getNote(), actual.getNote());
    }

    private static final Comparator<Pot> POT_ID_COMPARATOR =
            Comparator.comparing(Pot::getId);

}