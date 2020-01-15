package org.xiaofengcanyue.jdbc;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AboutConnection {

    /**
     * Connection接口的setNetworkTimeout方法可以设置此数据库操作时的超时等待时间。
     *
     * Connection接口的abort方法用于强制关闭连接，并释放相关资源。
     * 该方法类似close方法，但close方法一般由连接使用者调用，而abort方法一般由数据库连接的管理者来调用。
     *
     * @throws SQLException
     */
    public void abortConnection() throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:derby://localhost/xiaofengcanyue");
        ThreadPoolExecutor executor = new DebugExecutorService(2,10,60,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
        connection.abort(executor);
        executor.shutdown();
        try{
            executor.awaitTermination(5,TimeUnit.MINUTES);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class DebugExecutorService extends ThreadPoolExecutor{

        public DebugExecutorService(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            System.out.println("清理任务："+r.getClass());
            super.beforeExecute(t, r);
        }

    }


    /**
     * rowset的使用
     * @throws SQLException
     */
    public static void useRowSet() throws SQLException{
        RowSet rs = null;
        RowSetFactory rsFactory = RowSetProvider.newFactory();
        try(JdbcRowSet jrs = rsFactory.createJdbcRowSet()){
            jrs.setUrl("jdbc:derby://localhost/xiaofengcanyue");
            jrs.setCommand("SELECT * FROM book");
            jrs.execute();
            jrs.absolute(1);
            jrs.updateString("name","New book");
            jrs.updateRow();
        }
    }


}
