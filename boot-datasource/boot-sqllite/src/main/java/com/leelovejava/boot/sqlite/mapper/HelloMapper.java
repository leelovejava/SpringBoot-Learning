package com.leelovejava.boot.sqlite.mapper;

import com.leelovejava.boot.sqlite.model.HelloModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author leelovejava
 */
@Mapper
@Component
public interface HelloMapper {

    /**
     * 插入 并查询id 赋给传入的对象
     *
     * @param model
     * @return
     */
    @Insert("INSERT INTO hello(id, title, text) VALUES(#{id}, #{title}, #{text})")
    /// @SelectKey(statement = "SELECT seq id FROM sqlite_sequence WHERE (name = 'hello')", before = false, keyProperty = "id", resultType = int.class)
    int insert(HelloModel model);

    /**
     * 根据 ID 查询
     *
     * @param id
     * @return
     */
    @Select("SELECT * FROM hello WHERE id=#{id}")
    HelloModel select(int id);

    /**
     * 查询全部
     *
     * @return
     */
    @Select("SELECT * FROM hello")
    List<HelloModel> selectAll();

    /**
     * 更新 value
     *
     * @param model
     * @return
     */
    @Update("UPDATE hello SET value=#{value} WHERE id=#{id}")
    int updateValue(HelloModel model);

    /**
     * 根据 ID 删除
     *
     * @param id
     * @return
     */
    @Delete("DELETE FROM hello WHERE id=#{id}")
    int delete(Integer id);

}
