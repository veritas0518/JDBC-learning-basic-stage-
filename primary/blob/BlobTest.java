package primary.blob;

import primary.bean.Customer;
import primary.util.JDBCUtils;
import org.junit.Test;

import java.io.*;
import java.sql.*;

/**
 * @ClassName: BlobTest
 * @Description: 测试使用PreparedStatement操作Blob类型的数据
 * @Author: TianXing.Xue
 * @Date: 2021/8/22 18:41
 **/

public class BlobTest {

    //向数据表customers中插入Blob类型的字段
    @Test
    public void testInsert() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        String sql = "insert into customers(name,email,birth,photo)values(?,?,?,?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setObject(1, "张晓三");
        ps.setObject(2, "zhangxiaosan@qq.com");
        ps.setObject(3, "1992-09-08");
        FileInputStream is = new FileInputStream(new File("友情与爱情.jpg"));
        ps.setBlob(4, is);

        ps.execute();

        JDBCUtils.closeResource(connection, ps);
    }

    //查询数据表Customer中Blob字段
    @Test
    public void testQuery() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            connection = JDBCUtils.getConnection();
            String sql = "select id,name,email,birth,photo from customers where id = ?";
            ps = connection.prepareStatement(sql);

            ps.setObject(1, 16);
            rs = ps.executeQuery();
            if (rs.next()) {

                //方式一：
                //            int id = rs.getInt(1);
                //            String name = rs.getString(2);
                //            String email = rs.getString(3);
                //            Date birth = rs.getDate(4);

                //方式二：
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Date birth = rs.getDate("birth");

                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);

                //将Blob类型的字段下载下来，以文件的方式保存在本地
                Blob photo = rs.getBlob("photo");
                is = photo.getBinaryStream();
                fos = new FileOutputStream("友情与爱情.jpg");
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        JDBCUtils.closeResource(connection, ps, rs);
    }
}

