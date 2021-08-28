package primary.preparedstatement.crud;

import primary.bean.Customer;
import primary.bean.Order;
import primary.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: PreparedStatementQueryTest
 * @Description: 使用PreparedStatement实现针对于不同表的通用的查询操作
 * @Author: TianXing.Xue
 * @Date: 2021/8/22 11:28
 **/

public class PreparedStatementQueryTest {

    @Test
    public void testGetForList() {
        String sql = "select id , name,email from customers where id < ?";
        List<Customer> list = getForList(Customer.class, sql, 12);
        list.forEach(System.out::println);

        String sql1 = "select order_id orderId , order_name orderName from `order` where order_id < ?";
        List<Order> orderList = getForList(Order.class, sql1, 5);
        orderList.forEach(System.out::println);

    }


    public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtils.getConnection();
            ps = connection.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过结果集的元数据(ResultSetMetaData)来获取结果集当中的列数
            int columnCount = rsmd.getColumnCount();
            //创建集合对象
            ArrayList<T> list = new ArrayList<>();

            while (rs.next()) {
                T t = clazz.getDeclaredConstructor().newInstance();
                //处理结果集一行数据中的每一个列：给t对象指定的属性赋值
                for (int i = 0; i < columnCount; i++) {
                    //sql里是从1开始，获取列支
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //给cust对象指定的columnName属性，赋值为columnValue,通过反射
                    //这里注意是clazz，即当前的T类
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps, rs);
        }
        return null;
    }


    @Test
    public void testGetInstance() {
        String sql = "select id , name,email from customers where id = ?";
        Customer instance = getInstance(Customer.class, sql, 12);
        System.out.println(instance);

        String sql1 = "select order_id orderId , order_name orderName from `order` where order_id = ?";
        Order instance1 = getInstance(Order.class, sql1, 1);
        System.out.println(instance1);
    }

    /*方法描述
     * @author: TianXing.Xue
     * @Description: 针对于不同的表的通用的查询操作，返回表中的一条记录
     * @param: <T> T getInstance:表示这是泛型方法
     * @return:
     * @date: 2021/8/22 11:41
     */
    public <T> T getInstance(Class<T> clazz, String sql, Object... args) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtils.getConnection();
            ps = connection.prepareStatement(sql);

            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过结果集的元数据(ResultSetMetaData)来获取结果集当中的列数
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                T t = clazz.getDeclaredConstructor().newInstance();
                //处理结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {
                    //sql里是从1开始，获取列支
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //给cust对象指定的columnName属性，赋值为columnValue,通过反射
                    //这里注意是clazz，即当前的T类
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps, rs);
        }
        return null;
    }
}
