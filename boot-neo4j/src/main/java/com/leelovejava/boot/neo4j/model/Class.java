package com.leelovejava.boot.neo4j.model;

import com.leelovejava.boot.neo4j.config.CustomIdStrategy;
import com.leelovejava.boot.neo4j.constants.NeoConsts;
import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * 班级节点
 *
 * @author leelovejava
 * @date 2020/11/5 17:37
 **/
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Class {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * 班级名称
     */
    @NonNull
    private String name;

    /**
     * 班级的班主任
     */
    @Relationship(NeoConsts.R_BOSS_OF_CLASS)
    @NonNull
    private Teacher boss;
}
