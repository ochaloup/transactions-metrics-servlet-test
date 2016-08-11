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
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.jboss.qa.ochaloup.ejb.xa.TestXAResource;
import org.jboss.qa.ochaloup.utils.Database;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class TestBean {
    private static final String TABLE = "test";

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource ds;

    @Resource(mappedName = "java:/jms/queue/DLQ")
    private Queue queueExample;
 
    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory cf;

    @Resource(lookup = "java:jboss/TransactionManager")
    private TransactionManager tm;

    @Resource
    private EJBContext context;

    @PostConstruct
    public void postConstruct() {
        System.out.println("Post construct now: " + TestBean.class.getName());
        try {
            selectCount();
        } catch (Exception sqle) {
            Database.createTable(ds, TABLE);
        }
    }

    public void doCommit() {
        try {
            context.getUserTransaction().begin();
            doInsert();
            sendMessage("hi commit");
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
            sendMessage("hi rollback");
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

    public void doXAFail() {
        try {
            context.getUserTransaction().begin();
            tm.getTransaction().enlistResource(new TestXAResource(TestXAResource.Do.COMMIT_RMFAIL));
            doInsert();
            context.getUserTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("this should not occur as RMFAIL will be managed by TM in recovery phase", e);
        }
    }

    public void doXAFailRmerr() {
        try {
            context.getUserTransaction().begin();
            tm.getTransaction().enlistResource(new TestXAResource(TestXAResource.Do.COMMIT_RMERR));
            doInsert();
            context.getUserTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("RMERR thrown by TestXAResource", e);
        }
    }

    public int selectCount() {
        return Database.selectCount(ds, TABLE);
    }

    public void doInsert() {
        Database.doInsert(ds, TABLE, 1, "abc");
    }

    private void sendMessage(String txt) {
        javax.jms.Connection connection = null;
        try {         
            connection = cf.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer publisher = null;
 
            publisher = session.createProducer(queueExample);
 
            connection.start();
 
            TextMessage message = session.createTextMessage(txt);
            publisher.send(message);
            System.out.println("mesage " + txt + " was sent");
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
        finally {         
            if (connection != null)   {
                try {
                    connection.close();
                } catch (JMSException e) {                    
                    e.printStackTrace();
                }
 
            }
        }
    } 
}
