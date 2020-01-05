package org.xiaofengcanyue.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AboutRegular {

    public static void main(String[] args) {

//        namedCapturingGroup();

//        repeatPattern();

//        useUnicodeCharacterClass();

        matchScript();
    }

    /**
     * 通过 java7引入的 命名捕获分组 来匹配字符串并提取内容
     */
    public static void namedCapturingGroup(){
        String url = "http://www.example.org/uid/alex/docid/1/title/MyFirstBlog";
        Pattern pattern = Pattern.compile("^.*/uid/(?<uid>.*)/docid/(?<docid>.*)/title/(?<title>.*)");
        Matcher matcher = pattern.matcher(url);
        if(matcher.matches()){
            String uid = matcher.group("uid");
            String docId = matcher.group("docid");
            String title = matcher.group("title");
            System.out.printf("%s %s %s",uid,docId,title);
        }
    }

    /**
     * 捕获分组的名称也可以用在正则表达式中，用来替换使用数字来进行后向引用的做法。
     */
    public static void repeatPattern(){
        String str = "123-123-12-456-456";
        Pattern pattern = Pattern.compile("(?<num>\\d+)-\\k<num>");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            String repeat = matcher.group("num");
            System.out.printf("%s\r\n",repeat);
        }
    }

    /**
     * 如果一个Unicode字符不在基本多语言平面（BMP)，那么要在java中以代理项对的方式出现，在转换成"\\u"形式的时候则需要两个相邻的字符。
     * java7中新增"\\x"来直接表示unicode中的代码点，其使用方式和"\\u"类似，只不过允许表示的范围更广。
     *
     */
    public static void unicodeCodePointPattern(){
        Pattern pattern = Pattern.compile("\\x1011F");
    }


    /**
     * 使用Pattern进行compile时可以指定多个标记，这些标记可以控制匹配时的行为。
     * 例如Pattern.CASE_INSENSITIVE可以设置匹配时不区分大小写。
     * java7中新增Pattern.UNICODE_CHARACTER_CLASS来设置使用unicode版本的预定义字符类和POSIX字符类。
     * 例如"\\d"默认只能匹配到0到9的字符，启用Pattern.UNICODE_CHARACTER_CLASS后可匹配unicode规范中所定义的所有属于数字类别的字符，不只是0到9，还包括其他语言中的数字字符。
     *
     */
    public static void useUnicodeCharacterClass(){
        //一般数字100和 全角数字 1 0 0
        String str = "100 １　０　０";
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            String digit = matcher.group(1);
            System.out.printf("%s\r\n",digit);
        }
        pattern = Pattern.compile("(\\d+)",Pattern.UNICODE_CHARACTER_CLASS);
        matcher = pattern.matcher(str);
        while(matcher.find()){
            String digit = matcher.group(1);
            System.out.printf("%s\r\n",digit);
        }
    }

    /**
     * java7中允许指定unicode字符使用的书写格式（script）进行匹配。
     */
    public static void matchScript(){
        String str = "abc 你好 123";
        Pattern pattern = Pattern.compile("(\\p{script=Han}+)");//只匹配汉字
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            String hans = matcher.group(1);
            System.out.println(hans);
        }
    }

}
