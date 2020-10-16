package com.atguigu.java1;

import org.junit.Test;

/**
 * JEP 378：文本块功能转正
 * @author shkstart  Email:shkstart@126.com
 * @create 2020 22:45
 */
public class TextBlocksTest {
    /**
     * JEP 378：文本块功能转正
     * Text Blocks首次是在JDK 13中以预览功能出现的，然后在JDK 14中又预览了一次，终于在JDK 15中被确定下来，可放心使用了。
     *
     * 文本块是一种多行字符串文字，它避免了大多数转义序列的需要，以一种可预测的方式自动设置字符串的格式，并在需要时使开发人员可以控制格式，简化编写 Java 程序的任务。
     *
     * 文本块建议的目标是提高 Java 程序中的字符串的可读性，这些字符串表示以非 Java 语言编写的代码。
     * 另一个目标是支持从字符串文本迁移，规定任何新构造都可以表达与字符串文本相同的字符串集，解释相同的转义序列，并且以与字符串文本相同的方式进行操作。OpenJDK 开发人员希望添加转义序列来管理显式空白和换行控件。
     *
     */
    @Test
    public void test1(){
        String text1 = "The Sound of silence\n" +
                "Hello darkness, my old friend\n" +
                "I've come to talk with you again\n" +
                "Because a vision softly creeping\n" +
                "Left its seeds while I was sleeping\n" +
                "And the vision that was planted in my brain\n" +
                "Still remains\n" +
                "Within the sound of silence";

        System.out.println(text1);

        //jdk13中的新特性：
        String text2 = """
                The Sound of silence
                Hello darkness, my old friend
                I've come to talk with you again
                Because a vision softly creeping
                Left its seeds while I was sleeping
                And the vision that was planted in my brain
                Still remains
                Within the sound of silence\
                """;
        System.out.println();
        System.out.println(text2);

        System.out.println(text1.length());
        System.out.println(text2.length());
    }

    //html
    @Test
    public void test2(){
        String html1 = "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>java14新特性</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p>hello,atguigu</p>\n" +
                "</body>\n" +
                "</html>";
        //jdk13中的新特性：
        String html2 = """
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>java14新特性</title>
                </head>
                <body>
                    <p>hello,atguigu</p>
                </body>
                </html>
                """;
    }

    //json
    @Test
    public void test3() {
        //jdk13之前的写法
        String myJson = "{\n" +
                "    \"name\":\"Song Hongkang\",\n" +
                "     \"address\":\"www.atguigu.com\",\n" +
                "    \"email\":\"shkstart@126.com\"\n" +
                "}";
        System.out.println(myJson);

        //jdk13的新特性
        String myJson1 = """
                {
                    "name":"Song Hongkang",
                     "address":"www.atguigu.com",
                    "email":"shkstart@126.com"
                }""";
        System.out.println(myJson1);
    }

    //sql
    @Test
    public void test4(){
        String sql = "SELECT id,NAME,email\n" +
                "FROM customers\n" +
                "WHERE id > 4\n" +
                "ORDER BY email DESC";

        //jdk13新特性：
        String sql1 = """
                SELECT id,NAME,email
                FROM customers
                WHERE id > 4
                ORDER BY email DESC
                """;
    }
    //jdk14新特性
    @Test
    public void test5(){
        String sql1 = """
                SELECT id,NAME,email
                FROM customers
                WHERE id > 4
                ORDER BY email DESC
                """;
        System.out.println(sql1);

        // \:取消换行操作
        // \s:表示一个空格
        String sql2 = """
                SELECT id,NAME,email \
                FROM customers\s\
                WHERE id > 4 \
                ORDER BY email DESC\
                """;
        System.out.println(sql2);
        System.out.println(sql2.length());
    }
}
