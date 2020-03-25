package org.xiaofengcanyue.primitives;


import com.google.common.primitives.Ints;

/**
 * java中的primitive类型包括：byte,short,int,long,float,double,char,boolean
 * guava提供的相关工具类：
 * Primitive Type	Guava Utilities (all in com.google.common.primitives)
 * byte	            Bytes, SignedBytes, UnsignedBytes
 * short	        Shorts
 * int	            Ints, UnsignedInteger, UnsignedInts
 * long	            Longs, UnsignedLong, UnsignedLongs
 * float	        Floats
 * double	        Doubles
 * char	            Chars
 * boolean	        Booleans
 *
 */
public class AboutPrimitives {
    public static void main(String[] args) {
        int a = Ints.BYTES;
        System.out.println(a);
    }
}
