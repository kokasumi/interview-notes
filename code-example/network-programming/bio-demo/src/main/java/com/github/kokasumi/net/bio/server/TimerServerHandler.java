package com.github.kokasumi.net.bio.server;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/16 14:46
 * @description
 * @since v1.0
 */
public class TimerServerHandler implements Runnable{
    private Socket socket;

    public TimerServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            String currentTime = null;
            String body = null;
            while (true) {
                body = in.readLine();
                if(StringUtils.isEmpty(body)) {
                    break;
                }
                System.out.printf("The time server receive order: %s\n", body);
                // 如果请求消息为查询时间的指令”QUERY TIME ORDER“，则获取当前最新的系统时间
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
                        : "BAD ORDER";
                out.println(currentTime);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(Objects.nonNull(in)) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(Objects.nonNull(out)) {
                out.close();
                out = null;
            }
            if(Objects.nonNull(socket)) {
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
