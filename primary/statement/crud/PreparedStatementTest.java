package primary.statement.crud;

import primary.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * @ClassName: PreparedStatementTest
 * @Description: 演示使用PreparedStatement来替换Statement, 解决SQL注入问题
 * @Author: TianXing.Xue
 * @Date: 2021/8/22 13:46
 *
 * 除了解决Statement的拼串、sql问题之外，PreparedStatement还有哪些好处？
 * 1.PreparedStatement可以操作Blob的数据，而Statement做不到
 * 2.PreparedStatement可以更高效的批量操作
 **/

public class PreparedStatementTest {
    @Test
    public void testLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名");
        String user = scanner.nextLine();
        System.out.println("请输入密码");
        String password = scanner.nextLine();
        String sql = "select user,password from user_table where user = ? and password = ?";

        User returnUser = getInstance(User.class, sql, user, password);
        if (returnUser != null) {
            System.out.println("登入成功");
        } else {
            System.out.println("用户名不存在或密码输入错误");
        }
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