package org.xiaofengcanyue.io.nio;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AboutSocketChannel {

    public static void main(String[] args) throws Exception{
        //loadWebPageUseSocket("www.baidu.com");

        startSimpleServer();
    }

    /**
     * 客户端阻塞式读取网络资源
     * @param url
     * @throws IOException
     */
    public static void loadWebPageUseSocket(String url) throws IOException{
        try(FileChannel destChannel = FileChannel.open(Paths.get("content_socket.html"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);
            SocketChannel sc = SocketChannel.open(new InetSocketAddress(url,80))){
            String request = "GET / HTTP/1.1\r\n\r\nHost: www.baidu.com\r\n\r\n";
            ByteBuffer header = ByteBuffer.wrap(request.getBytes("UTF-8"));
            sc.write(header);
            destChannel.transferFrom(sc,0,Integer.MAX_VALUE);
        }
    }

    /**
     * 服务端阻塞式返回固定字符串
     * @throws IOException
     */
    public static void startSimpleServer() throws IOException{
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress("localhost",10800));
        while(true){
            try(SocketChannel sc = channel.accept()){
                sc.write(ByteBuffer.wrap("Hello".getBytes("UTF-8")));
            }
        }
    }


    /**
     * 使用 选择器非阻塞客户端 访问服务器
     */
    public static class LoadWebPageUseSelector{
        public void load(Set<URL> urls) throws IOException{
            Map<SocketAddress,String> mapping = urlToSocketAddress(urls);
            Selector selector = Selector.open();
            for(SocketAddress address:mapping.keySet()){
                register(selector,address);
            }
            int finished = 0, total = mapping.size();
            ByteBuffer buffer = ByteBuffer.allocate( 32 * 1024);
            int len = -1;
            while(finished < total){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isValid() && key.isReadable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
                        String filename = address.getHostName() + ".txt";
                        FileChannel destChannel = FileChannel.open(Paths.get(filename),StandardOpenOption.APPEND,StandardOpenOption.CREATE);
                        buffer.clear();
                        while((len = channel.read(buffer)) > 0 || buffer.position() != 0){
                            buffer.flip();
                            destChannel.write(buffer);
                            buffer.compact();
                        }
                        if(len == -1){
                            finished++;
                            key.cancel();
                        }
                    }else if(key.isValid() && key.isConnectable()){
                        SocketChannel channel = (SocketChannel) key.channel();
                        boolean success = channel.finishConnect();
                        if(!success){
                            finished++;
                            key.cancel();
                        }else{
                            InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
                            String path = mapping.get(address);
                            String request = "GET "+path+" HTTP/1.0\r\n\r\nHOST:"+address.getHostString()+"\r\n\r\n";
                            ByteBuffer header = ByteBuffer.wrap(request.getBytes("UTF-8"));
                            channel.write(header);
                        }
                    }
                }
            }
        }

        private void register(Selector selector,SocketAddress address)throws IOException{
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(address);
            channel.register(selector,SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
        }

        private Map<SocketAddress,String> urlToSocketAddress(Set<URL> urls){
            Map<SocketAddress,String> mapping = new HashMap<>();
            for(URL url:urls){
                int port = url.getPort() != -1 ? url.getPort():url.getDefaultPort();
                SocketAddress address = new InetSocketAddress(url.getHost(),port);
                String path = url.getPath();
                if(url.getQuery() != null){
                    path = path + "?" +url.getQuery();
                }
                mapping.put(address,path);
            }
            return mapping;
        }
    }

}
