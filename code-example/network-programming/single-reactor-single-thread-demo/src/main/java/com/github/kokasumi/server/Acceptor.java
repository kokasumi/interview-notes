package com.github.kokasumi.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/17 16:28
 * @description
 * @since v1.0
 */
public class Acceptor implements Runnable{
    private final SelectionKey selectionKey;
    private final Selector selector;

    public Acceptor(Selector selector,SelectionKey selectionKey) {
        this.selector = selector;
        this.selectionKey = selectionKey;
    }

    @Override
    public void run() {
        // 接受新连接
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 将新创建的SocketChannel设置为异步非阻塞，同时也可以对齐TCP参数进行设置，例如TCP接收和发送缓冲区的大小等
            socketChannel.configureBlocking(false);
            // 添加新连接到selector选择器
            SelectionKey key = socketChannel.register(selector,SelectionKey.OP_READ);
            key.attach(new TimerServerHandler(key));
            selector.wakeup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
