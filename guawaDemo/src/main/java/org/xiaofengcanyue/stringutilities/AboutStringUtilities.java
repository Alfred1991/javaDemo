package org.xiaofengcanyue.stringutilities;

import com.google.common.base.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class AboutStringUtilities {

    public static void main(String[] args) {
        aboutCaseFormat();
    }

    /**
     * Joiner是不可变的，它的配置方法总返回一个新的Joiner，因此它是线程安全的。
     * 可以将它看做一个static final constant
     */
    public static void aboutJoiner(){
        Joiner joiner = Joiner.on("; ").skipNulls();
        System.out.println(joiner.join("Harry",null,"Ron","Hermione"));

        /**
         * 使用Object的toString()方法
         */
        System.out.println(Joiner.on(",").join(Arrays.asList(1,5,7)));
    }

    /**
     * splitter是线程安全的。
     */
    public static void aboutSplitter(){
        Splitter.on(",").trimResults().omitEmptyStrings().split("foo,bar,,   qux");
        /**
         * 解析map
         */
        Splitter.on(",").trimResults().omitEmptyStrings().withKeyValueSeparator("->").split("123->avc,345->sdfa");
    }

    /**
     * 1、what constitutes a "matching" character?
     * 2、what to do with those "matching" characters?
     */
    public static void aboutCharMatcher(){
        String noControl = CharMatcher.javaIsoControl().removeFrom("string"); // remove control characters
        String theDigits = CharMatcher.digit().retainFrom("string"); // only the digits
        String spaced = CharMatcher.whitespace().trimAndCollapseFrom("string", ' ');
        // trim whitespace at ends, and replace/collapse whitespace into single spaces
        String noDigits = CharMatcher.javaDigit().replaceFrom("string", "*"); // star out all digits
        String lowerAndDigit = CharMatcher.javaDigit().or(CharMatcher.javaLowerCase()).retainFrom("string");
        // eliminate all characters that aren't digits or lowercase
    }

    /**
     * Charsets提供了六个标准Charset实现
     */
    public static void aboutCharsets(){
        try {
            byte[] bytes = "string".getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // how can this possibly happen?
            throw new AssertionError(e);
        }

        /**
         * 最好使用：
         */
        byte[] bytes = "string".getBytes(Charsets.UTF_8);
    }

    /**
     * CaseFormat用于在ASCII case conventions之间进行转换。它支持：
     * Format	            Example
     * LOWER_CAMEL	        lowerCamel
     * LOWER_HYPHEN	        lower-hyphen
     * LOWER_UNDERSCORE	    lower_underscore
     * UPPER_CAMEL	        UpperCamel
     * UPPER_UNDERSCORE	    UPPER_UNDERSCORE
     *
     */
    public static void aboutCaseFormat(){
        System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,"CONSTANT_NAME"));
    }

}
