package com.leelovejava.boot.jwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leelovejava.boot.jwt.config.AudienceConfig;
import com.leelovejava.boot.jwt.service.UserService;
import com.leelovejava.boot.jwt.util.JwtHelper;
import com.leelovejava.boot.jwt.util.MatcherUtil;
import com.leelovejava.boot.jwt.util.ResultVOUtil;
import com.leelovejava.boot.jwt.vo.ResultVo;
import com.leelovejava.boot.jwt.vo.UserVo;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户
 * 使用流程
 * 1. 前端提交用户名和密码到任意一台服务器
 * 2. 服务器验证用户名和密码（spring security或者shiro）
 * 3. 如果验证成功，将使用jwt的api生成一个token
 * 4. 这个token将会返回给前端，前端将会把它保存起来（cookie、上下文或者其他），之后每次请求，都把这个token加在http头里发送到服务端
 * 5. 服务端能够验证token的合法性，因为里面有过期时间和防篡改机制，所以token需要完整发送
 *
 * @author leelovejava
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private AudienceConfig audience;

    /**
     * 登录
     *
     * @param usernameOrEmail 邮箱或用户名
     * @param password        密码
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResultVo login(@RequestParam("usernameOrEmail") String usernameOrEmail,
                          @RequestParam("password") String password) {
        Boolean isEmail = MatcherUtil.matcherEmail(usernameOrEmail);
        UserVo user = new UserVo();
        if (isEmail) {
            user.setEmail(usernameOrEmail);
        } else {
            user.setUsername(usernameOrEmail);
        }
        UserVo query_user = userService.get(user);
        if (query_user == null) {
            return ResultVOUtil.error("400", "用户名或邮箱错误");
        }
        // 验证密码
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
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "getTokenInfo", method = RequestMethod.POST)
    public ResultVo getTokenInfo(@RequestParam("token") String token) {
        try {
            Claims claims = JwtHelper.parseJWT(token, audience.getBase64Secret());
            return ResultVOUtil.success(new ObjectMapper().writeValueAsString(claims));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultVOUtil.error("500", "解析jwt错误");
    }
}
