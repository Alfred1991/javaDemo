package org.xiaofengcanyue.io.nio;

import java.io.*;
import java.net.URI;
import java.nio.channels.FileLockInterruptionException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * java7以后由java.nio.file.FileSystem来作为文件系统的抽象，允许用户实现自己的文件系统。
 * 新的文件系统需要实现java.nio.file.spi.FileSystemProvider接口，并在java平台中进行注册。
 * 每个文件系统都有一个对应的uri模式作为该文件系统的标识符，例如默认的文件系统的uri模式是"file"。
 * java标准库中包含了两种文件系统实现：1、默认的基于底层操作系统的文件系统实现；2、操作zip和jar文件的文件系统。
 */
public class AboutNio2FileSystems {

    public static void main(String[] args) {

    }

    /**
     * 用传统方式向已有的zip文件中添加新文件
     * @param zipFile
     * @param fileToAdd
     * @throws Exception
     */
    public static void addFileToZip(File zipFile,File fileToAdd) throws Exception{
        File tempFile = File.createTempFile(zipFile.getName(),null);
        tempFile.delete();
        zipFile.renameTo(tempFile);
        try(ZipInputStream input = new ZipInputStream(new FileInputStream(tempFile));
            ZipOutputStream output = new ZipOutputStream(new FileOutputStream(zipFile))){
            ZipEntry entry = input.getNextEntry();
            byte[] buf = new byte[8192];
            while(entry != null){
                String name = entry.getName();
                if(!name.equals(fileToAdd.getName())){
                    output.putNextEntry(new ZipEntry(name));
                    int len = 0;
                    while((len = input.read(buf)) > 0){
                        output.write(buf,0,len);
                    }
                }
            }
            try(InputStream newFileInput = new FileInputStream(fileToAdd)){
                output.putNextEntry(new ZipEntry(fileToAdd.getName()));
                int len = 0;
                while((len = newFileInput.read(buf)) > 0){
                    output.write(buf,0,len);
                }
                output.closeEntry();
            }
        }
        tempFile.delete();
    }

    /**
     * 使用zip/jar文件系统向已有的zip文件中添加新文件
     * @param zipFile
     * @param fileToAdd
     * @throws IOException
     */
    public static void addFileToZip2(File zipFile,File fileToAdd) throws IOException{
        Map<String,String> env = new HashMap<>();
        env.put("create","true");
        try(FileSystem fs = FileSystems.newFileSystem(URI.create("jar:"+zipFile.toURI()),env)){
            Path pathToAddFile = fileToAdd.toPath();
            Path pathInZipfile = fs.getPath("/"+fileToAdd.getName());
            Files.copy(pathToAddFile,pathInZipfile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
