package primary.connection;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @ClassName: Connection
 * @Description: 数据库和JAVA的第一次连接，学习JDBC的第一天
 * @Author: TianXing.Xue
 * @Date: 2021/8/13 17:00
 * @Version: 2.0
 **/
public class ConnectionTest {

    //方式一：
    @Test
    public void testConnection() throws SQLException {
        //获取Driver的实现类对象
        Driver driver = new com.mysql.cj.jdbc.Driver();

        //jdbc：主协议
        //mysql：子协议
        //localhost：ip地址
        //3306：默认mysql端口号
        //myemployees：数据库名字
        String url = "jdbc:mysql://localhost:3306/myemployees";

        //将用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user", "root");  //设置键-值对
        info.setProperty("password", "111111");

        Connection connect = driver.connect(url, info);
        System.out.println(connect);
    }

    //方式二：对方式一的迭代：在如下的程序中不出现第三方的api,使得程序具有更好的可移植性
    @Test
    public void testConnection2() throws Exception {
        //1.获取Driver的实现类对象：使用反射来实现，先得有个大的Class的实例
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();

        //2.提供要连接的数据库
        String url = "jdbc:mysql://localhost:3306/myemployees";

        //3.提供连接需要的用户名和密码
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "111111");

        //4.获取连接
        Connection connect = driver.connect(url, info);
        System.out.println(connect);
    }

    //方式三：使用DriverManger替换Driver
    @Test
    public void testConnection3() throws Exception {

        //1.获取Driver的实现类对象
        Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();

        //2.提供另外三个连接的基本信息
        String url ="jdbc:mysql://localhost:3306/myemployees";
        String user = "root";
        String password = "111111";

        //注册驱动
        DriverManager.registerDriver(driver);

        //获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    //方式四：可以只是加载驱动，不用显示的注册驱动了
    @Test
    public void testConnection4() throws Exception{

        //1.提供另外三个连接的基本信息
        String url ="jdbc:mysql://localhost:3306/myemployees";
        String user = "root";
        String password = "111111";

        //2.加载Driver
        //Driver中的静态代码块完成了注册的操作，静态代码块随着类的加载而加载
        Class.forName("com.mysql.cj.jdbc.Driver");  //甚至这步都可以省略不写！！！！

        /*

        相较于方式三：可以省略如下的操作

        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
        //注册驱动
        DriverManager.registerDriver(driver);


        为什么可以省略上述操作呢？
            在mysql的Driver实现类中，声明了如下的操作

            static {
                try {
                    DriverManager.registerDriver(new Driver());
                } catch (SQLException var1) {
                    throw new RuntimeException("Can't register driver!");
                }
            }


         */

        //3.获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    //方式五(final版)：将数据库连接需要的4个基本信息声明在配置文件中，通过读取配置文件的方式，获取连接
    /*
    *
    *  此种方式的好处？
    * 1.实现了数据与代码的分离，实现了解耦
    * 2.如果需要修改配置文件信息，可以避免程序重新打包
    * */
    @Test
    public void getConnection5() throws Exception {
        //1.读取配置文件中的四个基本信息
        //第一行是为了获得一个类的加载器
        InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
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
        System.out.println(connection);
    }
}
