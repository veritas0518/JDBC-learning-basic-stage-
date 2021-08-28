package primary.preparedstatement.crud;

import primary.bean.Order;
import primary.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @ClassName: OrderForQuery
 * @Description: 针对于Order表的通用的查询操作
 * @Author: TianXing.Xue
 * @Date: 2021/8/21 22:04
 **/

public class OrderForQuery {

 /*
 *  针对于表的字段名与类的属性名不相同的情况：
 *    1.必须声明sql时，使用类的属性名来命名字段的别名
 *    2.使用ResultSetMetaData时，需要使用getColumnLabel()来替换getColumnName()，来获取列的别名
 *
 *      说明：如果sql中没有给字段其别名，getColumnLabel()获取的就是列名
 *
 *
 *
 * */
    @Test
    public void testOrderForQuery(){
        String sql  = "select order_id orderId ,order_name orderName ,order_date orderDate from `order` where order_id = ?";
        Order order = orderForQuery(sql, 1);
        System.out.println(order);
    }

    /*方法描述
    * @author: TianXing.Xue
    * @Description: 通用的，针对于order表的查询操作
    * @param:
    * @return:
    * @date: 2021/8/21 22:14
    */
    public Order orderForQuery(String sql, Object ...args){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtils.getConnection();
            ps = connection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                //填充占位符
                ps.setObject(i+1,args[i]);
            }

            //执行，获取结果集
            rs = ps.executeQuery();
            //获取结果集的元数据

            ResultSetMetaData rsmd = rs.getMetaData();
            //获取列数
            int columnCount = rsmd.getColumnCount();

            if(rs.next()){
               Order order = new Order();
                for (int i = 0; i < columnCount; i++) {
                    //获取每个列的列值：通过ResultSet
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的列名：通过ResultSetMetaData
                    //获取列的列名：getColumnName()  ---不推荐使用
                    //获取列的别名：getColumnLabel()
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //通过反射将对象指定名的属性赋值为指定的值
                    Field field = Order.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(order,columnValue);

                }
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection,ps,rs);
        }

        return null;
    }





    @Test
    public void testQuery1() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtils.getConnection();
            String sql = "select order_id,order_name,order_date from `order` where  order_id = ?";
            ps = connection.prepareStatement(sql);

            ps.setObject(1, 1);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = (int) rs.getObject(1);
                String name = (String) rs.getObject(2);
                Date date = (Date) rs.getObject(3);

                Order order = new Order(id, name, date);
                System.out.println(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps, rs);
        }
    }
}
