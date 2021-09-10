package com.zy.channel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

public class ChannelDemo {

    /**
     * 字符集编码解码
     */
    public static void charsetTest() {
        Map<String, Charset> map = Charset.availableCharsets();
        for (Entry<String, Charset> set : map.entrySet()) {
            System.out.println(set.getKey() + ":" + set.getValue());
        }

        Charset charset = Charset.forName("GBK");

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("你好兰邦苓");
        charBuffer.flip();

        ByteBuffer byteBuffer = charset.encode(charBuffer);

        CharBuffer decode = charset.decode(byteBuffer);
        System.out.println(decode.toString());

    }

    /**
     * 分散的聚集
     */
    public static void fileChannel4() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(
                "/Users/apple/Documents/practice/test.txt", "rw");
        FileChannel inChannel = randomAccessFile.getChannel();

        ByteBuffer buffer1 = ByteBuffer.allocate(10);

        ByteBuffer buffer2 = ByteBuffer.allocate(7);

        inChannel.read(new ByteBuffer[]{buffer1, buffer2});

        FileChannel outChannel = FileChannel
                .open(Paths.get("/Users/apple/Documents/practice/test2.txt"),
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        buffer1.flip();
        buffer2.flip();
        outChannel.write(new ByteBuffer[]{buffer1, buffer2});

        outChannel.close();
        inChannel.close();
        randomAccessFile.close();

    }


    /**
     * channel之间直接进行数据传输(直接缓冲区)
     */
    public static void fileChannel3() throws IOException {
        FileChannel inChannel = FileChannel
                .open(Paths.get("/Users/apple/Documents/practice/test.jpg"),
                        StandardOpenOption.READ);

        FileChannel outChannel = FileChannel
                .open(Paths.get("/Users/apple/Documents/practice/test3.jpg"),
                        StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        inChannel.transferTo(0, inChannel.size(), outChannel);

        outChannel.close();
        inChannel.close();
    }

    /**
     * 利用内存映射文件直接进行数据的传输
     */
    public static void fileChannel2() throws IOException {
        FileChannel inChannel = FileChannel
                .open(Paths.get("/Users/apple/Documents/practice/test.jpg"),
                        StandardOpenOption.READ);

        FileChannel outChannel = FileChannel
                .open(Paths.get("/Users/apple/Documents/practice/test3.jpg"),
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                        StandardOpenOption.READ);

        //内存映射文件，直接操作内存即可
        MappedByteBuffer inMapBuffer = inChannel.map(MapMode.READ_ONLY, 0, inChannel.size());

        //内存映射文件，直接操作内存
        MappedByteBuffer outMapBuffer = outChannel.map(MapMode.READ_WRITE, 0, inChannel.size());

        byte[] tmp = new byte[inMapBuffer.limit()];

        System.out.println(inMapBuffer.position());
        System.out.println(inMapBuffer.limit());
        System.out.println(inMapBuffer.capacity());

        System.out.println(outMapBuffer.position());
        System.out.println(outMapBuffer.limit());
        System.out.println(outMapBuffer.capacity());

        inMapBuffer.get(tmp);
        outMapBuffer.put(tmp);

        System.out.println("============");

        System.out.println(inMapBuffer.position());
        System.out.println(inMapBuffer.limit());
        System.out.println(inMapBuffer.capacity());

        System.out.println(outMapBuffer.position());
        System.out.println(outMapBuffer.limit());
        System.out.println(outMapBuffer.capacity());

        outChannel.close();
        inChannel.close();
    }

    /**
     * 利用channel进行数据传输
     */
    public static void fileChannel1() throws IOException {
        FileInputStream fis = new FileInputStream("/Users/apple/Documents/practice/test.jpg");
        FileChannel inChannel = fis.getChannel();

        FileOutputStream fos = new FileOutputStream("/Users/apple/Documents/practice/test1.jpg");
        FileChannel outChannel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (inChannel.read(buffer) != -1) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
        }

        outChannel.close();
        fos.close();
        inChannel.close();
        fis.close();
    }

}
