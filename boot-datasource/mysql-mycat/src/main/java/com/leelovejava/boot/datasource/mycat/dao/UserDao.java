package com.leelovejava.boot.datasource.mycat.dao;

import com.leelovejava.boot.datasource.mycat.model.UserModel;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户数据访问层
 *
 * @author leelovejava
 */
public interface UserDao extends JpaRepository<UserModel, Long> {

    /**
     * 根据名称模糊查询
     *
     * @param name
     * @param pageable
     * @return
     */
    //Page<UserModel> findByNameLike(String name, SpringDataWebProperties.Pageable pageable);

}
