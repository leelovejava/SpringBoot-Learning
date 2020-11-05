package com.leelovejava.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author leelovejava
 * @date 2020/11/3 22:38
 **/
public class Test01 {
    /**
     * 准备抓取的目标地址，%E4%BA%92%E8%81%94%E7%BD%91 为utf-8格式的 互联网
     */
    private static String url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=互联网";

    public static void main(String[] args) throws Exception {
        // 链接到目标地址
        Connection connect = Jsoup.connect(url);
        // 设置useragent,设置超时时间，并以get请求方式请求服务器
        Document document = connect.userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").timeout(6000).ignoreContentType(true).get();
        Thread.sleep(1000);
        // 获取指定标签的数据
        Element elementById = document.getElementById("content_left");
        // 输出文本数据
        System.out.println(elementById.text());
        // 输出html数据
        System.out.println(elementById.html());
    }
}
