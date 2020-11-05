package com.leelovejava.jsoup;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author leelovejava
 * @date 2020/11/3 22:39
 **/
public class Test02 {
    /**
     * 本地文件保存地址
     */
    private static final String LOCAL_FILE_PATH = "C:\\Users\\tianhao-PC\\Downloads\\tmp\\";
    private static final String CRYPTO_FILE_PATH = "D:\\workspace2\\SpringBoot-Learning\\jsoup\\src\\test\\resources\\crypto-js-4.0.0\\crypto-js.js";

    private static String jiemiJs(String str) throws ScriptException,
            FileNotFoundException, NoSuchMethodException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        // 得到脚本引擎
        ScriptEngine engine = engineManager.getEngineByName("JavaScript");
        FileReader fReader = new FileReader(CRYPTO_FILE_PATH);
        engine.eval(fReader);

        Invocable inv = (Invocable) engine;
        //调用js中的方法
        Object test2 = inv.invokeFunction("jiemi", str);
        return test2.toString();
    }

    /**
     * 获取页面中所有的图片
     */
    public static void main(String[] args) {
        Map<String, String> cookieMap = convertCookie("_ga=GA1.2.1138875356.1604411420; _gid=GA1.2.1543067441.1604411420; wordpress_test_cookie=WP+Cookie+check; wordpress_logged_in_e9a9d6f79824b739e9d691f2c4536341=1165746141%7C1605621363%7C28vSlbGggC8NSWl4g2TvT1sevcIfQHhQ2ngD6Zk2CnG%7Ce9b42fd15a49f3747294b16460177d20abe91081753c232457bf85ebc37d314a; PHPSESSID=gt5hgoade6kv033pph3g7ipfj3");
        try {
            getImg(cookieMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getImg(Map<String, String> cookieMap) throws InterruptedException, IOException {
        // 链接到目标地址
        String url;
        //for (int i = 100000; i < 274349; i++) {
        for (int i = 100020; i < 274349; i++) {
            url = "https://laogewen428.vip/bj/" + i + ".html";
            getUrlImg(url, cookieMap);
        }
    }

    /**
     * 下载图片到指定目录
     *
     * @param filePath 文件路径
     * @param imgUrl   图片URL
     */
    public static void downImages(String filePath, String imgUrl) throws IOException {
        // 若指定文件夹没有，则先创建
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 截取图片文件名
        String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
        if (fileName.contains("u=") || fileName.length() > 89) {
            fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".png";
        }
        if (!fileName.contains(".")) {
            fileName += ".png";
        }
        try {
            // 文件名里面可能有中文或者空格，所以这里要进行处理。但空格又会被URLEncoder转义为加号
            String urlTail = URLEncoder.encode(fileName, "UTF-8");
            // 因此要将加号转化为UTF-8格式的%20
            imgUrl = imgUrl.substring(0, imgUrl.lastIndexOf('/') + 1) + urlTail.replaceAll("\\+", "\\%20");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 写出的路径
        File file = new File(filePath + File.separator + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            // 获取图片URL
            URL url = new URL(imgUrl);
            // 获得连接
            URLConnection connection = url.openConnection();
            // 设置10秒的相应时间
            connection.setConnectTimeout(10 * 1000);
            // 获得输入流
            InputStream in = connection.getInputStream();
            // 获得输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            // 构建缓冲区
            byte[] buf = new byte[1024];
            int size;
            // 写入到文件
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void getUrlImg(String url, Map<String, String> cookieHashMap) {
        try {
            Connection connect = Jsoup.connect(url).cookies(cookieHashMap);
            //设置useragent,设置超时时间，并以get请求方式请求服务器
            Document document = connect.userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)").timeout(6000).ignoreContentType(true).get();
            Thread.sleep(1000);
            // 获取指定标签的数据
            String str = document.toString();
            String contact = null, address = null;

            if (str.contains("联系方式")) {
                contact = str.substring(str.lastIndexOf("var data =") + 12, str.lastIndexOf("</script> </font><br>"));
                contact = contact.substring(0, contact.indexOf("\";"));
                contact = jiemiJs(contact);
                contact = contact.substring(contact.indexOf("#ff0000\">") + 9, contact.indexOf("</span>"));
            }

            if (str.contains("详细地址")) {
                address = str.substring(str.indexOf("var data =") + 12, str.indexOf("</script> </font><br>"));
                address = address.substring(0, address.indexOf("\";"));
                address = jiemiJs(address);
                address = address.substring(address.indexOf("#ff0000\">") + 9, address.indexOf("</span>"));
            }


            Elements elements = document.getElementsByClass("article-content");
            if (elements.isEmpty()) {
                return;
            }
            Elements addressSpan = document.getElementsByClass("breadcrumbs").get(0).getElementsByTag("span");
            String id = url.substring(url.lastIndexOf('/') + 1).
                    replace(".html", "");
            // 省
            String provinceName = addressSpan.get(4).text();
            // 市
            String cityName = addressSpan.get(6).text();
            // 文件下载地址
            String filePath = LOCAL_FILE_PATH + provinceName + File.separator + cityName + File.separator + id;
            // 记事本下载地址
            String textPath = filePath + File.separator + id + ".txt";
            if (!new File(filePath).exists()) {
                new File(filePath).mkdirs();
            }
            StringBuffer content = new StringBuffer();
            String title = document.getElementsByClass("article-title").get(0).text();
            content.append("标题:" + title + "\r\n");
            String createTime = document.getElementsByClass("article-meta").get(0)
                    .getElementsByTag("span").get(0).text();
            content.append("更新时间:" + createTime + "\r\n");
            Element element = elements.get(0);
            Element dContent = element.getElementsByClass("aerfb").get(0);

            Element element2 = dContent.getElementsByTag("p").get(0);
            String[] s = element2.text().split(" ");
            for (String s1 : s) {
                if (s1.contains("详细地址")) {
                    if (s1.split("： ").length == 1) {
                        ///s1 += element2.getElementsByTag("font").get(0).text();
                        s1 += address;
                    }
                } else if (s1.contains("联系方式")) {
                    if (s1.split("： ").length == 1) {
                        ///s1 += element2.getElementsByTag("font").get(1).text();
                        s1 += contact;
                    }
                }
                content.append(s1 + "\r\n");
            }

            // 写入text文件
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(textPath, true)));
            out.write(content.toString());
            out.close();

            // 获取所有图片链接
            Elements imgTag = element.getElementsByTag("img");
            for (int i = 0; i < imgTag.size(); i++) {
                String imgPath = imgTag.get(i).attr("src");
                if (StringUtils.isNotEmpty(imgPath)) {
                    imgPath = "https://laogewen428.vip" + imgPath;
                    downImages(filePath,
                            imgPath);
                }
            }
        } catch (Exception e) {
            System.out.println("错误的地址:" + url);
            e.printStackTrace();
        }

    }

    /**
     * 转换cookie
     *
     * @param cookie
     * @return
     */
    public static Map<String, String> convertCookie(String cookie) {
        HashMap<String, String> cookiesMap = new HashMap<String, String>();
        String[] items = cookie.trim().split(";");
        for (String item : items) cookiesMap.put(item.split("=")[0], item.split("=")[1]);
        return cookiesMap;
    }
}
