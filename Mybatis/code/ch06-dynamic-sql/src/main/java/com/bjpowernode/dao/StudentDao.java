package com.bjpowernode.dao;

import com.bjpowernode.domain.Student;
import com.bjpowernode.vo.CustomObject;
import com.bjpowernode.vo.QueryParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface StudentDao {
    //if
    List<Student> selectIf(Student student);

    //where
    List<Student> selectWhere(Student student);

    //foreach-1
    List<Student> selectForeachOne(List<Integer> idlist);

    //foreach-2
    List<Student> selectForeachTwo(List<Student> studentList);


    List<Student> selectAllStudents();
}
