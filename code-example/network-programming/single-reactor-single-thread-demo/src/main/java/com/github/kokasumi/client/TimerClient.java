package com.github.kokasumi.client;

import org.apache.commons.lang.ArrayUtils;

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
        new Thread(new TimerClientHandler("127.0.0.1", port),"TimClient-001").start();
    }
}
