package org.xiaofengcanyue.ioutilities;

import com.google.common.io.Closer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AboutClosingResources {

    public static void main(String[] args) {

    }

    /**
     * 主要用于java1.7之前的环境，因为java1.7提供了try-with-resources功能
     */
    public static void useOfCloser() throws IOException {
        Closer closer = Closer.create();
        try{
            InputStream in = closer.register(Files.newInputStream(Paths.get("")));
        }catch(Throwable e){
            throw closer.rethrow(e);
        }finally {
            closer.close();
        }
    }


}
