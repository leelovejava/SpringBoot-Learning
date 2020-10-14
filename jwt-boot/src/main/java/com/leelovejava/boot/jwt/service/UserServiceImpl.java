package com.leelovejava.boot.jwt.service;

import com.leelovejava.boot.jwt.vo.RoleVo;
import com.leelovejava.boot.jwt.vo.UserVo;
import org.springframework.stereotype.Service;

/**
 * @author leelovejava
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 此处模拟数据库查询
     *
     * @param userVo
     * @return
     */
    @Override
    public UserVo get(UserVo userVo) {
        RoleVo roleVo = new RoleVo("1", "1");
        return new UserVo("1", "1@qq.com", "tianhao", "123456", roleVo);
    }
}
