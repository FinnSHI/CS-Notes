package com.bjpowernode;

import com.bjpowernode.dao.ProvinceDao;
import com.bjpowernode.dao.StudentDao;
import com.bjpowernode.domain.Student;
import com.bjpowernode.utils.MyBatisUtil;
import com.bjpowernode.vo.CustomObject;
import com.bjpowernode.vo.ProvinceCity;
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
    public void testSelectById2(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);
        CustomObject co  = dao.selectById2(1001);
        System.out.println("CustomObject = " + co);
        //3.关闭SqlSession对象
        session.close();
    }

    @Test
    public void testSelectById3(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);
        CustomObject co  = dao.selectById3(1005);
        System.out.println("CustomObject = " + co);
        //3.关闭SqlSession对象
        session.close();
    }

    @Test
    public void testCountStudent(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);
        long nums  = dao.countStudent();
        System.out.println("nums = " + nums);
        //3.关闭SqlSession对象
        session.close();
    }


    @Test
    public void testSelectMap(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);

        Map<Object,Object> map  = dao.selectMap(1006);
        System.out.println("map==="+map);
        //3.关闭SqlSession对象
        session.close();


        System.out.println("name="+map.get("name"));
        System.out.println("id="+map.get("id"));
    }

    @Test
    public void testSelecProvinceCity(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        ProvinceDao dao  = session.getMapper(ProvinceDao.class);

        List<ProvinceCity> list = dao.selectProvinceCityList(1);

        session.close();

        list.forEach( p-> System.out.println(p));


    }


    @Test
    public void testLikeOne(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        String name="%李%";
        List<Student> students = dao.selectLikeOne(name);

        sqlSession.close();

        students.forEach( stu-> System.out.println(stu));
    }


    @Test
    public void testLikeTwo(){
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        StudentDao dao  = sqlSession.getMapper(StudentDao.class);

        String name="李";
        List<Student> students = dao.selectLikeTwo(name);

        sqlSession.close();

        students.forEach( stu-> System.out.println(stu));
    }


}
