package com.activiti;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * @author zhangy
 * @date 2021/12/10 15:22
 * @apiNote:
 */
public class JasyptTest {

    private static final String ALGORITHM_INFO = "PBEWithMD5AndDES";
    private static final String PASSWORD_INFO = "EbfYkitulv73I2p0mXI50JMXoaxZTKJ0";

        public static void main(String[] args) {
            StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
            //配置文件中配置如下的算法
            standardPBEStringEncryptor.setAlgorithm(ALGORITHM_INFO);
            //配置文件中配置的password
            standardPBEStringEncryptor.setPassword(PASSWORD_INFO);
            //加密后的文本
            System.out.println("mysql username:" + standardPBEStringEncryptor.encrypt("root"));
            System.out.println("mysql password:" + standardPBEStringEncryptor.encrypt("Whjrjd@99!"));
            System.out.println(standardPBEStringEncryptor.decrypt("Haq+RL2YGt46RA0C0azEDr+WM5N2IDoL"));

        }

}
