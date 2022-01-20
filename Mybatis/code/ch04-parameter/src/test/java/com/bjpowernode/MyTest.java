package com.bjpowernode;

import com.bjpowernode.dao.StudentDao;
import com.bjpowernode.domain.Student;
import com.bjpowernode.utils.MyBatisUtil;
import com.bjpowernode.vo.QueryParam;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void testOneParameter(){
        SqlSession sqlSession  = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);
        Student student = dao.selectByEmail("lifeng@qq.com");
        System.out.println("email==="+student);
        sqlSession.close();
    }

    @Test
    public void testSelectByNameOrAge(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        List<Student> students = dao.selectByNameOrAge("李四", 26);

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }


    @Test
    public void testSelectBObject(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        Student student  = new Student();
        student.setName("李思思");
        student.setAge(22);
        student.setEmail("小乔");
        List<Student> students = dao.selectByObject(student);

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }

    @Test
    public void testSelectBObject2(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        QueryParam param = new QueryParam();
        param.setP1("东皇");
        param.setP2(20);
        List<Student> students = dao.selectByQueryParam(param);

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }


    @Test
    public void testSelectByPosition(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        //按位置传递参数
        List<Student> students = dao.selectByPosition("李四", 20);

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }

    @Test
    public void testSelectByMap(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        //使用map传递参数
        Map<String,Object> data = new HashMap<>();
        data.put("myname", "李思思");
        data.put("myage", 20);
        List<Student> students = dao.selectStudentByMap(data);

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }

    @Test
    public void testUpdateStudent(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        Student student  = new Student();
        student.setId(1003);
        student.setName("张峰");
        student.setEmail("ZHANGFENG@qq.com");
        student.setAge(20);
        int rows = dao.updateStudent(student);
        sqlSession.commit();
        System.out.println("更新学生的rows="+rows);
        sqlSession.close();

    }



}
