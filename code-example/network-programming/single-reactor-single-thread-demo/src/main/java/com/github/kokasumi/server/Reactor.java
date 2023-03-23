package com.github.kokasumi.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/17 16:34
 * @description
 * @since v1.0
 */
public class Reactor implements Runnable{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile  boolean stop;

    public Reactor(int port){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            serverSocketChannel.configureBlocking(false);
            SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            key.attach(new Acceptor(selector, key));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                // 设置selector的休眠时间为1s，无论是否有读写等事件发生，selector每隔1s被唤醒一次
                selector.select(1000);
                // 当有处于就绪状态的channel时，selector就返回就绪状态的Channel的SelectionKey集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterable = selectionKeys.iterator();
                SelectionKey key=null;
                // 通过对就绪状态的Channel集合进行迭代，可以进行网络的异步读写操作
                while (keyIterable.hasNext()) {
                    key = keyIterable.next();
                    keyIterable.remove();
                    dispatch(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        Runnable runnable = (Runnable) selectionKey.attachment();
        if(Objects.nonNull(runnable)) {
            runnable.run();
        }
    }
}
