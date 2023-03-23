package com.github.kokasumi.client;

import org.apache.commons.lang.ArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/16 15:01
 * @description
 * @since v1.0
 */
public class TimerClient {
    public static void main(String[] args) {
        int port = 9090;
        if(ArrayUtils.isNotEmpty(args)) {
            try {
                port = Integer.valueOf(args[0]);
            }catch (Exception e) {
                port = 9090;
            }
        }
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("127.0.0.1", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
            out.println("QUERY TIME ORDER");
            System.out.println("Send order to server succeed.");
            String resp = in.readLine();
            System.out.printf("Now is: %s\n", resp);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(Objects.nonNull(out)) {
                out.close();
                out = null;
            }
            if(Objects.nonNull(in)) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }
            if(Objects.nonNull(socket)) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
    }
}
