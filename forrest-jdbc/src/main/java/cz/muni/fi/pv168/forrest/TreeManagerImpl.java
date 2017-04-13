package cz.muni.fi.pv168.forrest;

import cz.muni.fi.pv168.forrest.common.DBUtils;
import cz.muni.fi.pv168.forrest.common.IllegalEntityException;
import cz.muni.fi.pv168.forrest.common.ServiceFailureException;
import cz.muni.fi.pv168.forrest.common.ValidationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jakub Bohos 422419
 */
public class TreeManagerImpl implements TreeManager {

    private static final Logger logger = Logger.getLogger(
            TreeManagerImpl.class.getName());

    private DataSource dataSource;

    public TreeManagerImpl() {
    }

    public TreeManagerImpl(DataSource dataSource) {

    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createTree(Tree tree) {
        checkDataSource();
        validate(tree);
        if (tree.getTreeId() != null) {
            throw new IllegalEntityException("tree id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Tree (name,treeType,isProtected) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, tree.getName());
            st.setString(2, tree.getTreeType());
            st.setBoolean(3, tree.isProtected());
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, tree, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            tree.setTreeId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting pot into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Tree getTree(Long id) throws ServiceFailureException {

        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, treeType, isProtected FROM Tree WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleTree(st);
        } catch (SQLException ex) {
            String msg = "Error when getting tree with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateTree(Tree tree) throws ServiceFailureException {
        checkDataSource();
        validate(tree);

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
                    "UPDATE Tree SET name = ?, treeType = ?, isProtected = ? WHERE id = ?");
            st.setString(1, tree.getName());
            st.setString(2, tree.getTreeType());
            st.setInt(3, tree.isProtected()?1:0);
            st.setLong(4, tree.getTreeId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, tree, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating tree in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteTree(Tree tree) throws ServiceFailureException {
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
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Tree WHERE id = ?");
            st.setLong(1, tree.getTreeId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, tree, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting tree from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    static Tree executeQueryForSingleTree(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Tree result = rowToTree(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more trees with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }


    static List<Tree> executeQueryForMultipleTrees(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Tree> result = new ArrayList<Tree>();
        while (rs.next()) {
            result.add(rowToTree(rs));
        }
        return result;
    }

    static private Tree rowToTree(ResultSet rs) throws SQLException {
        Tree result = new Tree();
        result.setTreeId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setTreeType(rs.getString("treeType"));

        // This is the proper way, how to handle LocalDate, however it is not
        // supported by Derby yet - see https://issues.apache.org/jira/browse/DERBY-6445
        //result.setBorn(rs.getObject("born", LocalDate.class));
        //result.setDied(rs.getObject("died", LocalDate.class));

        result.setProtected(rs.getInt("isProtected") != 0);
        return result;
    }




    @Override
    public List<Tree> findAllTrees() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, treeType, isProtected FROM Tree");
            return executeQueryForMultipleTrees(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all trees from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    private void validate(Tree tree) {
        if (tree == null) {
            throw new ValidationException("tree is null");
        }
        if (tree.getName() == null) {
            throw new ValidationException("name is null");
        }
        if (tree.getTreeType() == null) {
            throw new ValidationException("treeType is null");
        }
    }

}
