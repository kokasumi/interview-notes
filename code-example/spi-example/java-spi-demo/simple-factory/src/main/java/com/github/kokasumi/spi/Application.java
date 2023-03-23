package com.github.kokasumi.spi;

import java.util.ServiceLoader;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/22 15:17
 * @description
 * @since v1.0
 */
public class Application {
    public static void main(String[] args) {
        ServiceLoader<InternetService> loader = ServiceLoader.load(InternetService.class);
        loader.forEach(t -> t.connectInternet());
    }
}
