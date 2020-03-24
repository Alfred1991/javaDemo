package org.xiaofengcanyue.concurrency;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Phaser;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AboutNewFeatureInJava1_7 {

    /**
     * java se 7的一个重要更新是增加了一个轻量级任务执行框架，一般称为fork/join框架（任务ForkJoinTask，及其子类RecursiveTask、RecursiveAction和执行ForkJoinPool）。
     * 该框架目的在于利用好底层平台上的多核CPU和多处理器来进行并行处理，解决问题时通常使用分治（divide and conquer）算法或map/reduce算法来进行。
     * fork操作的作用是把一个大的问题划分成若干个较小的问题，该过程一般是递归的。
     * join操作的作用是把这些部分解收集并组织起来，得到最终的完整解。
     */
    public static class MaxValue{
        private static final int RANGE_LENGTH = 2000;
        private final ForkJoinPool forkJoinPool = new ForkJoinPool();
        private static class MaxValueTask extends RecursiveTask<Long>{
            private final long[] array;
            private final int start;
            private final int end;
            MaxValueTask(long[] array,int start,int end){
                this.array = array;
                this.start = start;
                this.end = end;
            }

            @Override
            protected Long compute() {
                long max = Long.MIN_VALUE;
                if(end - start <= RANGE_LENGTH){
                    for(int i = start;i<end;i++){
                        if(array[i] > max){
                            max = array[i];
                        }
                    }
                }else{
                    int mid = (start + end)/2;
                    MaxValueTask lowTask = new MaxValueTask(array,start,mid);
                    MaxValueTask highTask = new MaxValueTask(array,mid,end);
                    lowTask.fork();
                    highTask.fork();
                    max = Math.max(max,lowTask.join());
                    max = Math.max(max,highTask.join());
                }
                return max;
            }
        }
        public void calculate(long[] array){
            MaxValueTask task = new MaxValueTask(array,0,array.length);
            Long result = forkJoinPool.invoke(task);
            System.out.println(result);
        }
    }

    /**
     * 多阶段线程同步工具：
     * 在fork/join框架的子任务之间进行同步时，应该优先使用Phaser类的对象。
     * Phaser类的特点是把多个线程协作执行的任务划分为多个阶段(phase)，在每个阶段可以由任意个参与者参与。线程可以随时注册并参与到某个阶段的执行中来。
     * 当一个阶段中所有的线程都执行完成后，Phaser类的对象会自动进入下一个阶段。如此循环下去，直到Phaser类的对象中不再包含任何参与者，此时Phaser类的对象的运行自动结束。
     */
    public static class WebPageImageDownloader {

        private final Phaser phaser = new Phaser(1);
        private final Pattern imageUrlPattern = Pattern.compile("src=['\"]?(.*?(\\.jpg|\\.gif|\\.png))['\"]?[\\s>]+",Pattern.CASE_INSENSITIVE);

        public void download(URL url, final Path path, Charset charset) throws IOException{
            if(charset == null){
                charset = StandardCharsets.UTF_8;
            }
            String content = getContent(url,charset);
            List<URL> imageUrls = extractImageUrls(content);
            for(final URL imageUrl : imageUrls){
                phaser.register();
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            InputStream is = imageUrl.openStream();
                            Files.copy(is,getSavedPath(path,imageUrl), StandardCopyOption.REPLACE_EXISTING);
                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            phaser.arriveAndDeregister();
                        }
                    }
                }.start();
            }
        }

        private String getContent(URL url,Charset charset)throws IOException{
            InputStream is = url.openStream();
            return new InputStreamReader(is,charset.name()).toString();
        }
        private List<URL> extractImageUrls(String content){
            List<URL> result = new ArrayList<>();
            Matcher matcher = imageUrlPattern.matcher(content);
            while(matcher.find()){
                try{
                    result.add(new URL(matcher.group(1)));
                }catch (MalformedURLException e){

                }
            }
            return result;
        }
        private Path getSavedPath(Path parent,URL url){
            //获取图片保存路径
            return Paths.get("");
        }
    }

    /**
     * 在不同线程访问一个ThreadLocal类的对象时，所访问和修改的是每个线程各自独立的对象。
     */
    public static class ThreadLocalIdGenerator {
        private static final ThreadLocal<List> list = new ThreadLocal<List>(){
            @Override
            protected List initialValue() {
                return new ArrayList();
            }
        };
        public static int getSize(){
            return list.get().size();
        }
    }

    /**
     * 多线程环境下产生随机数。
     */
    public static int getRandom(){
        return ThreadLocalRandom.current().nextInt();
    }
}
