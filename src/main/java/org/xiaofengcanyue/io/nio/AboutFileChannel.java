package org.xiaofengcanyue.io.nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AboutFileChannel {

    public static void main(String[] args) throws Exception {
        //openAndWrite();

        //readWriteAbsolute();

        //loadWebPage("https://www.baidu.com");

        copyUseByteBuffer("/Users/luohao/IdeaProjects/javaDemo/content.html","content_copy.html");
    }

    /**
     * 写filechannel
     * @throws IOException
     */
    public static void openAndWrite() throws IOException{

        FileChannel channel = FileChannel.open(Paths.get("my.txt"), StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.putChar('A').flip();
        channel.write(buffer);

    }

    /**
     * bytebuffer结合filechannel实现从文件absolute读写
     * @throws IOException
     */
    public static void readWriteAbsolute() throws IOException{
        FileChannel channel = FileChannel.open(Paths.get("absolute.txt"),StandardOpenOption.READ,StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        ByteBuffer writeBuffer = ByteBuffer.allocate(4).putChar('A').putChar('B');
        writeBuffer.flip();
        channel.write(writeBuffer,1024);
        ByteBuffer readBuffer = ByteBuffer.allocate(2);
        channel.read(readBuffer,1026);
        readBuffer.flip();
        char result = readBuffer.getChar();

        System.out.println(result);
    }

    /**
     * 使用transerfrom和transferto在通道间传输
     * @param url
     * @throws IOException
     */
    public static void loadWebPage(String url) throws IOException{
        try(FileChannel destChannel = FileChannel.open(Paths.get("content.html"),StandardOpenOption.WRITE,StandardOpenOption.CREATE)){
            InputStream input = new URL(url).openStream();
            ReadableByteChannel srcChannel = Channels.newChannel(input);
            destChannel.transferFrom(srcChannel,0,Integer.MAX_VALUE);
        }
    }

    public static void copyUseByteBuffer(String srcFilename,String destFilename) throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(32 * 1024);
        try(FileChannel src = FileChannel.open(
                Paths.get(srcFilename),StandardOpenOption.READ);
            FileChannel dest = FileChannel.open(
                Paths.get(destFilename),StandardOpenOption.WRITE,StandardOpenOption.CREATE)
            ){
            while(src.read(buffer)>0 || buffer.position() != 0){
                buffer.flip();
                dest.write(buffer);
                buffer.compact();
            }
        }
    }

    public static void copyUseChannelTransfer(String srcFilename,String destFilename) throws IOException{
        try(FileChannel src = FileChannel.open(
                Paths.get(srcFilename),StandardOpenOption.READ);
            FileChannel dest = FileChannel.open(
                Paths.get(destFilename),StandardOpenOption.WRITE,StandardOpenOption.CREATE
            )
        ){
            src.transferTo(0,src.size(),dest);
        }
    }

    /**
     * map将文件的内容刷到内存中，可实现高性能读写。
     * @param srcFilename
     * @throws IOException
     */
    public static void mapFile(String srcFilename) throws IOException{
        try(FileChannel channel = FileChannel.open(Paths.get(srcFilename),StandardOpenOption.READ,StandardOpenOption.WRITE)){
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,channel.size());
            byte b = buffer.get(1024 * 1024);
            buffer.put(5 * 1024 * 1024,b);
            buffer.force();
        }
    }

    /**
     * 文件通道的锁在整个虚拟机中共享。因此不能用它来同步线程。
     * @throws IOException
     */
    public static void updateWithLock() throws IOException{
        try(FileChannel channel = FileChannel.open(Paths.get("settings.config"),StandardOpenOption.READ,StandardOpenOption.WRITE);
            FileLock lock = channel.lock()){
            lock.isShared();
        }
    }


}
