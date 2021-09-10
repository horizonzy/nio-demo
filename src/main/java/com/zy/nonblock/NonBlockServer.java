package com.zy.nonblock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO的核心 1.Channel
 * <p>  |-- SelectableChannel
 * <p>      |--SocketChannel
 * <p>      |--ServerSocketChannel
 * <p>      |--DatagramChannel
 * <p>      |--Pipe.SinkChannel
 * <p>      |--Pipe.SourceChannel
 * 2.Buffer
 * <p>
 * 3.Selector  SelectableChannel的多路复用选择器，用于监控SelectableChannel的IO状况
 */
public class NonBlockServer {

    public static void main(String[] args) throws IOException {
        server();
    }

    public static void server() throws IOException {
        ServerSocketChannel serverSocketChannel = SelectorProvider.provider().openServerSocketChannel();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9898));

        Selector selector = SelectorProvider.provider().openSelector();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                if (sk.isAcceptable()) {

                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) sk.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len;
                    while ((len = socketChannel.read(byteBuffer)) > 0) {
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                    if (len < 0) {
                        sk.cancel();
                        socketChannel.close();
                    }
                }
                iterator.remove();
            }
        }
        serverSocketChannel.close();
    }
}
