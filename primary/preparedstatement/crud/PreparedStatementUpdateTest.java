package primary.preparedstatement.crud;

import primary.util.JDBCUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @ClassName: PreparedStatementUpdateTest
 * @Description: 使用PreparedStatement替换Statement，实现对数据表的增删改查操作
 * @Author: TianXing.Xue
 * @Date: 2021/8/15 23:10
 **/

public class PreparedStatementUpdateTest {

    //向customer表中添加一条记录
    @Test
    public void testInsert() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            //1.获得一个系统类的加载器
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(is);

            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            String driverClass = properties.getProperty("driverClass");

            //2.加载驱动
            Class.forName(driverClass);

            //3.获取连接
            connection = DriverManager.getConnection(url, user, password);

            //4.预编译sql语句，返回PreparedStatement
            String sql = "insert into customers(name,email,birth)values(?,?,?)"; //占位符
            ps = connection.prepareStatement(sql);

            //5.填充占位符
            ps.setString(1, "哪吒");
            ps.setString(2, "nezha@gmail.com");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse("1422-01-01");
            ps.setDate(3, new java.sql.Date(date.getTime()));

            //6.执行操作
            ps.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                //7.资源关闭
                try {
                    ps.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    //修改customers表的一条记录
    @Test
    public void testUpdate() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库的连接
            connection = JDBCUtils.getConnection();
            //2.预编译sql语句，返回PreparedStatement的实例
            String sql = "update customers set name =? where id =?";
            ps = connection.prepareStatement(sql);
            //3.填充占位符
            ps.setObject(1, "莫扎特");
            ps.setObject(2, 18);
            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.资源的关闭
            JDBCUtils.closeResource(connection, ps);
        }
    }

    @Test
    public void testCommonUpdate() {
//        String sql="delete from customers where id =?";
//        update(sql,3);
        String sql = "update `order` set order_name = ? where order_id= ?";
        update(sql, "DD", "2");

    }

    //通用的增删改操作
    public void update(String sql, Object... args) { //sql中占位符的个数与可变形参的长度相同
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            //1.获取数据库的连接
            connection = JDBCUtils.getConnection();
            //2.预编译sql语句，返回PreparedStatement的实例
            ps = connection.prepareStatement(sql);
            //3.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]); //小心参数声明错误
            }
            //4.执行
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭
            JDBCUtils.closeResource(connection, ps);
        }
    }

}


