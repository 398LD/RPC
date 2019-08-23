package com.kexun.server;

import com.kexun.entity.Student;
import com.kexun.entity.StudentImpl;

public class MainApp {

    public static void main(String[] args) {

        ServerCenter serverCenter = new ServerCenterImpl();

        //注册一个服务
        serverCenter.registry(Student.class.getName(), StudentImpl.class);

        //启动服务
        serverCenter.start();

    }

}
