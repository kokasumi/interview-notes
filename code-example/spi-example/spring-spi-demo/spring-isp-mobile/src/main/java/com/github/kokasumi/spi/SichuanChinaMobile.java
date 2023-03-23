package com.github.kokasumi.spi;

/**
 * @version v1.0
 * @author: lg
 * @date: 2023/3/22 15:14
 * @description
 * @since v1.0
 */
public class SichuanChinaMobile implements InternetService{
    public SichuanChinaMobile(){}

    @Override
    public void connectInternet() {
        System.out.println("connect internet by [Sichuan China Mobile]");
    }
}
