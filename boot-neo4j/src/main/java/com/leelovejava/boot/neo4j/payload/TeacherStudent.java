package com.leelovejava.boot.neo4j.payload;

import  com.leelovejava.boot.neo4j.model.Student;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * 师生关系
 * @author leelovejava
 * @date 2020/11/5 17:43
 **/
@Data
@QueryResult
public class TeacherStudent {
    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 学生信息
     */
    private List<Student> students;
}
