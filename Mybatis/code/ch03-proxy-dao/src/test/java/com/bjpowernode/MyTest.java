package com.bjpowernode;

import com.bjpowernode.dao.StudentDao;
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
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);
        Student student = dao.selectById(1005);
        System.out.println("student = " + student);
        //3.关闭SqlSession对象
        session.close();
    }

    @Test
    public void testSelectStudets(){
        SqlSession session = MyBatisUtil.getSqlSession();
        StudentDao dao  = session.getMapper(StudentDao.class);
        //com.sun.proxy.$Proxy ==StudentDaoImpl
        System.out.println("dao==="+dao.getClass().getName());
        List<Student> students = dao.selectStudents();
        students.forEach( stu-> System.out.println("stu="+stu));
        session.close();

    }

}
