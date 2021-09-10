package com.zy.block;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BlockServer {

    public static void main(String[] args) throws IOException {
        server();
    }


    public static void server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9898));
        SocketChannel socketChannel = serverSocketChannel.accept();

        FileChannel outChannel = FileChannel
                .open(Paths.get("/Users/apple/Documents/practice/test3.jpg"),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (socketChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            outChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        socketChannel.shutdownInput();

        byteBuffer.put("服务端收到了信息".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        socketChannel.shutdownOutput();

        outChannel.close();
        socketChannel.close();
        serverSocketChannel.close();

    }
}
