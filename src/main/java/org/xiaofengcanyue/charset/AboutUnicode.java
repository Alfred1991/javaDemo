package org.xiaofengcanyue.charset;

import org.xiaofengcanyue.annotation.Author;
import org.xiaofengcanyue.annotation.Employee;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.*;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 编码格式unicode的代码点有1114112个，值的范围从0到0x10FFFF。
 * unicode的代码点分为17个区域，第一个区域的代码点范围是0到U+FFFF，这个平面也被称为基本多语言平面（Basic Multilingual Plane,BMP）。
 * utf-8采用8位代码单元，utf-16采用16位代码单元。
 */
public class AboutUnicode {

    public static void main(String[] args) throws Exception{
        System.out.println(filter("你好，123世界！!"));
    }

    /**
     * utf-16编码
     * @param codePoint
     * @return
     */
    public static char[] encode(int codePoint){
        if((codePoint >= 0 && codePoint <= 0xD7FF) || (codePoint >= 0xE000 && codePoint <= 0xFFFF)){
            return new char[]{(char) codePoint};
        }else{
            codePoint = codePoint - 0x10000;
            int high = (codePoint >> 10) + 0xD800;
            int low = (codePoint & 0x3FF) + 0xDC00;
            return new char[]{(char) high, (char) low};
        }
    }

    /**
     * java中的char由两个字节组成。
     * java语言中的char类型与unicode中的字符不是一一对应关系，非BMP中的字符由两个char类型单元通过代理项对的方式来实现。
     */
    public static void useCharacter(){Character c = null;
        String str = "你好";
        int codePoint = Character.codePointAt(str,0);
        Character.isBmpCodePoint(codePoint);
        int smpCodePoint = 0x12367;
        Character.isSupplementaryCodePoint(smpCodePoint);
        Character.charCount(smpCodePoint);
        char high = Character.highSurrogate(smpCodePoint);
        char low = Character.lowSurrogate(smpCodePoint);
    }


    /**
     * CharsetEncoder的用法
     */
    public static void simpleEncode(){
        Charset charset = StandardCharsets.UTF_8;
        CharsetEncoder encoder = charset.newEncoder();
        CharBuffer inputBuffer = CharBuffer.allocate(256);
        inputBuffer.put("你好").flip();
        ByteBuffer outputBuffer = ByteBuffer.allocate(256);
        encoder.encode(inputBuffer,outputBuffer,true);
        encoder.flush(outputBuffer);

        outputBuffer.flip();

        byte[] dest = new byte[outputBuffer.limit()];
        outputBuffer.get(dest);

        for (byte bb:dest) {
            System.out.printf("%X",bb);
        }
    }

    /**
     * 将一个utf-8的文件复制为GB18030的文件。
     * @throws IOException
     */
    public static void encodeFile() throws IOException{
        Charset charset = Charset.forName("GB18030");
        CharsetEncoder encoder = charset.newEncoder();
        encoder.onMalformedInput(CodingErrorAction.IGNORE);
        encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        ByteBuffer outputBuffer = ByteBuffer.allocate(128);
        List<String> lines = Files.readAllLines(Paths.get("test.html"),StandardCharsets.UTF_8);
        try(FileChannel destChannel = FileChannel.open(Paths.get("result.html"), StandardOpenOption.CREATE,StandardOpenOption.WRITE)){
            for(String line : lines){
                CharBuffer charBuffer = CharBuffer.wrap(line);
                while(true){
                    CoderResult result = encoder.encode(charBuffer,outputBuffer,false);
                    if(result.isOverflow()){
                        writeToChannel(destChannel,outputBuffer);
                    }else if(result.isUnderflow()){
                        break;
                    }
                }
            }
            writeToChannel(destChannel,outputBuffer);
            encoder.encode(CharBuffer.allocate(0),outputBuffer,true);
            CoderResult result = encoder.flush(outputBuffer);
            if(result.isOverflow()){
                ByteBuffer newBuffer = ByteBuffer.allocate(1024);
                encoder.flush(newBuffer);
                writeToChannel(destChannel,newBuffer);
            }else{
                writeToChannel(destChannel,outputBuffer);
            }
        }
    }

    public static void writeToChannel(WritableByteChannel channel,ByteBuffer buffer) throws IOException{
        buffer.flip();
        channel.write(buffer);
        buffer.compact();
    }

    /**
     * 过滤字符集，结果中只包含ISO-8859-1中定义的字符。
     * @param str
     * @return
     * @throws CharacterCodingException
     */
    public static String filter(String str) throws CharacterCodingException{
        Charset charset = StandardCharsets.ISO_8859_1;
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        CharBuffer buffer = CharBuffer.wrap(str);
        ByteBuffer byteBuffer = encoder.encode(buffer);
        CharBuffer result = decoder.decode(byteBuffer);
        return result.toString();
    }

    /**
     * 当前页面的字符集的确定取决于下面几个因素：
     *  1、返回页面的HTTP响应中的Content-Type头中给出的字符集，如"text/html;charset=utf-8"；
     *  2、页面中通过<meta>标签声明的字符集；
     *  3、用户通过浏览器的界面手动选择的字符集。
     *
     * 如果没有在Content-Type头中显示指定，ISO-8859-1是默认的编码格式，因此会产生乱码。
     *
     * 所以需要在<meta>标签中声明utf-8作为编码格式，同时对服务器进行配置以发送正确的HTTP响应头信息。
     * 而在服务端，通过HttpServletRequest类的setCharacterEncoding方法来显式地设置解析时使用的编码格式。常用的做法是通过一个过滤器javax.servlet.Filter接口来实现这一设置。
     *
     * @throws Exception
     */
    public static void urlEncode() throws Exception{
        URLEncoder.encode("","utf-8");
    }

}
