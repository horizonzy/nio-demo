package com.zy.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class UdpChannelDemo {

    public static void send() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            byteBuffer.put(input.getBytes());
            byteBuffer.flip();
            datagramChannel.send(byteBuffer, new InetSocketAddress("127.0.0.1", 9898));
            byteBuffer.clear();
        }

        datagramChannel.close();

    }

    public static void receive() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);

        datagramChannel.bind(new InetSocketAddress(9898));

        Selector selector = Selector.open();

        SelectionKey key = datagramChannel.register(selector, SelectionKey.OP_READ);
        key.interestOps(SelectionKey.OP_READ);

        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();
                if (sk.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    DatagramChannel channel = (DatagramChannel) sk.channel();
                    channel.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, buffer.limit()));

                }
            }
            iterator.remove();
        }

    }

}
