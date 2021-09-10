package com.zy.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;

/**
 * Pipe的的SinkChannel和SourceChannel的操作是一对一的，SinkChannel中write一次， SourceChannel就可以读一次
 */
public class PipeChannelDemo {

    public static void main(String[] args) throws IOException {
        Pipe pipe = Pipe.open();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SinkChannel sinkChannel = pipe.sink();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put("第一次通过管道发送数据".getBytes());
            byteBuffer.flip();
            try {
                sinkChannel.write(byteBuffer);
                System.out.println("第一次发送数据完毕");
                byteBuffer.clear();
                byteBuffer.put("第二次通过管道发送数据".getBytes());
                byteBuffer.flip();
                sinkChannel.write(byteBuffer);
                System.out.println("第二次发送数据完毕");

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sinkChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            System.out.println("1111");
            SourceChannel sourceChannel = pipe.source();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            try {
                System.out.println("22222");
                //read操作会阻塞，等待sinkChannel中将数据写完
                sourceChannel.read(byteBuffer);
                System.out.println("33333");
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
                sourceChannel.read(byteBuffer);
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sourceChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }
}
