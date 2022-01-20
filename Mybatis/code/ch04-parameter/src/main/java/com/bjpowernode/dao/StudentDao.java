package com.bjpowernode.dao;

import com.bjpowernode.domain.Student;
import com.bjpowernode.vo.QueryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface StudentDao {

    //重载方法
    Student selectById(Integer id);


    //dao接口的方法形参是一个简单类型的
    //简单类型： java基本数据类型和String
    Student selectByEmail(String email);

    /*
      多个简单类型的参数
      使用@Param命名参数， 注解是mybatis提供的
      位置：在形参定义的前面
      属性：value 自定义的参数名称
     */
    List<Student> selectByNameOrAge(@Param("myname") String name,
                                    @Param("myage") Integer age);

    /*
     * 一个java对象作为参数( 对象由属性， 每个属性有set，get方法)
     */
    List<Student> selectByObject(Student student);

    List<Student> selectByQueryParam(QueryParam param);

    /*
       使用位置，获取参数
     */
    List<Student> selectByPosition(String name,Integer age);

    /*
       使用Map作为参数
     */
    List<Student> selectStudentByMap(Map<String,Object> map);


    //更新
    int updateStudent(Student student);

    // ${}占位符的使用, 使用@Param 命名
    List<Student> queryStudent(@Param("studentName") String name);

    List<Student> queryStudentOrderById();
    List<Student> queryStudentOrderByName();

    List<Student> queryStudentOrderByColName(@Param("myname") String name,
                                             @Param("colName") String colName,
                                             @Param("tableName") String tableName);
}
