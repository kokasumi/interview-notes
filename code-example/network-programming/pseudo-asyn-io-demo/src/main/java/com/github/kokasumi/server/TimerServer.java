package com.github.kokasumi.server;

import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/17 14:55
 * @description
 * @since v1.0
 */
public class TimerServer {
    public static void main(String[] args) {
        int port = 9090;
        if(ArrayUtils.isNotEmpty(args)) {
            try {
                port = Integer.valueOf(args[0]);
            }catch (Exception e) {
                port = 9090;
            }
        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.printf("The time server is start in port: %d\n", port);
            Socket socket = null;
            // 创建一个时间服务器类的线程池
            TimerServerHandlerExecutePool singleExecutor = new TimerServerHandlerExecutePool(50,10000);
            // 通过无限循环监听客户端连接
            while (true) {
                // 没有客户端接入，则主线程阻塞在ServerSocket的accept操作上
                socket = server.accept();
                // 当接收到新的客户端连接时，将请求Socket封装成一个Task，然后调用execute方法执行。从而避免了每个请求接入都创建一个新的线程
                singleExecutor.execute(new TimerServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(Objects.nonNull(server)) {
                System.out.println("The time server closed");
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
