package com.github.kokasumi.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/17 17:25
 * @description 处理异步连接和读写操作
 * @since v1.0
 */
public class TimerClientHandler implements Runnable{
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimerClientHandler(String host, int port) {
        this.host=host==null ? "127.0.0.1" : host;
        this.port=port;
        try {
            selector=Selector.open();
            socketChannel=SocketChannel.open();
            //设置为异步非阻塞模式，同时还可以设置SocketChannel的TCP参数。例如接收和发送的TCP缓冲区大小
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try {
            listenRead();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        /*
         * 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动注册并关闭，所以不需要重复释放资源。
         * 由于多路复用器上可能注册成千上万的Channel或者pipe，如果一一对这些资源进行释放显然不合适。
         * 因此，JDK底层会自动释放所有跟此多路复用器关联的资源。
         */
        if(selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listenRead() throws IOException {
        while (!stop) {
            selector.select(1000);
            Set<SelectionKey> selectedKeys=selector.selectedKeys();
            Iterator<SelectionKey> it=selectedKeys.iterator();
            SelectionKey key=null;
            //轮询多路复用器Selector，当有就绪的Channel时
            while(it.hasNext()) {
                key = it.next();
                it.remove();
                handleInput(key);
            }
        }
    }

    /**
     * 处理连接消息
     * @param channel
     * @throws IOException
     */
    private void handleConnect(SocketChannel channel) throws IOException {
        //对连接结果进行判断
        if(channel.finishConnect()){
            /*
             * 将SocketChannel注册到多路复用器上，注册SelectionKey.OP_READ操作位，
             * 监听网络读操作，然后发送请求消息给服务端。
             */
            channel.register(selector, SelectionKey.OP_READ);
            doWrite(channel);
        }else{
            //连接失败，进程退出
            System.exit(1);
        }
    }

    /**
     * 处理消息
     * @param key
     * @throws IOException
     */
    private void handleInput(SelectionKey key) throws IOException {
        //判断是否连接成功
        if(key.isValid()) {
            SocketChannel sc=(SocketChannel) key.channel();
            //处于连接状态，说明服务器已经返回ACK应答消息
            if(key.isConnectable()){
                handleConnect(sc);
            }
            if(key.isReadable()) {
                handleRead(key,sc);
            }
        }
    }

    /**
     * 处理读取事件
     * @param key
     * @param channel
     * @throws IOException
     */
    private void handleRead(SelectionKey key,SocketChannel channel) throws IOException {
        //开辟缓冲区
        ByteBuffer readBuffer=ByteBuffer.allocate(1024);
        //异步读取
        int readBytes=channel.read(readBuffer);
        if(readBytes>0){
            readBuffer.flip();
            byte[] bytes=new byte[readBuffer.remaining()];
            readBuffer.get(bytes);
            String body=new String(bytes, "UTF-8");
            System.out.println("Now is: "+body);
            this.stop=true;
        }else if(readBytes<0){
            //对端链路关闭
            key.cancel();
            channel.close();
        }
    }

    private void doConnect() throws IOException {
        //如果直接连接成功，则将SocketChannel注册到多路复用器Selector上，发送请求消息，读应答
        if(socketChannel.connect(new InetSocketAddress(host, port))){
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else{
            /*
             * 如果没有直接连接成功，则说明服务端没有返回TCP握手应答信息，但这并不代表连接失败，
             * 我们需要将SocketChannel注册到多路复用器Selector上，注册SelectionKey.OP_CONNECT，
             * 当服务端返回TCP syn-ack消息后，Selector就能轮询到整个SocketChannel处于连接就绪状态。
             */
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws IOException {
        byte[] req="QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer= ByteBuffer.allocate(req.length);
        //写入到发送缓冲区中
        writeBuffer.put(req);
        writeBuffer.flip();
        //由于发送是异步的，所以会存在"半包写"问题
        while (writeBuffer.hasRemaining()) {
            socketChannel.write(writeBuffer);
        }
        System.out.println("Send order to server succeed.");
    }
}
