package com.kexun.proxy;

import com.kexun.entity.Student;

import java.net.InetSocketAddress;

public class MainApp {


    public static void main(String[] args) {
        //获取动态代理对象
        Student student = (Student) ClientProxy.getProxy(Student.class, new InetSocketAddress("127.0.0.1", 8888));


        //调用学生学习
        student.lern();

        //调用学生说话
        String tall = student.tall();

        System.out.println(tall);


    }

}
