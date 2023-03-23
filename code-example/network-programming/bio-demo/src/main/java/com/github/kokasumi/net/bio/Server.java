package com.github.kokasumi.net.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/13 9:59
 * @description BIO服务端代码
 * 测试命令：
 * 1. 启动windows cmd命令行，使用 telnet 127.0.0.1 6666 连接服务端
 * 2. 连接到BIO服务器之后，通过 Ctrl+] 符号进入发送数据界面
 * 3. 使用 send Hello World向服务端发送数据
 * @since v1.0
 */
public class Server {
    public static void main(String[] args) throws IOException {
        // 创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 创建ServerSocket-
        ServerSocket serverSocket = new ServerSocket(6666);
        while (true) {
            System.out.println("等待连接中...");
            // 监听。等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端");
            executorService.execute(() -> handler(socket));
        }
    }

    /**
     * 编写handler方法，和客户端通讯
     * @param socket
     */
    private static void handler(Socket socket) {
        byte[] bytes = new byte[1024];
        System.out.println("当前线程信息：" + Thread.currentThread().getName());
        try {
            // 通过Socket获取输入流
            InputStream inputStream = socket.getInputStream();
            // 循环读取客户端发送数据
            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                System.out.println(Thread.currentThread().getName() + "：发送信息为：" + new String(bytes, 0 ,read));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭连接");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
