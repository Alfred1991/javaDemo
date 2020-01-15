package org.xiaofengcanyue.io.nio;

import org.omg.CORBA.OBJ_ADAPTER;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticFileHttpServer {

    public static void main(String[] args) throws Exception{
        start(Paths.get("").toAbsolutePath());
    }

    private static final Logger LOGGER = Logger.getLogger(StaticFileHttpServer.class.getName());
    private static final Pattern PATH_EXTRACTOR = Pattern.compile("GET (.*?) HTTP");
    private static final String INDEX_PAGE = "index.html";

    public static void start(final Path root) throws IOException{
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(10080));
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                serverChannel.accept(null,this);
                try{
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    result.read(buffer).get();
                    buffer.flip();
                    String request = new String(buffer.array());
                    LOGGER.log(Level.INFO,"请求：{0}",request);
                    String requestPath = extractPath(request);
                    LOGGER.log(Level.INFO,"处理请求：{0}",requestPath);
                    Path filePath = getFilePath(root,requestPath);
                    LOGGER.log(Level.INFO,"文件路径：{0}\r\n 是否存在：{1}",new Object[]{filePath.toAbsolutePath(),Files.exists(filePath)});
                    if(!Files.exists(filePath)){
                        String error404 = generateErrorResponse(404,"Not Found");
                        result.write(ByteBuffer.wrap(error404.getBytes()));
                        return;
                    }
                    String header = generateFileContentResponseHeader(filePath);
                    result.write(ByteBuffer.wrap(header.getBytes())).get();
                    Files.copy(filePath, Channels.newOutputStream(result));

                }catch (Exception e){
                    String error = generateErrorResponse(500,"Internal Server Error");
                    result.write(ByteBuffer.wrap(error.getBytes()));
                    LOGGER.log(Level.SEVERE,e.getMessage(),e);
                }finally {
                    try{
                        result.close();
                    }catch (IOException e){
                        LOGGER.log(Level.WARNING,e.getMessage(),e);
                    }
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                LOGGER.log(Level.SEVERE,exc.getMessage(),exc);
            }
        });
        LOGGER.log(Level.INFO,"服务器已经启动，文件根目录为："+root);
    }

    private static String extractPath(String request){
        Matcher matcher = PATH_EXTRACTOR.matcher(request);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

    private static Path getFilePath(Path root,String requestPath){
        if(requestPath == null || "/".equals(requestPath)){
            requestPath = INDEX_PAGE;
        }
        if(requestPath.startsWith("/")){
            requestPath = requestPath.substring(1);
        }
        int pos = requestPath.indexOf("?");
        if(pos != -1){
            requestPath = requestPath.substring(0,pos);
        }
        return root.resolve(requestPath);
    }

    private static String getContentType(Path filePath) throws IOException{
        return Files.probeContentType(filePath);
    }

    private static String generateFileContentResponseHeader(Path filePath) throws IOException{
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 200 OK\r\n");
        builder.append("Content-Type: ");
        builder.append(getContentType(filePath));
        builder.append("\r\n");
        builder.append("Content-Length: "+Files.size(filePath)+"\r\n");
        builder.append("\r\n");
        return builder.toString();
    }

    private static String generateErrorResponse(int statusCode,String message){
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 "+statusCode+" "+message+"\r\n");
        builder.append("Content-Type: text/plain\r\n");
        builder.append("Content-Length: "+message.length()+"\r\n");
        builder.append("\r\n");
        builder.append(message);
        return builder.toString();
    }
}
