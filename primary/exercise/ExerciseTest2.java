package primary.exercise;

import primary.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * @ClassName: ExerciseTest2
 * @Description: 课后练习2
 * @Author: TianXing.Xue
 * @Date: 2021/8/22 15:45
 **/

public class ExerciseTest2 {
    @Test
    public void testInsert() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("四级/六级");
        int type = scanner.nextInt();
        System.out.println("请输入身份证号：");
        String IDCard = scanner.next();
        System.out.println("请输入准考证号：");
        String examCard = scanner.next();
        System.out.println("请输入学生姓名：");
        String studentName = scanner.next();
        System.out.println("请输入所在城市");
        String location = scanner.next();
        System.out.println("请输入学生成绩");
        int grade = scanner.nextInt();

        String sql = "insert into examstudent(type,IDCard,examCard,studentName,location,grade) values(?,?,?,?,?,?)";
        int insertCount = update(sql, type, IDCard, examCard, studentName, location, grade);
        if (insertCount > 0) {
            System.out.println("添加成功");
        } else {
            System.out.println("添加失败");
        }
    }

    //问题1：向examstudent表中添加一条记录
    //通用的增删改操作
    public int update(String sql, Object... args) { //sql中占位符的个数与可变形参的长度相同

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
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.关闭
            JDBCUtils.closeResource(connection, ps);
        }
        return 0;
    }

    //问题2：根据身份证号或者准考证号查询学生成绩信息
    @Test
    public void queryWithIDCardOrExamCard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请选择您要输入的类型");
        System.out.println("a.准考证号");
        System.out.println("b.身份证号");
        String selection = scanner.next();
        //下面这样写是为了避免空指针异常
        if ("a".equalsIgnoreCase(selection)) {
            System.out.println("请输入准考证号：");
            String examCard = scanner.next();
            String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard," +
                    "StudentName name,Location location,Grade grade from examstudent where examCard = ?";

            Student student = getInstance(Student.class, sql, examCard);
            if (student != null) {
                System.out.println(student);
            } else {
                System.out.println("输入的准考证号有误");
            }

        } else if ("b".equalsIgnoreCase(selection)) {
            System.out.println("请输入身份证号：");
            String IDCard = scanner.next();
            String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard," +
                    "StudentName name,Location location,Grade grade from examstudent where IDCard = ?";

            Student student = getInstance(Student.class, sql, IDCard);
            if (student != null) {
                System.out.println(student);
            } else {
                System.out.println("输入的身份证号有误");
            }

        } else {
            System.out.println("您的输入有误，请重新进入程序");
        }
    }

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

    //问题3：删除指定的学生信息
    @Test
    public void testDeleteByExamCard() {
        System.out.println("请输入学生的考号：");
        Scanner scanner = new Scanner(System.in);
        String examCard = scanner.next();
        //查询指定准考证号的学生
        String sql = "select FlowID flowID,Type type,IDCard,ExamCard examCard," +
                "StudentName name,Location location,Grade grade from examstudent where examCard = ?";

        Student student = getInstance(Student.class, sql, examCard);
        if (student == null) {
            System.out.println("查无此人，请重新输入");
        } else {
            String sql1 = "delete from examstudent where examCard = ?";
            int deleteCount = update(sql1, student);
            if (deleteCount > 0) {
                System.out.println("删除成功");
            }
        }
    }

    //优化以后的操作
    @Test
    public void testDeleteByExamCard1() {
        System.out.println("请输入学生的考号：");
        Scanner scanner = new Scanner(System.in);
        String examCard = scanner.next();
        String sql = "delete from examstudent where examCard = ?";

        int deleteCount = update(sql, examCard);
        if (deleteCount > 0) {
            System.out.println("删除成功");
        } else {
            System.out.println("查无此人");
        }
    }
}
