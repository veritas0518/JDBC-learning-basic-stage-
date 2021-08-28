package primary.util;


import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName: JDBCutils
 * @Description: 操作数据库的工具类
 * @Author: TianXing.Xue
 * @Date: 2021/8/16 11:07
 **/

public class JDBCUtils {
    /*方法描述
     * @author: TianXing.Xue
     * @Description: 获取数据库的连接
     * @param:
     * @return:
     * @date: 2021/8/16 11:10
     */
    public static Connection getConnection() throws Exception {
        //1.读取配置文件中的四个基本信息
        //第一行是为了获得一个类的加载器
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(is);

        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String driverClass = properties.getProperty("driverClass");

        //2.加载驱动
        Class.forName(driverClass);

        //3.获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    /*方法描述
     * @author: TianXing.Xue
     * @Description: 关闭连接和Statement的操作
     * @param:
     * @return:
     * @date: 2021/8/16 11:13
     */
    public static void closeResource(Connection connection, Statement ps) {
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

    /*方法描述
     * @author: TianXing.Xue
     * @Description:关闭资源的操作
     * @param:
     * @return:
     * @date: 2021/8/21 18:31
     */
    public static void closeResource(Connection connection, Statement ps, ResultSet rs) {
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
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
