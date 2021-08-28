package primary.preparedstatement.crud;

import primary.bean.Customer;
import primary.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @ClassName: CustomerForQuery
 * @Description: 针对于Customers表的查询操作
 * @Author: TianXing.Xue
 * @Date: 2021/8/17 10:12
 **/

public class CustomerForQuery {

    @Test
    public void testQueryForCustomer(){
        String sql = "select id,name,birth,email from customers where id = ?";
        Customer customer = queryForCustomer(sql, 13);
        System.out.println(customer);

        sql = "select name,email from customers where name = ?";
        Customer customer1 = queryForCustomer(sql, "周杰伦");
        System.out.println(customer1);


    }

    /*方法描述
     * @author: TianXing.Xue
     * @Description: 针对于Customers表的通用的查询操作
     * @param:
     * @return:
     * @date: 2021/8/21 19:46
     */
    public Customer queryForCustomer(String sql, Object... args) {
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
                Customer cust = new Customer();
                //处理结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {
                    //sql里是从1开始，获取列支
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
//                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //给cust对象指定的columnName属性，赋值为columnValue,通过反射
                    Field field = Customer.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(cust, columnValue);
                }
                return cust;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps, rs);
        }
        return null;
    }

    @Test
    public void testQuery1() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = JDBCUtils.getConnection();

            String sql = "select id,name,email,birth from customers where id = ?";
            ps = connection.prepareStatement(sql);
            ps.setObject(1, 1);

            //执行，并返回结果集
            resultSet = ps.executeQuery();
            //处理结果集
            if (resultSet.next()) { //判断结果集的下一条是否有数据，如果数据返回ture,并指针下移；如果返回false,指针不会下移

                //获取当前这条数据的各个字段的值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                //方式一：
                //            System.out.println("id = "+id+",name = "+name+",email="+email+",birth = "+birth);

                //方式二
                //            Object[] obj = new Object[]{id,name,email,birth};

                //方式三
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(connection, ps, resultSet);
        }
    }
}
