package com.didispace.jwt.controller;

import com.alibaba.fastjson.JSON;
import com.didispace.jwt.service.UserService;
import com.didispace.jwt.util.JwtHelper;
import com.didispace.jwt.util.MatcherUtil;
import com.didispace.jwt.util.ResultVOUtil;
import com.didispace.jwt.config.AudienceConfig;
import com.didispace.jwt.vo.ResultVo;
import com.didispace.jwt.vo.UserVo;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private AudienceConfig audience;

    /**
     * 登录
     * @param usernameOrEmail 邮箱或用户名
     * @param password        密码
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResultVo login(@RequestParam("usernameOrEmail") String usernameOrEmail, @RequestParam("password") String password) {
        Boolean is_email = MatcherUtil.matcherEmail(usernameOrEmail);
        UserVo user = new UserVo();
        if (is_email) {
            user.setEmail(usernameOrEmail);
        } else {
            user.setUsername(usernameOrEmail);
        }
        UserVo query_user = userService.get(user);
        if (query_user == null) {
            return ResultVOUtil.error("400", "用户名或邮箱错误");
        }
        //验证密码
        if (!password.equals(query_user.getPassword())) {
            // 密码错误，返回提示
            return ResultVOUtil.error("400", "密码错误");
        }

        String jwtToken = JwtHelper.createJWT(query_user.getUsername(),
                query_user.getId(),
                query_user.getRole().toString(),
                audience.getClientId(),
                audience.getName(),
                audience.getExpiresSecond() * 1000,
                audience.getBase64Secret());

        String result_str = "bearer;" + jwtToken;
        return ResultVOUtil.success(result_str);
    }

    /**
     * 解析jwt
     * @param token
     * @return
     */
    @RequestMapping(value = "getTokenInfo", method = RequestMethod.POST)
    public ResultVo getTokenInfo(@RequestParam("token") String token) {
        try {
            Claims claims =JwtHelper.parseJWT(token, audience.getBase64Secret());
            return ResultVOUtil.success(JSON.toJSONString(claims));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultVOUtil.error("500", "解析jwt错误");
    }
}
