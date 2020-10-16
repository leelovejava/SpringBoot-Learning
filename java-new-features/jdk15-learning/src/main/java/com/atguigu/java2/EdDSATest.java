package com.atguigu.java2;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.EdECPoint;
import java.security.spec.EdECPublicKeySpec;
import java.security.spec.NamedParameterSpec;

/**
 * @author shkstart  Email:shkstart@126.com
 * @create 2020 11:26
 */
public class EdDSATest {
    public static void main(String[] args) throws Exception {
        // example: generate a key pair and sign
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519");
        KeyPair kp = kpg.generateKeyPair();
        // algorithm is pure Ed25519
        Signature sig = Signature.getInstance("Ed25519");
        sig.initSign(kp.getPrivate());
        byte[] msg = "数据".getBytes();
        sig.update(msg);//
        byte[] s = sig.sign();// example: use KeyFactory to contruct a public key
        KeyFactory kf = KeyFactory.getInstance("EdDSA");
        boolean xOdd = false;//
        BigInteger y = new BigInteger("1234");//
        NamedParameterSpec paramSpec = new NamedParameterSpec("Ed25519");
        EdECPublicKeySpec pubSpec = new EdECPublicKeySpec(paramSpec, new EdECPoint(xOdd, y));
        PublicKey pubKey = kf.generatePublic(pubSpec);
    }
}
