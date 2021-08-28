package primary.blob;

import primary.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @ClassName: InsertTest
 * @Description: 使用PreparedStatement实现批量数据的操作
 * @Author: TianXing.Xue
 * @Date: 2021/8/23 9:44
 *
 *  update delete本身就具有批量操作的效果
 *  此时的批量操作主要指的是批量插入，使用PreparedStatement如何实现更高效的批量插入
 *
 *  题目：向goods表中插入20000条数据
 *      CREATE TABLE goods(
 * 	        id INT PRIMARY KEY auto_increment,
 * 	        NAME VARCHAR(25)
 *      );
 *  方式一：使用Statement
 *  Connection connection = JDBCUtils.getConnection();
 *  Statement st = connection.createStatement();
 *  for(int i =1;i<20000;i++){
 *      String sql = "insert into gods(name) values('name_"+i+"')"
 *      st.execute(sql);
 *  }
 *
 **/

public class InsertTest {

    //批量插入的方式二：
    @Test
    public void testInsert1() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            connection = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = connection.prepareStatement(sql);

            for (int i = 1; i <=20000; i++) {
                ps.setObject(1, "name_" + i);
                ps.execute();
            }

            long end = System.currentTimeMillis();
            System.out.println("花费的时间为："+(end-start));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps);
        }

    }

    /*
    *  批量插入的方式三：
    *  1.addBatch()、executeBatch()、clearBatch()
    *  2.mysql服务器默认是关闭处理的，我们需要通过一个参数，让mysql开启批处理的支持
    *       &&rewriteBatchedStatements=true
    *  3.使用更新的mysql 驱动：mysql-connector-java
    *
    * */

    @Test
    public void testInsert2() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            connection = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = connection.prepareStatement(sql);

            for (int i = 1; i <=1000000; i++) {
                ps.setObject(1, "name_" + i);

                //1."攒"sql
                ps.addBatch();

                if (i % 500 == 0) {
                    //2.执行batch
                    ps.executeBatch();

                    //3.清空batch
                    ps.clearBatch();
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("花费的时间为：" + (end - start));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps);
        }
    }

    /*
    *   ☆批量插入的方式四：设置连接不允许自动提交数据。（最推荐的）
    * */
    @Test
    public void testInsertFinal() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();

            connection = JDBCUtils.getConnection();

            //设置不允许自动提交数据
            connection.setAutoCommit(false);

            String sql = "insert into goods(name) values(?)";
            ps = connection.prepareStatement(sql);

            for (int i = 1; i <= 1000000; i++) {
                ps.setObject(1, "name_" + i);

                //1."攒"sql
                ps.addBatch();

                if (i % 1000 == 0) {
                    //2.执行batch
                    ps.executeBatch();

                    //3.清空batch
                    ps.clearBatch();
                }
            }

            //提交数据
            connection.commit();

            long end = System.currentTimeMillis();
            System.out.println("花费的时间为：" + (end - start));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps);
        }
    }
}
