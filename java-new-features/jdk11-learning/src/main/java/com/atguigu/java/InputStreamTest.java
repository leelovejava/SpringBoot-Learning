package com.atguigu.java;

import org.junit.Test;

import java.io.FileOutputStream;

/**
 * 改进的文件API。
 * InputStream 加强
 *
 * @author leelovejava
 */
public class InputStreamTest {

	@Test
	public void testName() throws Exception {
		var cl = this.getClass().getClassLoader();
		try (var is = cl.getResourceAsStream("file"); var os = new FileOutputStream("file2")) {
			// 把输入流中的所有数据直接自动地复制到输出流中
			is.transferTo(os);
		}
	}
}
