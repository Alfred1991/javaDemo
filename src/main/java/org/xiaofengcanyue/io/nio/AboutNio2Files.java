package org.xiaofengcanyue.io.nio;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;

public class AboutNio2Files {

    public static void main(String[] args) throws Exception{
        useFileAttributeView();
    }

    /**
     * java.nio.file.Path的一些用法
     */
    public static void usePath(){
        Path path1 = Paths.get("folder1","stub1");
        Path path2 = Paths.get("folder2","stub2");
        path1.resolve(path2);
        path1.resolveSibling(path2);
        path1.relativize(path2);
        path1.subpath(0,1);
        path1.startsWith(path2);
        path1.endsWith(path2);
        Paths.get("folder1/./../folder2/my.text").normalize();
    }

    /**
     * 使用DirectoryStream来遍历目录的子项，当目录的子项发生变化时，迭代器的结果不一定会随之改变。
     * @throws IOException
     */
    public static void listFiles() throws IOException{
        Path path = Paths.get("src/main/java/org/xiaofengcanyue/io/nio");
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(path,"*.java")){
            for(Path entry:stream){
                System.out.println(entry.toAbsolutePath().toString());
            }
        }
    }


    /**
     * 继承 SimpleFileVisitor 对目录进行遍历
     */
    public static class SvnInfoCleanVisitor extends SimpleFileVisitor<Path>{
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if(isSvnFolder(dir)){
                cleanMark = true;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if(cleanMark){
                Files.setAttribute(file,"dos:readonly",false);
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if(exc == null && cleanMark){
                Files.delete(dir);
                if(isSvnFolder(dir)){
                    cleanMark = false;
                }
            }
            return FileVisitResult.CONTINUE;
        }

        private boolean cleanMark = false;

        private boolean isSvnFolder(Path dir){
            return ".svn".equals(dir.getFileName().toString());
        }

    }

    /**
     * 通过fileattributeview 来获取文件属性视图，并根据视图查看文件属性
     * @throws IOException
     */
    public static void useFileAttributeView() throws IOException{
        Path path = Paths.get("content.html");
        PosixFileAttributeView view = Files.getFileAttributeView(path,PosixFileAttributeView.class);
        if(view != null){
            PosixFileAttributes attrs = view.readAttributes();
            System.out.println("isother:"+attrs.isOther());
        }


        DosFileAttributeView view1 = Files.getFileAttributeView(path,DosFileAttributeView.class);
        if(view1 != null){
            DosFileAttributes attrs = view1.readAttributes();
            System.out.println("isreadonly:"+attrs.isReadOnly());
        }
    }


    /**
     * 直接读取文件属性，指定属性名需带名称空间（例如dos:），否则默认使用 基本属性视图
     * @param path
     * @param intervalInMillis
     * @return
     * @throws IOException
     */
    public static boolean checkUpdateRequired(Path path,int intervalInMillis) throws IOException{
        FileTime lastModifiedTime = (FileTime) Files.getAttribute(path,"lastModifiedTime");
        long now = System.currentTimeMillis();
        return now - lastModifiedTime.toMillis() > intervalInMillis;
    }

    /**
     * 用类似选择器的方式来监控目录
     * @throws IOException
     * @throws InterruptedException
     */
    public static void calculate() throws IOException,InterruptedException{
        WatchService service = FileSystems.getDefault().newWatchService();
        Path path = Paths.get("").toAbsolutePath();
        path.register(service,StandardWatchEventKinds.ENTRY_CREATE);
        while(true){
            WatchKey key = service.take();
            for(WatchEvent<?> event : key.pollEvents()){
                Path createdPath = (Path) event.context();
                createdPath = path.resolve(createdPath);
                long size = Files.size(createdPath);
                System.out.println(createdPath + " ===> "+size);
            }
            key.reset();
        }
    }

    /**
     * Files类中提供了一系列方法来支持文件操作
     * @throws IOException
     */
    public static void manipulateFiles() throws IOException{
        Path newFile = Files.createFile(Paths.get("new.txt").toAbsolutePath());
        List<String> content = new ArrayList<>();
        content.add("hello");
        content.add("world");
        Files.write(newFile,content, Charset.forName("UTF-8"));
        Files.size(newFile);
        byte[] bytes = Files.readAllBytes(newFile);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Files.copy(newFile,output);
        Files.delete(newFile);
    }

}
