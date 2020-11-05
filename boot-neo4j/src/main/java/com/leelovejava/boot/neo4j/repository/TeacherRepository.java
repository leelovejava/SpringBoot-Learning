package com.leelovejava.boot.neo4j.repository;

import com.leelovejava.boot.neo4j.model.Teacher;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * 教师节点Repository
 *
 * @author leelovejava
 * @date 2020/11/5 17:48
 **/
public interface TeacherRepository extends Neo4jRepository<Teacher, String> {
}
