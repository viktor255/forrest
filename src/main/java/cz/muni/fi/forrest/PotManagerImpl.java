package cz.muni.fi.forrest;

import cz.muni.fi.forrest.common.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Viktor Lehotsky on 14.03.2017.
 */
public class PotManagerImpl implements PotManager {

    public PotManagerImpl() {
    }

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
    public void createPot(Pot pot) {

        checkDataSource();
        validate(pot);
        if (pot.getId() != null) {
            throw new IllegalEntityException("pot id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Pot (row,col,capacity,note) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, pot.getRow());
            st.setInt(2, pot.getColumn());
            st.setInt(3, pot.getCapacity());
            st.setString(4, pot.getNote());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, pot, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            pot.setId(id);
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
    public void updatePot(Pot pot) {

    }

    @Override
    public void deletePot(Pot pot) {

    }

    @Override
    public List<Pot> findAllPots() {

        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, col, row, capacity, note FROM Pot");
            return executeQueryForMultiplePots(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all pots from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Pot findPotById(Long id) {
        return null;
    }


    static Pot executeQueryForSinglePot(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Pot result = rowToPot(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more pots with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Pot> executeQueryForMultiplePots(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Pot> result = new ArrayList<Pot>();
        while (rs.next()) {
            result.add(rowToPot(rs));
        }
        return result;
    }

    private static Pot rowToPot(ResultSet rs) throws SQLException {
        Pot result = new Pot();
        result.setId(rs.getLong("id"));
        result.setColumn(rs.getInt("col"));
        result.setRow(rs.getInt("row"));
        result.setCapacity(rs.getInt("capacity"));
        result.setNote(rs.getString("note"));
        return result;
    }

    private static void validate(Pot pot) {
        if (pot == null) {
            throw new IllegalArgumentException("pot is null");
        }
        if (pot.getRow() < 0) {
            throw new ValidationException("row is negative number");
        }
        if (pot.getColumn() < 0) {
            throw new ValidationException("column is negative number");
        }
        if (pot.getCapacity() <= 0) {
            throw new ValidationException("capacity is not positive number");
        }
    }


}
