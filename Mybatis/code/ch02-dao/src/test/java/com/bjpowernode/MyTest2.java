package com.bjpowernode;

import com.bjpowernode.dao.StudentDao;
import com.bjpowernode.dao.impl.StudentDaoImpl;
import com.bjpowernode.domain.Student;
import org.junit.Test;

import java.util.List;

/**
 *
 */
public class MyTest2 {

    /**
     * String sqlId="com.bjpowernode.dao.StudentDao.selectById";
     * Student student  = sqlSession.selectOne(sqlId,id);
     *
     *
     * 测试方法中：调用dao的方法
     * Student student  = dao.selectById(1002);
     *
     * 1）dao: 通过反射能够得到 全限定类型名称
     *    dao是StudentDao类型的， 全限定名称 com.bjpowernode.dao.StudentDao
     * 2）selectById: dao中的方法名称，  方法名称就是 mapper文件中标签的id
     *   通过dao.selectById()能得到  sqlId="com.bjpowernode.dao.StudentDao.selectById";
     *
     * 3）确定调用SqlSession的那个方法
     *    1.根据dao接口的方法返回中， 如果返回是一个对象，例如Student ，调用SqlSession.selectOne();
     *      如果dao接口中的方法返回List，  调用SqlSession的 selectList();
     *
     *    2.根据mapper文件中的标签，如果标签是<insert>，调用SqlSession.insert()方法
     *
     *
     * mybatis框架，发现使用dao的方法调用能确定 执行sql语句的必要信息， mybatis简化dao对象的实现。
     * 由mybatis框架在程序执行期间， 根据你的Dao接口，  创建一个内存中的 接口的实现类对象。
     * mybatis把这个技术叫做 dao技术（动态代理，dao的动态代理）。
     *
     * dao代理技术： 由mybatis创建 StudentDao接口的实现类 Proxy(StudentDaoImpl)
     * 使用框架创建的StudentDaoImpl代替 "你自己手工实现的StudentDaoImpl类的功能"，
     * 不用开发人员写 dao接口的实现类。
     *
     * 使用dao的代理要求：
     *  1. mapper文件中的namespace： 必须是dao接口的全限定名称
     *  2. mapper文件中标签的id是 dao接口中的方法名称（一摸一样的）
     */
    @Test
    public void testSelectOne(){
        //要使用dao的方法
        // 接口类型    变量 =  new 接口的实现类();
        StudentDao  dao  = new StudentDaoImpl();
        Student student  = dao.selectById(1002);
        System.out.println("通过dao执行方法，的到的对象=="+student);
    }


    @Test
    public void testSelectStudents(){
        StudentDao dao  = new StudentDaoImpl();
        List<Student> students =  dao.selectStudents();
        students.forEach( stu-> System.out.println("stu="+stu));
    }

    @Test
    public void testInsert(){
        StudentDao dao  = new StudentDaoImpl();

        Student student  = new Student();
        student.setId(1009);
        student.setName("周强");
        student.setEmail("zhouqiang@qq.com");
        student.setAge(28);

        dao.insertStudent(student);
    }
}
