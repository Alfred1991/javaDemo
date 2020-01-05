package org.xiaofengcanyue.io.nio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * 0 <= mark <= position <= limit <= capacity
 */
public class AboutBuffer {

    public static void main(String[] args) {
        viewBuffer();
    }

    public static void useByteBuffer(){
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put((byte)1);
        buffer.put(new byte[3]);
        buffer.putChar('A');
        buffer.putFloat(0.0f);
        buffer.putLong(10,100L);
        Character c = buffer.getChar(4);

        System.out.println(c);
    }


    /**
     * 大端表示字节序列中高位在前，小端相反。
     */
    public static void byteOrder(){
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(1);

        System.out.println(buffer.order());

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        Integer i =buffer.getInt(0);

        System.out.println(i);
        System.out.println(ByteOrder.nativeOrder());
    }

    /**
     * 接收者在完成读取之后需要进行compact，compact之后position处于可写的位置，读则需要从头开始。
     */
    public static void compact(){
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put(new byte[16]);
        buffer.flip();
        buffer.getInt();
        buffer.compact();
        int pos = buffer.position();

        System.out.println(pos);
    }

    /**
     * 创建缓冲区视图，视图的定位大致相当于io里面的过滤流(eg:bufferedinputstream)
     * 新缓冲区的起始位置是原缓冲区的position，新旧 缓冲区的 position，limit和mark位置彼此独立，修改 新/旧缓冲区 会彼此影响。
     */
    public static void viewBuffer(){
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.putInt(1);
        IntBuffer intBuffer = buffer.asIntBuffer();
        intBuffer.put(2);
        int value = buffer.getInt();

        System.out.println(value);
        System.out.println(intBuffer.capacity());
    }

}

