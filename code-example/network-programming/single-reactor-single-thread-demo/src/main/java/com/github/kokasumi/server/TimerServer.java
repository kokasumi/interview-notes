package com.github.kokasumi.server;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/17 17:05
 * @description
 * @since v1.0
 */
public class TimerServer {
    public static void main(String[] args) {
        int port=9090;
        if(args!=null&&args.length>0){
            try {
                port=Integer.valueOf(args[0]);
            } catch (Exception e) {
                // 采用默认值
            }
        }

        Reactor reactor=new Reactor(port);
        new Thread(reactor, "NIO-MultiplexerTimeServer-001").start();
        System.out.printf("The time server is start in port: %d\n", port);
    }
}
