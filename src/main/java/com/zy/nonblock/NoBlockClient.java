package com.zy.nonblock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Date;
import java.util.Scanner;

public class NoBlockClient {

    public static void main(String[] args) throws IOException {
        client("兰邦苓");
    }

    public static void client(String clientName) throws IOException {
        //1.获取通道
        SocketChannel socketChannel = SelectorProvider.provider().openSocketChannel();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9898));

        //2.切换非阻塞模式
        socketChannel.configureBlocking(false);


        //3.分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.nextLine();
            byteBuffer.put((clientName + " " + new Date().toString() + "\n" + str).getBytes("UTF-8"));
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        socketChannel.close();
    }
}
