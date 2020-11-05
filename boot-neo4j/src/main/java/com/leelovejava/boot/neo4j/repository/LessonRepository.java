package com.leelovejava.boot.neo4j.repository;

import com.leelovejava.boot.neo4j.model.Lesson;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * 课程节点Repository
 * @author leelovejava
 * @date 2020/11/5 17:46
 **/
public interface LessonRepository extends Neo4jRepository<Lesson, String>{
}
