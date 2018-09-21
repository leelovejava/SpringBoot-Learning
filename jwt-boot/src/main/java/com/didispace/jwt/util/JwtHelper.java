package com.didispace.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * jwt工具类
 */
public class JwtHelper {

    /**
     * 解析jwt
     */
    public static Claims parseJWT(String jsonWebToken, String base64Security) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 构建jwt
     *
     * @param name           用户名
     * @param userId         用户id
     * @param role           角色
     * @param audience       客户端信息
     * @param issuer         该JWT的签发者
     * @param TTLMillis      过期时间 秒
     * @param base64Security base64加密秘钥
     * @return
     */
    public static String createJWT(String name, String userId, String role,
                                   String audience, String issuer, long TTLMillis, String base64Security) {
        // 签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Security);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // 添加构成JWT的参数
        // 头部(header):声明类型、声明加密的算法（通常使用HMAC SHA256）
        // 载荷(payload)
        // 签证(signature):HS256加密,包括三部分:header(base64后的)、payload(base64后的)、secret 私钥
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .claim("role", role)
                .claim("unique_name", name)
                .claim("userId", userId)
                .setIssuer(issuer)
                .setAudience(audience)
                .signWith(signatureAlgorithm, signingKey);
        // 添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        // 生成JWT
        return builder.compact();
    }
}