package com.github.kokasumi.net.bio.server;

import org.apache.commons.lang.ArrayUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/16 14:55
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
            // 通过无限循环监听客户端连接
            while (true) {
                // 没有客户端接入，则主线程阻塞在ServerSocket的accept操作上
                socket = server.accept();
                new Thread(new TimerServerHandler(socket)).start();
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
