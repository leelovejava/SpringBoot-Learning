package com.leelovejava.boot.neo4j.payload;

import com.leelovejava.boot.neo4j.model.Student;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * 按照课程分组的同学关系
 *
 * @author leelovejava
 * @date 2020/11/5 17:43
 **/
@Data
@QueryResult
public class ClassmateInfoGroupByLesson {
    /**
     * 课程名称
     */
    private String lessonName;

    /**
     * 学生信息
     */
    private List<Student> students;
}
