package com.leelovejava.boot.jwt.util;

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
 *
 * @author leelovejava
 */
public class JwtHelper {

    /**
     * 解析jwt
     */
    public static Claims parseJWT(String jsonWebToken, String base64Security) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security)).
                            build()
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (Exception ex) {
            // 如果secret被篡改了,那么将会抛出SignatureException异常
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
        // 1. 头部(header):声明类型、声明加密的算法（通常使用HMAC SHA256）
        // 2. 载荷(payload)
        // 3. 签证(signature):HS256加密,包括三部分:header(base64后的)、payload(base64后的)、secret 私钥
        JwtBuilder builder = Jwts.builder().
                setHeaderParam("typ", "JWT")
                // subject 签发的主体，比如用户名。其实它也是放在claims中的
                // claims 一些附加信息，也就是payload中的内容。由于它是一个HashMap，所以你大可以向里面仍你所需要的所有信息
                .claim("role", role)
                .claim("unique_name", name)
                .claim("userId", userId)
                .setIssuer(issuer)
                .setAudience(audience)
                // secret 加密的密钥，用来验证前面签发的内容
                .signWith(signingKey, SignatureAlgorithm.HS256);
        // 添加Token过期时间
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            // expiration 签发日期和失效日期，在验证的时候可以使用
            builder.setExpiration(exp).setNotBefore(now);
        }

        // 生成JWT
        return builder.compact();
    }
}
