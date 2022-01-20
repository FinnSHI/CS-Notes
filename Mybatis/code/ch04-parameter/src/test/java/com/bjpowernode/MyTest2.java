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
public class MyTest2 {




    @Test
    public void testQueryStudent(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        List<Student> students = dao.queryStudent("'李四' or id > 0 ");

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }



    @Test
    public void testQueryStudentOrderById(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        List<Student> students = dao.queryStudentOrderById();

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }

    @Test
    public void testQueryStudentOrderByName(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        List<Student> students = dao.queryStudentOrderByName();

        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }

    @Test
    public void testQueryStudentOrderByColName(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        List<Student> students = dao.queryStudentOrderByColName("李四","id","student2");


        students.forEach( stu-> System.out.println("stu="+stu));
        sqlSession.close();

    }

}
