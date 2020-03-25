package org.xiaofengcanyue.ioutilities;

import com.google.common.base.Charsets;
import com.google.common.io.*;

import java.io.File;
import java.io.IOException;

/**
 * 字节流工具ByteStreams
 * 字符流工具CharStreams
 *
 * ByteStreams	                          CharStreams
 * byte[] toByteArray(InputStream)	      String toString(Readable)
 * N/A	                                  List<String> readLines(Readable)
 * long copy(InputStream, OutputStream)	  long copy(Readable, Appendable)
 * void readFully(InputStream, byte[])	  N/A
 * void skipFully(InputStream, long)	  void skipFully(Reader, long)
 * OutputStream nullOutputStream()	      Writer nullWriter()
 */
public class AboutIOUtilities {

    public static void main(String[] args) {

    }

    /**
     * sources是流的输入的抽象，sinks是流的输出的抽象
     *
     * Bytes	                                    Chars
     * Files.asByteSource(File)	                    Files.asCharSource(File, Charset)
     * Files.asByteSink(File, FileWriteMode...)	    Files.asCharSink(File, Charset, FileWriteMode...)
     * MoreFiles.asByteSource(Path, OpenOption...)	MoreFiles.asCharSource(Path, Charset, OpenOption...)
     * MoreFiles.asByteSink(Path, OpenOption...)	MoreFiles.asCharSink(Path, Charset, OpenOption...)
     * Resources.asByteSource(URL)	                Resources.asCharSource(URL, Charset)
     * ByteSource.wrap(byte[])	                    CharSource.wrap(CharSequence)
     * ByteSource.concat(ByteSource...)	            CharSource.concat(CharSource...)
     * ByteSource.slice(long, long)	                N/A
     * CharSource.asByteSource(Charset)	            ByteSource.asCharSource(Charset)
     * N/A	                                        ByteSink.asCharSink(Charset)
     */
    public static void createSourcesAndSinks(){
        Files.asByteSource(new File(""));
    }

    /**
     * 默认情况下，所有其他操作都需要调用获取stream的方法。
     */
    public static void useSourcesAndSinks(ByteSource byteSource, CharSource charSource, ByteSink byteSink, CharSink charSink) throws IOException {
        //获取stream的方法
        byteSource.openStream();
        byteSink.openBufferedStream();

        //其他操作
        byteSource.read();
        charSource.readFirstLine();
        charSink.write("");
    }

    public static void aboutFiles() throws IOException{
        Files.createParentDirs(new File(""));
        Files.getFileExtension("");
        Files.getNameWithoutExtension("");
        Files.simplifyPath("");
        Files.fileTraverser();
    }
}
