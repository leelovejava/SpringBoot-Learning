package com.leelovejava.jsoup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Windows下载及安装chromedriver.exe
 * https://blog.csdn.net/muriyue6/article/details/101440353
 *
 * 下载地址 http://chromedriver.storage.googleapis.com/index.html
 */
public class TestSelenium {
    public static void main(String[] args)  {
    	// 设置环境变量
    	System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        WebDriver dr = new ChromeDriver();
        // 设置访问地址
        //dr.get("http://www.baidu.com");
		dr.get("file://C:\\Users\\tianhao-PC\\Desktop\\laogewen428\\index.html");

		// 获取输出信息；html中的js代码执行后 在body中document.write()或者 赋值给body或者div

		// 元素定位 获取 <html> 中<body> 下面的 内容
		WebElement webElement = dr.findElement(By.tagName("span"));
		System.out.println(webElement.getText());
        dr.quit();
    }
}
