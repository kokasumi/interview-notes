package com.github.kokasumi.server;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/17 15:42
 * @description
 * @since v1.0
 */
public class TimerServerHandler implements Runnable{
    private final SelectionKey selectionKey;
    private final SocketChannel channel;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);


    public TimerServerHandler(SelectionKey selectionKey) throws IOException {
        this.selectionKey = selectionKey;
        channel = (SocketChannel) selectionKey.channel();
    }

    @Override
    public void run() {
        try {
            int readBytes = channel.read(readBuffer);
            // readBytes > 0，读到了字节，对字节进行解码
            // readBytes = 0, 没有读取到字节，属于正常场景，忽略
            // readBytes = -1，链路已经关闭，需要关闭SocketChannel，释放资源
            if(readBytes > 0) {
                // 将缓冲区当前的limit设置为position，position设置为0，用于后续对缓冲区的读取操作
                readBuffer.flip();
                // 根据缓冲区可读的字节个数创建字节数组
                byte[] bytes = new byte[readBuffer.remaining()];
                // 调用ByteBuffer的get操作将缓冲区可读的字节数组复制到新创建的字节数组中
                readBuffer.get(bytes);
                String body = new String(bytes, "UTF-8").trim();
                System.out.printf("The time server receive order: %s\n", body);
                // 如果请求消息为查询时间的指令”QUERY TIME ORDER“，则获取当前最新的系统时间
                String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
                        : "BAD ORDER";
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                doWrite(currentTime);
            }else if(readBytes < 0) {
                // 对端链路关闭
                selectionKey.cancel();
                channel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 需要指出的是，由于SocketChannel是异步非阻塞的，它并不保证一次性能够把需要发送的字节数组发送完，
     * 此时会出现“写半包”问题，我们需要注册写操作，不断轮询Selector，将没有发送完毕的ByteBuffer发送完毕，
     * 可以通过ByteBuffer的hasRemaining()方法判断消息是否发送完成。
     * @param response
     * @throws IOException
     */
    private void doWrite(String response) throws IOException {
        if(StringUtils.isNotEmpty(response)) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            //调用ByteBuffer的put操作将字节数组复制到缓冲区
            writeBuffer.put(bytes);
            writeBuffer.flip();
            while (writeBuffer.hasRemaining()) {
                channel.write(writeBuffer);
            }
        }
    }
}
