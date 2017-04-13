package cz.muni.fi.forrest;

import cz.muni.fi.forrest.common.DBUtils;
import cz.muni.fi.forrest.common.IllegalEntityException;
import cz.muni.fi.forrest.common.ServiceFailureException;
import cz.muni.fi.forrest.common.ValidationException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanterManagerImpl implements PlanterManager {


    private static final Logger logger = Logger.getLogger(
            PotManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public Pot findPotWithTree(Tree tree) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();
        if (tree == null) {
            throw new IllegalArgumentException("tree is null");
        }
        if (tree.getTreeId() == null) {
            throw new IllegalEntityException("tree id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Pot.potId, row, collumn, capacity, note " +
                            "FROM Pot JOIN Tree ON Pot.potId = Tree.potId " +
                            "WHERE Tree.id = ?");
            st.setLong(1, tree.getTreeId());
            return PotManagerImpl.executeQueryForSinglePot(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find pot with tree " + tree;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Tree> findTreesInPot(Pot pot) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();
        if (pot == null) {
            throw new IllegalArgumentException("pot is null");
        }
        if (pot.getId() == null) {
            throw new IllegalEntityException("pot id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Tree.treeId, name, treeType, isProtected " +
                            "FROM Tree JOIN Pot ON Pot.id = Tree.potId " +
                            "WHERE Pot.id = ?");
            st.setLong(1, pot.getId());
            return TreeManagerImpl.executeQueryForMultipleTrees(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find trees in pot " + pot;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Tree> findUnplantedTrees() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, treeType, isProtected " +
                            "FROM Tree WHERE potId IS NULL");
            return TreeManagerImpl.executeQueryForMultipleTrees(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find unplanted trees";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Pot> findEmptyPots() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Pot.potId, row, column, capacity, note " +
                            "FROM Pot LEFT JOIN Tree ON Pot.id = Tree.potId " +
                            "GROUP BY Pot.id, col, row, capacity, note " +
                            "HAVING COUNT(Tree.id) = 0");
            return PotManagerImpl.executeQueryForMultiplePots(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find empty pots";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Pot> findPotsWithSomeFreeSpace() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Pot.id, row, column, capacity, note " +
                            "FROM Pot LEFT JOIN Tree ON Pot.id = Tree.potId " +
                            "GROUP BY Pot.id, col, row, capacity, note " +
                            "HAVING COUNT(Tree.id) < capacity");
            return PotManagerImpl.executeQueryForMultiplePots(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find pots with some free space";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void putTreeIntoPot(Tree tree, Pot pot) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();
        if (pot == null) {
            throw new IllegalArgumentException("pot is null");
        }
        if (pot.getId() == null) {
            throw new IllegalEntityException("pot id is null");
        }
        if (tree == null) {
            throw new IllegalArgumentException("tree is null");
        }
        if (tree.getTreeId() == null) {
            throw new IllegalEntityException("tree id is null");
        }
        Connection conn = null;
        PreparedStatement updateSt = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            checkIfPotHasSpace(conn, pot);

            updateSt = conn.prepareStatement(
                    "UPDATE Tree SET potId = ? WHERE id = ? AND potId IS NULL");
            updateSt.setLong(1, pot.getId());
            updateSt.setLong(2, tree.getTreeId());
            int count = updateSt.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException("Tree " + tree + " not found or it is already placed in some pot");
            }
            DBUtils.checkUpdatesCount(count, tree, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when putting tree into pot";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, updateSt);
        }
    }

    private static void checkIfPotHasSpace(Connection conn, Pot pot) throws IllegalEntityException, SQLException {
        PreparedStatement checkSt = null;
        try {
            checkSt = conn.prepareStatement(
                    "SELECT capacity, COUNT(Tree.id) as treesCount " +
                            "FROM Pot LEFT JOIN Tree ON Pot.id = Tree.potId " +
                            "WHERE Pot.id = ? " +
                            "GROUP BY Pot.id, capacity");
            checkSt.setLong(1, pot.getId());
            ResultSet rs = checkSt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("capacity") <= rs.getInt("treesCount")) {
                    throw new IllegalEntityException("Pot " + pot + " is already full");
                }
            } else {
                throw new IllegalEntityException("Pot " + pot + " does not exist in the database");
            }
        } finally {
            DBUtils.closeQuietly(null, checkSt);
        }
    }
    
    @Override
    public void removeTreeFromPot(Tree tree, Pot pot) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();
        if (pot == null) {
            throw new IllegalArgumentException("pot is null");
        }
        if (pot.getId() == null) {
            throw new IllegalEntityException("pot id is null");
        }
        if (tree == null) {
            throw new IllegalArgumentException("tree is null");
        }
        if (tree.getTreeId() == null) {
            throw new IllegalEntityException("tree id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in 
            // method DBUtils.closeQuietly(...) 
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Tree SET potId = NULL WHERE id = ? AND potId = ?");
            st.setLong(1, tree.getTreeId());
            st.setLong(2, pot.getId());
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, tree, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when putting tree into pot";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }
}