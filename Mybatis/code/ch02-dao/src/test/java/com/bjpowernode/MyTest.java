package com.bjpowernode;

import com.bjpowernode.domain.Student;
import com.bjpowernode.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 *
 */
public class MyTest {


    @Test
    public void testSelectById(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.指定sqlId
        String sqlId="com.bjpowernode.dao.StudentDao.selectById";
        //3.执行SqlSession的方法，表示执行sql语句
        Student student  = session.selectOne(sqlId,1001);
        System.out.println("查询的结果==="+student);
        //4.关闭SqlSession对象
        session.close();
    }

    @Test
    public void testSelectStudents(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.指定sqlId
        String sqlId="com.bjpowernode.dao.StudentDao.selectStudents";
        //3.执行SqlSession的方法，表示执行sql语句
        List<Student> students= session.selectList(sqlId);

        for(Student stu:students){
            System.out.println("student===="+stu);
        }

        //4.关闭SqlSession对象
        session.close();
    }

    @Test
    public void testInsertStudent(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.指定sqlId
        String sqlId="com.bjpowernode.dao.StudentDao.insertStudent";
        //3.执行SqlSession的方法，表示执行sql语句

        Student student  = new Student();
        student.setId(1008);
        student.setName("东皇");
        student.setEmail("donghuang@QQ.com");
        student.setAge(30);
        int rows = session.insert(sqlId, student);
        session.commit();
        System.out.println("insert的行数==="+rows);

        //4.关闭SqlSession对象
        session.close();
    }
}
