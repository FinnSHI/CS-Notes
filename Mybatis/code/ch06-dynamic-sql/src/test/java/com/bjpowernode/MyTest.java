package com.bjpowernode;

import com.bjpowernode.dao.StudentDao;
import com.bjpowernode.domain.Student;
import com.bjpowernode.utils.MyBatisUtil;
import com.bjpowernode.vo.CustomObject;
import com.bjpowernode.vo.ProvinceCity;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.omg.PortableInterceptor.InvalidSlot;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MyTest {

    @Test
    public void testPageHelper(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);


        //调用PageHelper的方法
        PageHelper.startPage(1,400);
        List<Student> students  = dao.selectAllStudents();

        students.forEach( stu-> System.out.println("stu=="+stu));
        //3.关闭SqlSession对象
        session.close();
    }



    @Test
    public void testSelectForeachTwo(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);


        List<Student> list  = new ArrayList<>();
        Student s1 = new Student();
        s1.setId(1001);

        Student s2 = new Student();
        s2.setId(1005);

        list.add(s1);
        list.add(s2);


        List<Student> students  = dao.selectForeachTwo(list);

        students.forEach( stu-> System.out.println("stu=="+stu));
        //3.关闭SqlSession对象
        session.close();
    }


    @Test
    public void testSelectForeachOne(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);


        List<Integer> idlist = new ArrayList<>();
        idlist.add(1001);
        idlist.add(1002);
        idlist.add(1003);


        List<Student> students  = dao.selectForeachOne(idlist);

        students.forEach( stu-> System.out.println("stu=="+stu));
        //3.关闭SqlSession对象
        session.close();
    }

    @Test
    public void testFor(){
        List<Integer> idlist = new ArrayList<>();
        idlist.add(1001);
        idlist.add(1002);
        idlist.add(1003);

        // 查询 id 在 idlist中的student
        // select * from student where id in (1001,1002,1003)

        StringBuffer sql= new StringBuffer("");
        sql.append("select * from student where id in ");

        //使用循环， 把List数据 追加到 sql 字符串中。

        //循环之前加入 (
        sql.append("(");
        for(int i=0;i< idlist.size();i++){
            Integer item  = idlist.get(i);// item是集合成员
            sql.append(item);//添加成员到 sql字符串
            sql.append(","); //集合成员之间的分隔符
        }
        sql.deleteCharAt( sql.length()-1) ;

        //循环之后，加入 )
        sql.append(")");

        System.out.println("sql==="+sql);
    }


    @Test
    public void testSelectIf(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);


        Student student  = new Student();
        //student.setName("李四");
        //student.setAge(20);

        //student.setName("李四");

        student.setName(null);
        student.setAge(20);

        List<Student> students  = dao.selectIf(student);

        students.forEach( stu-> System.out.println("stu=="+stu));
        //3.关闭SqlSession对象
        session.close();
    }



    @Test
    public void testSelectWhere(){
        //1.获取SqlSession
        SqlSession session = MyBatisUtil.getSqlSession();
        //2.获取dao的代理
        StudentDao dao = session.getMapper(StudentDao.class);


        Student student  = new Student();
        //student.setName("李四");
        //student.setAge(20);

        //student.setName("李四");

        //student.setName(null);
        //student.setAge(20);

        List<Student> students  = dao.selectWhere(student);

        students.forEach( stu-> System.out.println("stu=="+stu));
        //3.关闭SqlSession对象
        session.close();
    }

}
