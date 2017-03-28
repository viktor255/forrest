package cz.muni.fi.forrest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
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
            throw new IllegalArgumentException("tree id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Body (name,treeType,isProtected) VALUES (?,?,?)",
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
            String msg = "Error when inserting tree into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Tree getTree(Long id) {
        return null;
    }

    @Override
    public void updateTree(Tree tree) {
    }

    @Override
    public void deleteTree(Tree tree) {
    }

    @Override
    public List<Tree> findAllTrees() {
        return null;
    }

    private void validate(Tree tree) {
        if (tree == null) {
            throw new IllegalArgumentException("tree is null");
        }
        if (tree.getName() == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (tree.getTreeType() == null) {
            throw new IllegalArgumentException("treeType is null");
        }
    }

}
