package org.xiaofengcanyue.io.nio;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Date;

public class AboutMulticastChannel {

    public static void main(String[] args) throws Exception {
        clientStart();
    }

    /**
     * 向 组播地址224.0.0.2 进行广播
     * @throws IOException
     */
    public static void serverStart() throws IOException{
        DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET).bind(null);
        InetAddress group = InetAddress.getByName("224.0.0.2");
        int port = 5000;
        while(true){
            try{
                Thread.sleep(2000);
            }catch (InterruptedException e){
                break;
            }
            String str = new Date().toString();
            dc.send(ByteBuffer.wrap(str.getBytes()),new InetSocketAddress(group,port));
        }
    }

    public static void clientStart() throws IOException{
        NetworkInterface ni = NetworkInterface.getByName("eth1");
        int port = 5000;
        try(DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR,true)
                .bind(new InetSocketAddress(port))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF,ni)){
            InetAddress group = InetAddress.getByName("224.0.0.1");
            MembershipKey key = dc.join(group,ni);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            dc.receive(buffer);
            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            String str = new String(data);
            System.out.println(str);
            key.drop();
        }
    }
}
