package com.leelovejava.tools.commons;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;

/**
 * 常见的编码，解码方法封装
 *
 * @author y.x
 * @date 2019/11/25
 */
public class CodecTest {
    public void test01() {
        // Base64
        //Base64.encodeBase64String( byte[] binaryData)
        //Base64.decodeBase64(String base64String);

        // MD5
        ///DigestUtils.md5Hex(String data)

        // URL
        ///URLCodec.decodeUrl( byte[] bytes);
        ///URLCodec.encodeUrl(BitSet urlsafe, byte[] bytes);
    }
}
