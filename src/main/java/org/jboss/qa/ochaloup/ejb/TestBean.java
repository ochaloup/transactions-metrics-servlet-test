package org.jboss.qa.ochaloup.ejb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TestBean {
    private static final String TABLE = "test";

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource ds;

    @Resource
    private EJBContext context;

    @PostConstruct
    public void postConstruct() {
        System.out.println("Post construct now");
        try(Connection conn = ds.getConnection()) {
            selectCount();
        } catch (Exception sqle) {
            try(Connection conn = ds.getConnection()) {
                conn.createStatement().executeUpdate("CREATE TABLE " + TABLE + "(id INT, a VARCHAR(255))");
            } catch (Exception e) {
                throw new RuntimeException("can't create table", sqle);
            }
        }
    }

    public void doCommit() {
        try {
            context.getUserTransaction().begin();
            doInsert();
            context.getUserTransaction().commit();
        } catch (IllegalStateException | NotSupportedException | SystemException
                | SecurityException | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            e.printStackTrace();
            try { 
                context.getUserTransaction().rollback();
            } catch (Exception re) {
                // ignore
            }
            throw new RuntimeException(e);
        }
    }
    
    public void doRollback() {
        try {
            context.getUserTransaction().begin();
            doInsert();
            context.getUserTransaction().rollback();
        } catch (IllegalStateException | NotSupportedException | SystemException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void doTimeout() {
        try {
            context.getUserTransaction().setTransactionTimeout(3);
            context.getUserTransaction().begin();
            doInsert();
            Thread.sleep(7 * 1000);
            context.getUserTransaction().commit();
        } catch (IllegalStateException | NotSupportedException | SystemException
                | SecurityException | RollbackException | HeuristicMixedException | HeuristicRollbackException | InterruptedException e) {
            e.printStackTrace();
            try { 
                context.getUserTransaction().rollback();
            } catch (Exception re) {
                // ignore
            }
            throw new RuntimeException(e);
        }
    }

    public int selectCount() {
        try(Connection conn = ds.getConnection()) {
            ResultSet resultset = conn.createStatement().executeQuery(String.format("SELECT count(1) FROM %s", TABLE));
            if(resultset.next()) {
                return resultset.getInt(1);
            } else {
                return Integer.MIN_VALUE;
            }
        } catch (SQLException sqle) {
            throw new RuntimeException("select failed", sqle);
        }
    }

    private void doInsert() {
        try(Connection conn = ds.getConnection()) {
            int updated = conn.createStatement().executeUpdate(
                    String.format("INSERT INTO %s (id, a) VALUES (%s,'%s')", TABLE, 1, "abc"));
            System.out.println("INSERT to table " + TABLE + " outcome: " + updated);
        } catch (SQLException sqle) {
            throw new RuntimeException("insert failed", sqle);
        }        
    }
}
