package com.zbensoft.e.payment.api.common.gif;

import java.io.*;
public class Test{
public static void main(String[] args) throws FileNotFoundException
    {
//        Captcha captcha = new SpecCaptcha(150,40,5);// png格式验证码
////        captcha.out(new FileOutputStream("D:/Users/pengpeng/Desktop/aa/aa.png"));
        Captcha captcha = new GifCaptcha(150,40,5);//   gif格式动画验证码
        captcha.out(new FileOutputStream("C:/Wangchenyang/tmp/imgs/aa.gif"));
        System.out.println(captcha.text());
    }
}