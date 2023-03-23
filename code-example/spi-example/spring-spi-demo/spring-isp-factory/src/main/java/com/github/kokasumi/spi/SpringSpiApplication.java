package com.github.kokasumi.spi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/23 17:19
 * @description
 * @since v1.0
 */
@SpringBootApplication
public class SpringSpiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSpiApplication.class,args);
        List<InternetService> internetServices = SpringFactoriesLoader.loadFactories(InternetService.class,
                Thread.currentThread().getContextClassLoader());
        internetServices.forEach(t -> t.connectInternet());
    }
}
