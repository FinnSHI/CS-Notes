package com.bjpowernode;

import com.bjpowernode.domain.Student;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class MyTest {


    //测试mybatis执行sql语句
    @Test
    public void testSelectStudentById() throws IOException {
        //调用mybatis某个对象的方法，执行mapper文件中的sql语句
        //mybatis核心类： SqlSessionFactory

        //1.定义mybatis主配置文件的位置, 从类路径开始的相对路径
        String config="mybatis.xml";
        //2.读取主配置文件。使用mybatis框架中的Resources类
        InputStream inputStream = Resources.getResourceAsStream(config);
        //3.创建SqlSessionFactory对象， 使用SqlSessionFactoryBuidler类
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);

        //4.获取SqlSession对象。
        SqlSession session = factory.openSession();

        //5.指定要执行的sql语句的 id
        //  sql的id = namespace+"."+ select|update|insert|delete标签的id属性值
        String sqlId = "com.bjpowernode.dao.StudentDao"+"."+"selectStudentById";

        // 6.通过SqlSession的方法，执行sql语句
        Student student = session.selectOne(sqlId);
        System.out.println("使用mybatis查询一个学生："+student);

        // 7.关闭SqlSession对象
        session.close();

    }

    //测试mybatis执行sql语句
    @Test
    public void testSelectStudentById2() throws IOException {
        //调用mybatis某个对象的方法，执行mapper文件中的sql语句
        //mybatis核心类： SqlSessionFactory

        //1.定义mybatis主配置文件的位置, 从类路径开始的相对路径
        String config="mybatis.xml";
        //2.读取主配置文件。使用mybatis框架中的Resources类
        InputStream inputStream = Resources.getResourceAsStream(config);
        //3.创建SqlSessionFactory对象， 使用SqlSessionFactoryBuidler类
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);

        //4.获取SqlSession对象。
        SqlSession session = factory.openSession();

        //5.指定要执行的sql语句的 id
        //  sql的id = namespace+"."+ select|update|insert|delete标签的id属性值
        String sqlId = "com.bjpowernode.dao.StudentDao"+"."+"selectStudentById";


        // 6.通过SqlSession的方法，执行sql语句
        Student student = session.selectOne(sqlId,1001);
        System.out.println("使用mybatis查询一个学生："+student);

        // 7.关闭SqlSession对象
        session.close();

    }

    @Test
    public void testInsertStudent() throws IOException {

        String config="mybatis.xml";
        InputStream inputStream = Resources.getResourceAsStream(config);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = factory.openSession();

        //5.指定要执行的sql语句的 id
        //  sql的id = namespace+"."+ select|update|insert|delete标签的id属性值
        String sqlId = "com.bjpowernode.dao.StudentDao"+"."+"insertStudent";

        // 6.通过SqlSession的方法，执行sql语句
        Student student  = new Student();
        student.setId(1006);
        student.setName("张飞");
        student.setEmail("zhangfie@qq.com");
        student.setAge(26);

        int rows = session.insert(sqlId,student);
        System.out.println("使用mybatis添加一个学生，rows="+rows);

        //mybatis默认执行sql语句是  手工提交事务 模式， 在做insert，update ，delete 后需要提交事务。
        session.commit();

        // 7.关闭SqlSession对象
        session.close();

    }


    @Test
    public void testAutoCommitInsertStudent() throws IOException {

        String config="mybatis.xml";
        InputStream inputStream = Resources.getResourceAsStream(config);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = factory.openSession(true);

        //5.指定要执行的sql语句的 id
        //  sql的id = namespace+"."+ select|update|insert|delete标签的id属性值
        String sqlId = "com.bjpowernode.dao.StudentDao"+"."+"insertStudent";

        // 6.通过SqlSession的方法，执行sql语句
        Student student  = new Student();
        student.setId(1007);
        student.setName("小乔");
        student.setEmail("zhangfie@qq.com");
        student.setAge(26);

        int rows = session.insert(sqlId,student);
        System.out.println("使用mybatis添加一个学生，rows="+rows);


        // 7.关闭SqlSession对象
        session.close();

    }
}
