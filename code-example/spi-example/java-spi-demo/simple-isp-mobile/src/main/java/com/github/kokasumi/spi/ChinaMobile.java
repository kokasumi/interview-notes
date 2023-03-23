package com.github.kokasumi.spi;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/22 15:13
 * @description
 * @since v1.0
 */
public class ChinaMobile implements InternetService{
    @Override
    public void connectInternet() {
        System.out.println("connect internet by [China Mobile]");
    }
}
