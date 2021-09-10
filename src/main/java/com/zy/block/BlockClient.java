package com.zy.block;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BlockClient {

    public static void main(String[] args) throws IOException {
        client();
    }

    public static void client() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        FileChannel inChannel = FileChannel
                .open(Paths.get("/Users/apple/Documents/practice/test.jpg"),
                        StandardOpenOption.READ);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (inChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        socketChannel.shutdownOutput();

        int len = 0;
        while ((len = socketChannel.read(byteBuffer)) != -1) {
            System.out.println(new String(byteBuffer.array(), 0, len));
        }
        socketChannel.shutdownInput();

        inChannel.close();
        socketChannel.close();
    }
}
