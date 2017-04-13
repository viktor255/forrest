package cz.muni.fi.pv168.forrest.common;

import cz.muni.fi.pv168.forrest.Tree;
import cz.muni.fi.pv168.forrest.TreeManager;
import cz.muni.fi.pv168.forrest.TreeManagerImpl;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Viktor Lehotsky on 12.04.2017.
 */
public class Main {
    final static Logger log = LoggerFactory.getLogger(Main.class);

    public static DataSource createMemoryDatabase() {
        BasicDataSource bds = new BasicDataSource();
        //set JDBC driver and URL
        bds.setDriverClassName(EmbeddedDriver.class.getName());
        bds.setUrl("jdbc:derby:memory:forrestDB;create=true");
        //populate db with tables and data
        new ResourceDatabasePopulator(
                new ClassPathResource("schema-javadb.sql"),
                //.execute(bds);
                new ClassPathResource("test-data.sql"))
                .execute(bds);
        return bds;
    }

    public static void main(String[] args) throws Exception {

        log.info("zaciname");

        DataSource dataSource = createMemoryDatabase();
        TreeManager treeManager = new TreeManagerImpl();

        List<Tree> allTrees = treeManager.findAllTrees();
        System.out.println("allTrees = " + allTrees);

    }
}
